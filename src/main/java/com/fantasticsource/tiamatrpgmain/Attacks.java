package com.fantasticsource.tiamatrpgmain;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public class Attacks
{
    private static boolean recursive = false;

    public static boolean tryAttack(EntityPlayerMP player, Entity target)
    {
        if (recursive)
        {
            //Only return whether this attack succeeds, and take no other action
            System.out.println("Recursive: " + target.getName());
            return false;
        }
        else
        {
            recursive = true;

            List<Entity> entityList = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(MCTools.getAttribute(player, Attributes.MELEE_DISTANCE, Attributes.MELEE_DISTANCE.getDefaultValue())));
            System.out.println("Non-Recursive: " + entityList.size());

            for (Entity entity : entityList)
            {
                player.attackTargetEntityWithCurrentItem(entity);
            }
            //Do calcs, and initiate attacks on each possible target until the attack is ended (due to no more targets, a blocked attack, etc)

            recursive = false;

            //Always return false for non-recursive mode; it's the recursive mode which handles the final checks
            return false;
        }
    }
}
