package com.fantasticsource.tiamatrpgmain;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.List;

import static com.fantasticsource.mctools.MCTools.TRIG_TABLE;
import static com.fantasticsource.tiamatrpgmain.Attributes.*;

public class Attacks
{
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
            double distance = Math.sqrt(squareDist);
            double subConeStep = Tools.radtodeg(TRIG_TABLE.arctan(0.5 / distance));
            int subConeCount = (int) (angle / subConeStep);
            subConeStep = angle / subConeCount;
            double angleFromAxis = subConeStep;
            for (int cone = 0; cone < subConeCount; cone++)
            {
                double radius = distance * TRIG_TABLE.sin(angleFromAxis);
                double thetaStep = Math.PI * radius * 2;
                int thetaStepCount = (int) thetaStep + 1;
                thetaStep = 360d / thetaStepCount;
                double theta = thetaStep;
                for (int thetaStepI = 0; thetaStepI < thetaStepCount; thetaStepI++)
                {
                    //TODO Final calc, using theta and angleFromAxis
                    //TODO Since this is a full, unordered 360* rotation around the cone perimeter, +/- and which one uses sin/cos doesn't really matter, but I might want to check them sometime and put the "right" calc in anyway
                    double yaw = player.rotationYawHead + (angleFromAxis * TRIG_TABLE.sin(theta));
                    double pitch = player.rotationPitch + (angleFromAxis * TRIG_TABLE.cos(theta));

                    Vec3d pos = Vec3d.fromPitchYaw((float) pitch, (float) yaw).scale(distance).add(playerEyes);
                    EntitySnowball snowball = new EntitySnowball(player.world, pos.x, pos.y, pos.z);
                    snowball.setVelocity(0, 0, 0);
                    snowball.setNoGravity(true);
                    player.world.spawnEntity(snowball);


                    theta += thetaStep;
                }


                angleFromAxis += subConeStep;
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
