package com.fantasticsource.tiamatrpg;

import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryGUI;
import com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory;
import com.fantasticsource.tiamatrpg.inventory.inventoryhacks.ClientInventoryHacks;
import com.fantasticsource.tiamatrpg.inventory.inventoryhacks.InventoryHacks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TiamatRPG.MODID, name = TiamatRPG.NAME, version = TiamatRPG.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.034q,);required-after:tiamatitems@[1.12.2.000b,);required-after:tiamatactions@[1.12.2.000,)")
public class TiamatRPG
{
    public static final String MODID = "tiamatrpg";
    public static final String NAME = "Tiamat RPG";
    public static final String VERSION = "1.12.2.000g";


    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(TiamatRPG.class);
        MinecraftForge.EVENT_BUS.register(ControlHandler.class);
        MinecraftForge.EVENT_BUS.register(Attacks.class);
        MinecraftForge.EVENT_BUS.register(InventoryHacks.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            AttributeDisplayData.updateDisplayList();
            Keys.init(event);
            MinecraftForge.EVENT_BUS.register(Keys.class);
            MinecraftForge.EVENT_BUS.register(TiamatInventoryGUI.class);
            MinecraftForge.EVENT_BUS.register(ClientInventoryHacks.class);
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
        AttributeDisplayData.updateDisplayList();
    }


    @Mod.EventHandler
    public static void serverStart(FMLServerStartingEvent event)
    {
        TiamatPlayerInventory.init(event);
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        TiamatPlayerInventory.load(event);
        Network.WRAPPER.sendTo(new Network.InventorySizePacket(InventoryHacks.getCurrentInventorySize(player)), player);
    }

    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (!entity.world.isRemote && entity instanceof EntityLivingBase && Loader.isModLoaded("armourers_workshop"))
        {
            if (RenderModes.getRenderMode(entity, "CapeInv") == null) RenderModes.setRenderMode(entity, "CapeInv", "On");
            if (RenderModes.getRenderMode(entity, "ShoulderL") == null) RenderModes.setRenderMode(entity, "ShoulderL", "On");
            if (RenderModes.getRenderMode(entity, "ShoulderR") == null) RenderModes.setRenderMode(entity, "ShoulderR", "On");
        }
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
