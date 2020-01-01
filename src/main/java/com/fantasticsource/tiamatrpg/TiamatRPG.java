package com.fantasticsource.tiamatrpg;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.tiamatrpg.config.server.items.AffixesConfig;
import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryGUI;
import com.fantasticsource.tiamatrpg.inventory.TiamatItems;
import com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

@Mod(modid = TiamatRPG.MODID, name = TiamatRPG.NAME, version = TiamatRPG.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.026b,)")
public class TiamatRPG
{
    public static final String MODID = "tiamatrpg";
    public static final String NAME = "Tiamat RPG";
    public static final String VERSION = "1.12.2.000";

    public TiamatRPG()
    {
        Attributes.init();
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IllegalAccessException, IOException
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(TiamatRPG.class);
        MinecraftForge.EVENT_BUS.register(AffixesConfig.class);
        AffixesConfig.init();
        MinecraftForge.EVENT_BUS.register(CustomMouseHandler.class);
        MinecraftForge.EVENT_BUS.register(Attacks.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            Keys.init(event);
            MinecraftForge.EVENT_BUS.register(Keys.class);
            MinecraftForge.EVENT_BUS.register(TiamatInventoryGUI.class);
            Attributes.clientInit(event);
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
        Attributes.configChanged(event);
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
        CustomMouseHandler.unload(event);
    }

    @Mod.EventHandler
    public static void serverStop(FMLServerStoppedEvent event)
    {
        TiamatPlayerInventory.saveUnloadAll(event);
        CustomMouseHandler.unloadAll(event);
    }
//
//    @SubscribeEvent
//    public static void test(PlayerEvent.PlayerLoggedInEvent event)
//    {
//        System.out.println(1);
//        ServerTickTimer.schedule(10, () ->
//        {
//            System.out.println(2);
//            ItemStack stack = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().get(0).getHeldItemMainhand();
//            TiamatItems.setTwoHanded(stack, true);
//        });
//    }
}
