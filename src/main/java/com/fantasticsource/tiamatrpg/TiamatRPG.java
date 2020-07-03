package com.fantasticsource.tiamatrpg;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryGUI;
import com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory;
import com.fantasticsource.tiamatrpg.inventory.inventoryhacks.ClientInventoryHacks;
import com.fantasticsource.tiamatrpg.inventory.inventoryhacks.InventoryHacks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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

@Mod(modid = TiamatRPG.MODID, name = TiamatRPG.NAME, version = TiamatRPG.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.034zq,);required-after:tiamatitems@[1.12.2.000b,);required-after:tiamatactions@[1.12.2.000,)")
public class TiamatRPG
{
    public static final String MODID = "tiamatrpg";
    public static final String NAME = "Tiamat RPG";
    public static final String VERSION = "1.12.2.000m";


    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(TiamatRPG.class);
        MinecraftForge.EVENT_BUS.register(Attacks.class);
        MinecraftForge.EVENT_BUS.register(InventoryHacks.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            AttributeDisplayData.updateDisplayList();
            Keys.init(event);
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
            //Cape Default
            if (RenderModes.getRenderMode(entity, "CapeInvControl") == null) RenderModes.setRenderMode(entity, "CapeInvControl", "On");

            //Shoulders Default
            if (RenderModes.getRenderMode(entity, "ShoulderLControl") == null) RenderModes.setRenderMode(entity, "ShoulderLControl", "On");
            if (RenderModes.getRenderMode(entity, "ShoulderRControl") == null) RenderModes.setRenderMode(entity, "ShoulderRControl", "On");


            //Cape
            ItemStack cape = GlobalInventory.getTiamatCapeItem(entity);
            if (cape == null || cape.isEmpty()) RenderModes.setRenderMode(entity, "CapeInv", "Off");
            else RenderModes.setRenderMode(entity, "CapeInv", RenderModes.getRenderMode(entity, "CapeInvControl"));

            //Shoulders
            ItemStack shoulder = GlobalInventory.getTiamatShoulderItem(entity);
            if (shoulder == null || shoulder.isEmpty())
            {
                RenderModes.setRenderMode(entity, "ShoulderL", "Off");
                RenderModes.setRenderMode(entity, "ShoulderR", "Off");
            }
            else
            {
                RenderModes.setRenderMode(entity, "ShoulderL", RenderModes.getRenderMode(entity, "ShoulderLControl"));
                RenderModes.setRenderMode(entity, "ShoulderR", RenderModes.getRenderMode(entity, "ShoulderRControl"));
            }
        }
    }

    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        Entity entity = event.getEntity();
        if (!entity.world.isRemote && entity instanceof EntityLivingBase && Loader.isModLoaded("armourers_workshop"))
        {
            //Cape
            ItemStack cape = GlobalInventory.getTiamatCapeItem(entity);
            if (cape == null || cape.isEmpty()) RenderModes.setRenderMode(entity, "CapeInv", "Off");
            else RenderModes.setRenderMode(entity, "CapeInv", RenderModes.getRenderMode(entity, "CapeInvControl"));

            //Shoulders
            ItemStack shoulder = GlobalInventory.getTiamatShoulderItem(entity);
            if (shoulder == null || shoulder.isEmpty())
            {
                RenderModes.setRenderMode(entity, "ShoulderL", "Off");
                RenderModes.setRenderMode(entity, "ShoulderR", "Off");
            }
            else
            {
                RenderModes.setRenderMode(entity, "ShoulderL", RenderModes.getRenderMode(entity, "ShoulderLControl"));
                RenderModes.setRenderMode(entity, "ShoulderR", RenderModes.getRenderMode(entity, "ShoulderRControl"));
            }
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
