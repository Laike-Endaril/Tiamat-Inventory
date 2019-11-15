package com.fantasticsource.tiamatrpgmain;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Quaternion;

import java.lang.reflect.Field;
import java.util.List;

import static com.fantasticsource.mctools.MCTools.TRIG_TABLE;
import static com.fantasticsource.tiamatrpgmain.Attributes.*;
import static net.minecraft.entity.SharedMonsterAttributes.KNOCKBACK_RESISTANCE;

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

    public static boolean tryAttack(EntityPlayerMP attacker, Class filter) throws IllegalAccessException
    {
        if (recursive) return true;


        recursive = true;


        //Possible hit count
        double hitsRemaining = MCTools.getAttribute(attacker, MELEE_TARGETS);
        if (hitsRemaining <= 0) return false;


        //Cube distance check
        double range = MCTools.getAttribute(attacker, MELEE_BEST_DISTANCE) + MCTools.getAttribute(attacker, MELEE_TOLERANCE);
        List<Entity> entityList = attacker.world.getEntitiesWithinAABBExcludingEntity(attacker, attacker.getEntityBoundingBox().grow(range));
        if (entityList.size() == 0) return false;


        double angle = MCTools.getAttribute(attacker, MELEE_ANGLE);
        Vec3d playerEyes = attacker.getPositionVector().addVector(0, attacker.eyeHeight, 0);
        ExplicitPriorityQueue<Entity> queue = new ExplicitPriorityQueue<>(entityList.size());

        for (Entity target : entityList)
        {
            //Filter check
            if (filter != null && !filter.isAssignableFrom(target.getClass())) continue;

            //Spherical distance check
            Vec3d targetCenter = target.getPositionVector().addVector(0, target.height / 2, 0);
            double squareDist = playerEyes.squareDistanceTo(targetCenter);
            if (squareDist > Math.pow(range + (attacker.width + target.width) / 2, 2)) continue;

            //Succeed if player center is within target sphere
            if (squareDist < Math.pow(target.width / 2, 2))
            {
                queue.add(target, squareDist);
                continue;
            }

            //Succeed if direct raytrace along player vision line hits
            if (ImprovedRayTracing.entityPenetration(attacker, range, target, true) >= 0)
            {
                queue.add(target, squareDist);
                continue;
            }

            //Succeed if direct raytrace along axis between sphere centers is within attack angle and hits
            double angleDif = MCTools.lookAngleDifDeg(attacker, target);
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
            MCTools.spawnDebugSnowball(attacker.world, playerEyes.x, playerEyes.y, playerEyes.z);
            double distance = Math.sqrt(squareDist);
            double subConeStep = Tools.radtodeg(TRIG_TABLE.arctan(DISTRIBUTED_RAYTRACE_SPACING / distance));
            int subConeCount = (int) (angle / subConeStep);
            subConeStep = angle / subConeCount;
            double subConeAngle = subConeStep;

            Vec3d pitchYaw = Vec3d.fromPitchYaw(0, attacker.rotationYawHead + 90);
            Quaternion qPitchAxis = new Quaternion((float) pitchYaw.x, (float) pitchYaw.y, (float) pitchYaw.z, 0);
            pitchYaw = Vec3d.fromPitchYaw(attacker.rotationPitch, attacker.rotationYawHead);
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
        int lastSwingTime = (int) entityLivingBaseTicksSinceLastSwingField.get(attacker);
        while (queue.size() > 0)
        {
            Entity target = queue.poll();


            if (target instanceof EntityLivingBase)
            {
                EntityLivingBase targetLivingBase = (EntityLivingBase) target;

                //Block
                if (Math.random() < MCTools.getAttribute(targetLivingBase, BLOCK))
                {
                    //TODO make knockback use damage amount (need to do damage first!)
                    knockback(targetLivingBase, attacker, 100);
                    //TODO particle effect

                    //Stop the attack
                    break;
                }

                //Parry
                if (Math.random() < MCTools.getAttribute(targetLivingBase, PARRY))
                {
                    //TODO 1/2 second root
                    //TODO halve damage

                    //Don't damage this player, but do count this player against hit count
                    if (--hitsRemaining <= 0) break;
                    continue;
                }

                //Dodge
                if (Math.random() < MCTools.getAttribute(targetLivingBase, DODGE))
                {
                    //TODO 1 second of 2x movement speed

                    //Don't damage this player
                    continue;
                }
            }

            attacker.attackTargetEntityWithCurrentItem(target);
            entityLivingBaseTicksSinceLastSwingField.set(attacker, lastSwingTime);
            if (--hitsRemaining <= 0) break;
        }
        attacker.resetCooldown();


        recursive = false;


        return false;
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackBlock(PlayerInteractEvent.LeftClickBlock event) throws IllegalAccessException
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player instanceof EntityPlayerMP && Attacks.tryAttack((EntityPlayerMP) player, EntityLivingBase.class)) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackAir(PlayerInteractEvent.LeftClickEmpty event)
    {
        //This event normally only happens client-side; need to send to server
        Network.WRAPPER.sendToServer(new Network.LeftClickEmptyPacket());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackEntity(AttackEntityEvent event) throws IllegalAccessException
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player instanceof EntityPlayerMP && !Attacks.tryAttack((EntityPlayerMP) player, EntityLivingBase.class)) event.setCanceled(true);
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damageEntity(LivingHurtEvent event)
    {
        DamageSource source = event.getSource();
        Entity entity = source.getTrueSource();
        if (!(entity instanceof EntityLivingBase)) entity = source.getImmediateSource();
        if (!(entity instanceof EntityLivingBase) || entity.world.isRemote) return;

        EntityLivingBase attacker = (EntityLivingBase) entity;
        switch ((int) MCTools.getAttribute(attacker, MELEE_MODE))
        {
            case 1:
            {
                double dist = attacker.getDistance(event.getEntityLiving());
                double optimum = MCTools.getAttribute(attacker, MELEE_BEST_DISTANCE);
                double dif = Math.abs(dist - optimum);
                if (dif > MCTools.getAttribute(attacker, MELEE_TOLERANCE))
                {
                    event.setAmount((float) (event.getAmount() * MCTools.getAttribute(attacker, MELEE_MULTIPLIER_BAD)));
                }
                else
                {
                    event.setAmount((float) (event.getAmount() * MCTools.getAttribute(attacker, MELEE_MULTIPLIER_GOOD)));
                }
                break;
            }

            case 2:
            {
                double dist = attacker.getDistance(event.getEntityLiving());
                double optimum = MCTools.getAttribute(attacker, MELEE_BEST_DISTANCE);
                double dif = Math.abs(dist - optimum);
                double tolerance = MCTools.getAttribute(attacker, MELEE_TOLERANCE);
                if (dif > tolerance)
                {
                    event.setAmount((float) (event.getAmount() * MCTools.getAttribute(attacker, MELEE_MULTIPLIER_BAD)));
                }
                else
                {
                    double ratio = 1 - (dif / tolerance);
                    double multiplier = MCTools.getAttribute(attacker, MELEE_MULTIPLIER_BAD) + MCTools.getAttribute(attacker, MELEE_MULTIPLIER_GOOD) * ratio;
                    event.setAmount((float) (event.getAmount() * multiplier));
                }
                break;
            }
        }
        //If mode is not set, just do full damage at all distances in range
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void vanillaKnockback(LivingKnockBackEvent event)
    {
        event.setCanceled(true);
    }

    public static void knockback(EntityLivingBase target, Entity source, double force)
    {
        knockback(target, source, source != null ? target.getPositionVector().subtract(source.getPositionVector()) : new Vec3d(0, 0, 0).subtract(target.getLookVec()), force);
    }

    public static void knockback(EntityLivingBase target, Entity source, double xRatio, double yRatio, double zRatio, double force)
    {
        knockback(target, source, new Vec3d(xRatio, yRatio, zRatio), force);
    }

    public static void knockback(EntityLivingBase target, Entity source, Vec3d directionVector, double force)
    {
        //TODO add source's knockback bonus

        double ratio = 1 - MCTools.getAttribute(target, KNOCKBACK_RESISTANCE);
        if (ratio <= 0) return;

        Vec3d motion = new Vec3d(target.motionX, target.motionY, target.motionZ);
        System.out.println(target.getName());
        System.out.println(motion);
        if (!target.onGround) force *= 2;
        else motion.scale(0.5);

        target.isAirBorne = true;

        motion = motion.add(directionVector.normalize().scale(force * ratio));
        System.out.println(motion);
        System.out.println();

        target.motionX = motion.x;
        target.motionY = motion.y;
        target.motionZ = motion.z;
    }
}
