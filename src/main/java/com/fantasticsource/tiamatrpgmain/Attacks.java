package com.fantasticsource.tiamatrpgmain;

import net.minecraft.entity.player.EntityPlayerMP;

public class Attacks
{
    private static boolean recursive = false;

    public static boolean tryAttack(EntityPlayerMP player, boolean isAttackEvent)
    {
        if (recursive)
        {
            //Only return whether this attack succeeds, and take no other action
        }
        else
        {
            recursive = true;

            //Do stuff, and initiate attacks on each possible target until the attack is ended (due to no more targets, a blocked attack, etc)

            recursive = false;

            //Return
        }
    }
}
