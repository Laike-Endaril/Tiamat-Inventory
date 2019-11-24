package com.fantasticsource.tiamatrpg;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomMouseHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackEntity(AttackEntityEvent event) throws IllegalAccessException
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player.inventory.currentItem != 0 || !(player instanceof EntityPlayerMP) || !Attacks.tryAttack((EntityPlayerMP) player, EntityLivingBase.class)) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackBlock(PlayerInteractEvent.LeftClickBlock event) throws IllegalAccessException
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player.inventory.currentItem == 0)
        {
            event.setCanceled(true);
            if (player instanceof EntityPlayerMP) Attacks.tryAttack((EntityPlayerMP) player, EntityLivingBase.class);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackAir(PlayerInteractEvent.LeftClickEmpty event)
    {
        //This event normally only happens client-side; need to send to server
        EntityPlayer player = event.getEntityPlayer();
        if (player.inventory.currentItem == 0) Network.WRAPPER.sendToServer(new Network.LeftClickEmptyPacket());
    }
}
