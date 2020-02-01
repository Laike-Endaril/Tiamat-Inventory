package com.fantasticsource.tiamatrpg;

import com.fantasticsource.tiamatrpg.config.server.items.AffixesConfig;
import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryGUI;
import com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

@Mod(modid = TiamatRPG.MODID, name = TiamatRPG.NAME, version = TiamatRPG.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.033a,)")
public class TiamatRPG
{
    public static final String MODID = "tiamatrpg";
    public static final String NAME = "Tiamat RPG";
    public static final String VERSION = "1.12.2.000b";


    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IllegalAccessException, IOException
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(TiamatRPG.class);
        MinecraftForge.EVENT_BUS.register(AffixesConfig.class);
        AffixesConfig.init();
        MinecraftForge.EVENT_BUS.register(ControlHandler.class);
        MinecraftForge.EVENT_BUS.register(Attacks.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            Keys.init(event);
            MinecraftForge.EVENT_BUS.register(Keys.class);
            MinecraftForge.EVENT_BUS.register(TiamatInventoryGUI.class);
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void syncConfig(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        Attributes.updateDisplayList(event);
    }


    @Mod.EventHandler
    public static void serverStart(FMLServerStartingEvent event)
    {
        TiamatPlayerInventory.init(event);
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        TiamatPlayerInventory.load(event);
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        TiamatPlayerInventory.saveUnload(event);
    }

    @Mod.EventHandler
    public static void serverStop(FMLServerStoppedEvent event)
    {
        TiamatPlayerInventory.saveUnloadAll(event);
    }
}
