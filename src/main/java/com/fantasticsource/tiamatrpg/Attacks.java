package com.fantasticsource.tiamatrpg;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;

import static net.minecraft.entity.SharedMonsterAttributes.KNOCKBACK_RESISTANCE;

public class Attacks
{
    private static Field entityLivingBaseTicksSinceLastSwingField = ReflectionTool.getField(EntityLivingBase.class, "field_184617_aD", "ticksSinceLastSwing");
    public static boolean tiamatAttackActive = false;


    //TODO old code for cone attacks
//    public static void tiamatAttack(EntityPlayerMP attacker, Class filter) throws IllegalAccessException
//    {
//        //Possible hit count
//        double hitsRemaining = MCTools.getAttribute(attacker, MELEE_TARGETS);
//        if (hitsRemaining <= 0) return;
//
//
//        //Cube distance check
//        double range = MCTools.getAttribute(attacker, MELEE_BEST_DISTANCE) + MCTools.getAttribute(attacker, MELEE_TOLERANCE);
//        List<Entity> entityList = attacker.world.getEntitiesWithinAABBExcludingEntity(attacker, attacker.getEntityBoundingBox().grow(range));
//        if (entityList.size() == 0) return;
//
//
//        double angle = MCTools.getAttribute(attacker, MELEE_ANGLE);
//        Vec3d playerEyes = attacker.getPositionVector().addVector(0, attacker.eyeHeight, 0);
//        ExplicitPriorityQueue<Entity> queue = new ExplicitPriorityQueue<>(entityList.size());
//
//        //Filter check
//        entityList.removeIf(target -> filter != null && !filter.isAssignableFrom(target.getClass()));
//
//        //Cone check
//        entityList = Arrays.asList(MCTools.withinCone(playerEyes, attacker.rotationYawHead, attacker.rotationPitch, range, angle, true, entityList.toArray(new Entity[0])));
//        if (entityList.size() == 0) return;
//
//
//        //Attack sequence
//        int lastSwingTime = (int) entityLivingBaseTicksSinceLastSwingField.get(attacker);
//        while (queue.size() > 0)
//        {
//            Entity target = queue.poll();
//
//
//            if (target instanceof EntityLivingBase)
//            {
//                //Block
//                if (false)
//                {
//                    //TODO make knockback use damage amount (need to do damage calcs first!)
//                    knockback((EntityLivingBase) target, attacker, 100);
//                    //TODO particle effect
//
//                    //Stop the attack
//                    break;
//                }
//            }
//
//            tiamatAttackActive = true;
//            attacker.attackTargetEntityWithCurrentItem(target);
//            tiamatAttackActive = false;
//
//            entityLivingBaseTicksSinceLastSwingField.set(attacker, lastSwingTime);
//            if (--hitsRemaining <= 0) break;
//        }
//        attacker.resetCooldown();
//    }


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
        if (!target.onGround) force *= 2;
        else motion.scale(0.5);

        target.isAirBorne = true;

        motion = motion.add(directionVector.normalize().scale(force * ratio));

        target.motionX = motion.x;
        target.motionY = motion.y;
        target.motionZ = motion.z;
    }


    //TODO old code for altering damage
//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public static void damageEntity(LivingHurtEvent event)
//    {
//        DamageSource source = event.getSource();
//        Entity entity = source.getTrueSource();
//        if (!(entity instanceof EntityLivingBase)) entity = source.getImmediateSource();
//        if (!(entity instanceof EntityLivingBase) || entity.world.isRemote) return;
//
//        EntityLivingBase attacker = (EntityLivingBase) entity;
//        switch ((int) MCTools.getAttribute(attacker, MELEE_MODE))
//        {
//            case 1:
//            {
//                double dist = attacker.getDistance(event.getEntityLiving());
//                double optimum = MCTools.getAttribute(attacker, MELEE_BEST_DISTANCE);
//                double dif = Math.abs(dist - optimum);
//                if (dif > MCTools.getAttribute(attacker, MELEE_TOLERANCE))
//                {
//                    event.setAmount((float) (event.getAmount() * MCTools.getAttribute(attacker, MELEE_MULTIPLIER_BAD)));
//                }
//                else
//                {
//                    event.setAmount((float) (event.getAmount() * MCTools.getAttribute(attacker, MELEE_MULTIPLIER_GOOD)));
//                }
//                break;
//            }
//
//            case 2:
//            {
//                double dist = attacker.getDistance(event.getEntityLiving());
//                double optimum = MCTools.getAttribute(attacker, MELEE_BEST_DISTANCE);
//                double dif = Math.abs(dist - optimum);
//                double tolerance = MCTools.getAttribute(attacker, MELEE_TOLERANCE);
//                if (dif > tolerance)
//                {
//                    event.setAmount((float) (event.getAmount() * MCTools.getAttribute(attacker, MELEE_MULTIPLIER_BAD)));
//                }
//                else
//                {
//                    double ratio = 1 - (dif / tolerance);
//                    double multiplier = MCTools.getAttribute(attacker, MELEE_MULTIPLIER_BAD) + MCTools.getAttribute(attacker, MELEE_MULTIPLIER_GOOD) * ratio;
//                    event.setAmount((float) (event.getAmount() * multiplier));
//                }
//                break;
//            }
//        }
//        //If mode is not set, just do full damage at all distances in range
//    }
}
