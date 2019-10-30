package com.fantasticsource.tiamatrpgmain;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

import java.lang.reflect.Field;
import java.util.List;

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

    public static boolean tryAttack(EntityPlayerMP player, Entity target) throws IllegalAccessException
    {
        if (recursive)
        {
            //Only return whether this attack succeeds, and take no other action
            target.setGlowing(true);
            ServerTickTimer.schedule(60, () -> target.setGlowing(false));

            return false;
        }
        else
        {
            recursive = true;


            //Rough box
            double range = MCTools.getAttribute(player, Attributes.MELEE_DISTANCE, Attributes.MELEE_DISTANCE.getDefaultValue());
            List<Entity> entityList = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(range));

            //Distance, accounting for range and entity widths
            entityList.removeIf(e -> e.getPositionVector().addVector(0, e.height / 2, 0).squareDistanceTo(player.getPositionVector().addVector(0, player.height / 2, 0)) > Math.pow(range + (player.width + e.width) / 2, 2));

            //Angle
            //TODO

            //LOS
            //TODO

            //Attack sequence
            int lastSwingTime = (int) entityLivingBaseTicksSinceLastSwingField.get(player);
            for (Entity entity : entityList)
            {
                player.attackTargetEntityWithCurrentItem(entity);
                entityLivingBaseTicksSinceLastSwingField.set(player, lastSwingTime);
            }
            player.resetCooldown();


            recursive = false;


            //Always return false for non-recursive mode; it's the recursive mode which handles the final checks
            return false;
        }
    }
}
