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
import org.lwjgl.util.vector.Quaternion;

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
        double range = MCTools.getAttribute(player, MELEE_RANGE, MELEE_RANGE.getDefaultValue());
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


            //Don't do the "shotgun check" if we're only attacking in a line and not a cone
            if (angle == 0) continue;


            //Final check: "shotgun" check (cone of distributed raytraces) (mostly useful for detection vs. large mobs)
            //Find evenly distributed points on evenly distributed subcones
            //Transforms are: player yaw, player pitch, roll (theta along circular intersection of cone and sphere), subConeAngle(angle of current cone)
            MCTools.spawnDebugSnowball(player.world, playerEyes.x, playerEyes.y, playerEyes.z);
            double distance = Math.sqrt(squareDist);
            double subConeStep = Tools.radtodeg(TRIG_TABLE.arctan(DISTRIBUTED_RAYTRACE_SPACING / distance));
            int subConeCount = (int) (angle / subConeStep);
            subConeStep = angle / subConeCount;
            double subConeAngle = subConeStep;

            Vec3d pitchYaw = Vec3d.fromPitchYaw(0, player.rotationYawHead + 90);
            Quaternion qPitchAxis = new Quaternion((float) pitchYaw.x, (float) pitchYaw.y, (float) pitchYaw.z, 0);
            pitchYaw = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYawHead);
            Quaternion qPitchYaw = new Quaternion((float) pitchYaw.x, (float) pitchYaw.y, (float) pitchYaw.z, 0);

            boolean stop = false;
            for (int cone = 0; cone < subConeCount; cone++)
            {
                double radius = distance * TRIG_TABLE.sin(Tools.degtorad(subConeAngle));
                double rollStep = Math.PI * radius * 2 / DISTRIBUTED_RAYTRACE_SPACING;
                int thetaStepCount = Tools.max((int) rollStep + 1, 4);
                rollStep = Math.PI * 2 / thetaStepCount;
                double roll = rollStep;
                Quaternion theta0 = MCTools.rotatedQuaternion(qPitchYaw, qPitchAxis, Tools.degtorad(subConeAngle));

                for (int thetaStepI = 0; thetaStepI < thetaStepCount; thetaStepI++)
                {
                    //Final calc, using roll and subConeAngle


                    Quaternion qRotated = MCTools.rotatedQuaternion(theta0, qPitchYaw, roll);
                    qRotated.scale((float) distance);
                    Vec3d pos = new Vec3d(qRotated.x, qRotated.y, qRotated.z).add(playerEyes);

                    if (ImprovedRayTracing.entityPenetration(target, playerEyes, pos, true) > 0)
                    {
                        queue.add(target, squareDist);
                        stop = true;
                        break;
                    }

                    roll += rollStep;
                }

                if (stop) break;

                subConeAngle += subConeStep;
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
