package com.fantasticsource.tiamatinventory;

import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryGUI;
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

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding
            INVENTORY = new KeyBinding(TiamatInventory.MODID + ".key.inventory", KeyConflictContext.UNIVERSAL, Keyboard.KEY_R, TiamatInventory.MODID + ".keyCategory"),
            SWAP = new KeyBinding(TiamatInventory.MODID + ".key.swap", KeyConflictContext.IN_GAME, Keyboard.KEY_Q, TiamatInventory.MODID + ".keyCategory"),
            SHEATHE = new KeyBinding(TiamatInventory.MODID + ".key.sheathe", KeyConflictContext.IN_GAME, Keyboard.KEY_E, TiamatInventory.MODID + ".keyCategory");


    public static void init(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(Keys.class);

        for (KeyBinding keyBinding : new KeyBinding[]{INVENTORY, SWAP, SHEATHE}) ClientRegistry.registerKeyBinding(keyBinding);
    }

    @SubscribeEvent
    public static void keyPress(InputEvent event)
    {
        if (INVENTORY.isKeyDown())
        {
            Minecraft.getMinecraft().getTutorial().openInventory();
            Minecraft.getMinecraft().displayGuiScreen(new TiamatInventoryGUI());
            Network.WRAPPER.sendToServer(new Network.OpenTiamatInventoryPacket());
        }
        else if (SHEATHE.isKeyDown())
        {
            Network.WRAPPER.sendToServer(new Network.SheatheUnsheathePacket());
        }
        else if (SWAP.isKeyDown())
        {
            Network.WRAPPER.sendToServer(new Network.SwapWeaponsetsPacket());
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
