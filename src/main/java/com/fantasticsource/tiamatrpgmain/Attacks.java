package com.fantasticsource.tiamatrpgmain;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.List;

import static com.fantasticsource.mctools.MCTools.TRIG_TABLE;
import static com.fantasticsource.tiamatrpgmain.Attributes.*;

public class Attacks
{
    private static final double DISTRIBUTED_RAYTRACE_SPACING = 0.5;

    private static Field entityLivingBaseTicksSinceLastSwingField;
    private static boolean recursive = false;

    static
    {
        try
        {
            entityLivingBaseTicksSinceLastSwingField = ReflectionTool.getField(EntityLivingBase.class, "field_184617_aD", "ticksSinceLastSwing");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean tryAttack(EntityPlayerMP player, Class filter) throws IllegalAccessException
    {
        if (recursive) return true;


        recursive = true;


        //Possible hit count
        double hitsRemaining = MCTools.getAttribute(player, MELEE_TARGETS, MELEE_TARGETS.getDefaultValue());
        if (hitsRemaining <= 0) return false;


        //Cube distance check
        double range = MCTools.getAttribute(player, MELEE_DISTANCE, MELEE_DISTANCE.getDefaultValue());
        List<Entity> entityList = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(range));
        if (entityList.size() == 0) return false;


        double angle = MCTools.getAttribute(player, MELEE_ANGLE, MELEE_ANGLE.getDefaultValue());
        Vec3d playerEyes = player.getPositionVector().addVector(0, player.eyeHeight, 0);
        ExplicitPriorityQueue<Entity> queue = new ExplicitPriorityQueue<>(entityList.size());

        for (Entity target : entityList)
        {
            //Filter check
            if (filter != null && !filter.isAssignableFrom(target.getClass())) continue;

            //Spherical distance check
            Vec3d targetCenter = target.getPositionVector().addVector(0, target.height / 2, 0);
            double squareDist = playerEyes.squareDistanceTo(targetCenter);
            if (squareDist > Math.pow(range + (player.width + target.width) / 2, 2)) continue;

            //Succeed if player center is within target sphere
            if (squareDist < Math.pow(target.width / 2, 2))
            {
                queue.add(target, squareDist);
                continue;
            }

            //Succeed if direct raytrace along player vision line hits
            if (ImprovedRayTracing.entityPenetration(player, range, target, true) >= 0)
            {
                queue.add(target, squareDist);
                continue;
            }

            //Succeed if direct raytrace along axis between sphere centers is within attack angle and hits
            double angleDif = MCTools.lookAngleDifDeg(player, target);
            if (angleDif <= angle && ImprovedRayTracing.entityPenetration(target, playerEyes, target.getPositionVector(), true) >= 0)
            {
                queue.add(target, squareDist);
                continue;
            }


            //Final check: "shotgun" check (cone of distributed raytraces) (mostly useful for detection vs. large mobs)
            //Find evenly distributed points on evenly distributed subcones
            //Transforms are: player yaw, player pitch, roll (theta along circular intersection of cone and sphere), pitch2(angle of current cone)
            MCTools.spawnDebugSnowball(player.world, playerEyes.x, playerEyes.y, playerEyes.z);
            double distance = Math.sqrt(squareDist);
            double pitch2Step = Tools.radtodeg(TRIG_TABLE.arctan(DISTRIBUTED_RAYTRACE_SPACING / distance));
            int subConeCount = (int) (angle / pitch2Step);
            pitch2Step = angle / subConeCount;
            double pitch2 = pitch2Step;

            for (int cone = 0; cone < subConeCount; cone++)
            {
                double radius = distance * TRIG_TABLE.sin(Tools.degtorad(pitch2));
                double rollStep = Math.PI * radius * 2 / DISTRIBUTED_RAYTRACE_SPACING;
                int thetaStepCount = Tools.max((int) rollStep + 1, 4);
                rollStep = Math.PI * 2 / thetaStepCount;
                double roll = rollStep;

                for (int thetaStepI = 0; thetaStepI < thetaStepCount; thetaStepI++)
                {
                    //Final calc, using roll and pitch2
                    double flatYawOffset = pitch2 * -TRIG_TABLE.cos(roll);
                    double flatPitchOffset = pitch2 * -TRIG_TABLE.sin(roll);
                    double yaw = player.rotationYawHead + flatYawOffset * TRIG_TABLE.cos(Tools.degtorad(flatPitchOffset));
                    double pitch = (player.rotationPitch + flatPitchOffset) * TRIG_TABLE.cos(Tools.degtorad(flatYawOffset));
                    if (pitch > 90)
                    {
                        pitch = 180 - pitch;
                        yaw += 180;
                    }
                    else if (pitch < -90)
                    {
                        pitch = -180 - pitch;
                        yaw += 180;
                    }

                    Vec3d pos = Vec3d.fromPitchYaw((float) pitch, (float) yaw).scale(distance).add(playerEyes);
                    System.out.println(pos);
                    MCTools.spawnDebugSnowball(player.world, pos.x, pos.y, pos.z);


                    roll += rollStep;
                }


                pitch2 += pitch2Step;
            }


            //I feel a wall between us (or we're facing the wrong direction, etc)
        }
        if (entityList.size() == 0) return false;


        //Attack sequence
        int lastSwingTime = (int) entityLivingBaseTicksSinceLastSwingField.get(player);
        while (queue.size() > 0)
        {
            Entity target = queue.poll();


            //TODO check for block, deflect, and dodge


            player.attackTargetEntityWithCurrentItem(target);
            entityLivingBaseTicksSinceLastSwingField.set(player, lastSwingTime);
            if (--hitsRemaining <= 0) break;
        }
        player.resetCooldown();


        recursive = false;


        return false;
    }
}
