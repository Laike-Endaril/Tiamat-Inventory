package com.fantasticsource.tiamatrpg;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.LinkedHashMap;
import java.util.UUID;

public class CustomMouseHandler
{
    public static LinkedHashMap<UUID, Integer> playerControlModifiers = new LinkedHashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackEntity(AttackEntityEvent event) throws IllegalAccessException
    {
        EntityPlayer player = event.getEntityPlayer();

        if (!(player instanceof EntityPlayerMP))
        {
            //Client
            if (Keys.controlModifier == 1) Keys.skillset1Locked = true;
            else if (Keys.controlModifier == 2) Keys.skillset2Locked = true;

            Keys.controlModifier = 0;
            Keys.skillset1Pressed = false;
            Keys.skillset2Pressed = false;
            event.setCanceled(true);

            KeyBinding.setKeyBindState(-100, false);

            return;
        }


        //Server
        if (Attacks.tiamatAttackActive) return; //Allow subattacks to pass through normally

        event.setCanceled(true);
        customClickAction((EntityPlayerMP) player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackBlock(PlayerInteractEvent.LeftClickBlock event) throws IllegalAccessException
    {
        EntityPlayer player = event.getEntityPlayer();
        if (!(player instanceof EntityPlayerMP))
        {
            //Client
            if (Keys.controlModifier != 0 || player.inventory.currentItem == 0)
            {
                if (Keys.controlModifier == 1) Keys.skillset1Locked = true;
                else if (Keys.controlModifier == 2) Keys.skillset2Locked = true;

                Keys.controlModifier = 0;
                Keys.skillset1Pressed = false;
                Keys.skillset2Pressed = false;
                event.setCanceled(true);

                KeyBinding.setKeyBindState(-100, false);
            }

            return;
        }


        //Server
        if (customClickAction((EntityPlayerMP) player)) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void attackAir(PlayerInteractEvent.LeftClickEmpty event)
    {
        //This event normally only happens client-side; need to send to server
        EntityPlayer player = event.getEntityPlayer();
        if (Keys.controlModifier != 0 || player.inventory.currentItem == 0)
        {
            if (Keys.controlModifier == 1) Keys.skillset1Locked = true;
            else if (Keys.controlModifier == 2) Keys.skillset2Locked = true;

            Keys.controlModifier = 0;
            Keys.skillset1Pressed = false;
            Keys.skillset2Pressed = false;

            KeyBinding.setKeyBindState(-100, false);

            Network.WRAPPER.sendToServer(new Network.LeftClickEmptyPacket());
        }
    }


    public static void unload(PlayerEvent.PlayerLoggedOutEvent event)
    {
        playerControlModifiers.remove(event.player.getPersistentID());
    }

    public static void unloadAll(FMLServerStoppedEvent event)
    {
        playerControlModifiers.clear();
    }

    public static int getControlModifier(EntityPlayerMP player)
    {
        Integer integer = playerControlModifiers.get(player.getPersistentID());
        return integer != null ? integer : 0;
    }


    public static boolean customClickAction(EntityPlayerMP player) throws IllegalAccessException
    {
        switch (getControlModifier(player))
        {
            case 1:
                //TODO
                System.out.println("Skill 1");
                playerControlModifiers.remove(player.getPersistentID());
                return true;

            case 2:
                //TODO
                System.out.println("Skill 4");
                playerControlModifiers.remove(player.getPersistentID());
                return true;

            case 0:
            default:
                if (player.inventory.currentItem == 0)
                {
                    Attacks.tiamatAttack(player, EntityLivingBase.class);
                    return true;
                }
                return false;
        }
    }
}
