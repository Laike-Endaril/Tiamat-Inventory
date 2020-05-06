package com.fantasticsource.tiamatrpg.inventory.inventoryhacks;

import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;

public class InventoryHacks
{
    @SubscribeEvent
    public static void playerContainer(PlayerContainerEvent.Open event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        Container container = event.getContainer();
        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null || !slot.isHere(mc.player.inventory, slot.getSlotIndex())) continue;

            int slotIndex = slot.getSlotIndex();
            if (slotIndex < 9 || slotIndex == 40)
            {
                container.inventorySlots.set(i, new FakeSlot(mc.player.inventory, slotIndex, slot.xPos, slot.yPos));
            }
        }
    }

    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayer)) return;

        InventoryPlayer playerInventory = ((EntityPlayer) entity).inventory;
        for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 40})
        {
            ItemStack stack = playerInventory.getStackInSlot(i);
            if (!stack.isEmpty()) autoplaceItem((EntityPlayer) entity, stack);
        }
    }

    @SubscribeEvent
    public static void itemPickup(PlayerEvent.ItemPickupEvent event)
    {
        ItemStack stack = event.getStack();
        if (stack.isEmpty()) return;

        autoplaceItem(event.player, stack);
    }

    protected static void autoplaceItem(EntityPlayer player, ItemStack stack)
    {
        InventoryPlayer playerInventory = player.inventory;

        ArrayList<Integer> emptySlots = new ArrayList<>();
        for (int i = 9; i < 36; i++)
        {
            ItemStack stack2 = playerInventory.getStackInSlot(i);
            if (stack2.isEmpty())
            {
                emptySlots.add(i);
                continue;
            }

            int moveAmount = Tools.min(stack2.getMaxStackSize() - stack2.getCount(), stack.getCount());
            if (moveAmount > 0 && ItemMatcher.stacksMatch(stack, stack2))
            {
                stack.shrink(moveAmount);
                stack2.grow(moveAmount);
                if (stack.isEmpty()) break;
            }
        }

        if (!stack.isEmpty())
        {
            int max = stack.getMaxStackSize();
            for (int i : emptySlots)
            {
                ItemStack copy = stack.copy();
                int moveAmount = Tools.min(max, stack.getCount());
                stack.shrink(moveAmount);
                copy.setCount(moveAmount);
                playerInventory.setInventorySlotContents(i, copy);

                if (stack.isEmpty()) break;
            }
        }

        if (!stack.isEmpty())
        {
            ItemStack copy = stack.copy();
            stack.setCount(0);
            player.entityDropItem(copy, 0);
        }
    }
}
