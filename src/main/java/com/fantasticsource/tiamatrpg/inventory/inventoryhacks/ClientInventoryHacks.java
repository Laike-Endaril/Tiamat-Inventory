package com.fantasticsource.tiamatrpg.inventory.inventoryhacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientInventoryHacks
{
    @SubscribeEvent
    public static void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        Gui gui = event.getGui();
        if (!(gui instanceof GuiContainer)) return;

        event.getButtonList().add(new GuiFakeSlots((GuiContainer) gui));
    }

    @SubscribeEvent
    public static void guiOpen(GuiOpenEvent event)
    {
        Gui gui = event.getGui();
        if (!(gui instanceof GuiContainer)) return;

        Minecraft mc = Minecraft.getMinecraft();
        GuiContainer guiContainer = (GuiContainer) gui;
        for (int i = 0; i < guiContainer.inventorySlots.inventorySlots.size(); i++)
        {
            Slot slot = guiContainer.inventorySlots.inventorySlots.get(i);
            if (slot == null || !slot.isHere(mc.player.inventory, slot.getSlotIndex())) continue;

            int slotIndex = slot.getSlotIndex();
            if (slotIndex < 9 || slotIndex == 40)
            {
                guiContainer.inventorySlots.inventorySlots.set(i, new FakeSlot(mc.player.inventory, slotIndex, slot.xPos, slot.yPos));
            }
        }
    }
}
