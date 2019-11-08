package com.fantasticsource.tiamatrpgmain;

import com.fantasticsource.tiamatrpgmain.config.server.items.AffixesConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

@Mod(modid = TiamatRPGMain.MODID, name = TiamatRPGMain.NAME, version = TiamatRPGMain.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.026b,)")
public class TiamatRPGMain
{
    public static final String MODID = "tiamatrpgmain";
    public static final String NAME = "Tiamat RPG - Main";
    public static final String VERSION = "1.12.2.000";

    public TiamatRPGMain()
    {
        Attributes.init();
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IllegalAccessException, IOException
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(TiamatRPGMain.class);
        MinecraftForge.EVENT_BUS.register(AffixesConfig.class);
        AffixesConfig.init();
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void entityConstructing(EntityEvent.EntityConstructing event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase)
        {
            //Add new attributes
            Attributes.addAttributes((EntityLivingBase) entity);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void entityJoin(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase)
        {
            //Edit existing attributes
            Attributes.editAttributes((EntityLivingBase) entity);
        }
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
}
