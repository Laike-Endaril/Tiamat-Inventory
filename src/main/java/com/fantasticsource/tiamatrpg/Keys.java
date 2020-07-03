package com.fantasticsource.tiamatrpg;

import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding
            TIAMAT_INVENTORY_KEY = new KeyBinding(MODID + ".key.inventory", KeyConflictContext.UNIVERSAL, Keyboard.KEY_R, MODID + ".keyCategory");


    public static void init(FMLPreInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(TIAMAT_INVENTORY_KEY);
        MinecraftForge.EVENT_BUS.register(Keys.class);
    }

    @SubscribeEvent
    public static void keyPress(InputEvent event)
    {
        if (TIAMAT_INVENTORY_KEY.isKeyDown())
        {
            Minecraft.getMinecraft().getTutorial().openInventory();
            Minecraft.getMinecraft().displayGuiScreen(new TiamatInventoryGUI());
            Network.WRAPPER.sendToServer(new Network.OpenTiamatInventoryPacket());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void interceptVanillaInventory(GuiOpenEvent event)
    {
        if (event.getGui() instanceof GuiInventory && !Minecraft.getMinecraft().player.isCreative())
        {
            Minecraft.getMinecraft().getTutorial().openInventory();
            event.setGui(new TiamatInventoryGUI());
            Network.WRAPPER.sendToServer(new Network.OpenTiamatInventoryPacket());
        }
    }
}
