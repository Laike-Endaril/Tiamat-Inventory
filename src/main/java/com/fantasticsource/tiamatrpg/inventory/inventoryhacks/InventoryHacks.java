package com.fantasticsource.tiamatrpg.inventory.inventoryhacks;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatrpg.Network;
import com.fantasticsource.tiamatrpg.config.TiamatConfig;
import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryContainer;
import com.fantasticsource.tiamatrpg.nbt.SlotDataTags;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InventoryHacks
{
    protected static final Field CONTAINER_LISTENERS_FIELD = ReflectionTool.getField(Container.class, "listeners");

    public static final int[] ORDERED_SLOT_INDICES = new int[]
            {
                    9, 18, 27,
                    10, 19, 28,
                    11, 20, 29,
                    12, 21, 30,
                    13, 22, 31,
                    14, 23, 32,
                    15, 24, 33,
                    16, 25, 34,
                    17, 26, 35
            };

    public static int clientInventorySize = TiamatConfig.serverSettings.defaultInventorySize;

    @SideOnly(Side.CLIENT)
    public static ArrayList<Integer> getAvailableClientInventorySlots()
    {
        ArrayList<Integer> result = new ArrayList<>(clientInventorySize);
        for (int i = 0; i < clientInventorySize; i++) result.add(ORDERED_SLOT_INDICES[i]);
        return result;
    }

    public static int getCurrentInventorySize(EntityPlayerMP player)
    {
        int slotCount = TiamatConfig.serverSettings.defaultInventorySize;
        for (ItemStack stack : GlobalInventory.getAllNonSkinItems(player))
        {
            slotCount += SlotDataTags.getInvSlotCount(stack);
        }
        return slotCount;
    }

    @SubscribeEvent
    public static void playerContainer(PlayerContainerEvent.Open event)
    {
        int invSize = getCurrentInventorySize((EntityPlayerMP) event.getEntityPlayer());
        ArrayList<Integer> availableSlots = new ArrayList<>(invSize);
        for (int i = 0; i < invSize; i++)
        {
            availableSlots.add(ORDERED_SLOT_INDICES[i]);
        }

        Container container = event.getContainer();
        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null || !(slot.inventory instanceof InventoryPlayer)) continue;

            int slotIndex = slot.getSlotIndex();
            if (container instanceof TiamatInventoryContainer)
            {
                if (slotIndex > 0 && (slotIndex < 9 || (slotIndex < 36 && !availableSlots.contains(slotIndex))))
                {
                    container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                }
            }
            else
            {
                if (slotIndex < 9 || slotIndex >= 36 || !availableSlots.contains(slotIndex))
                {
                    container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                }
            }
        }
    }

    @SubscribeEvent
    public static void itemPickup1(EntityItemPickupEvent event)
    {
        ItemStack stack = event.getItem().getItem();
        int oldCount = stack.getCount();
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();

        int[] availableSlots = new int[getCurrentInventorySize(player)];
        System.arraycopy(ORDERED_SLOT_INDICES, 0, availableSlots, 0, availableSlots.length);
        autoplaceItem(event.getEntityPlayer(), stack, availableSlots);
        if (!stack.isEmpty()) event.setCanceled(true);

        int pickedUpCount = oldCount - stack.getCount();
        if (pickedUpCount > 0)
        {
            player.addStat(StatList.getObjectsPickedUpStats(stack.getItem()), pickedUpCount);

            for (EntityPlayer trackingPlayer : ((WorldServer) player.world).getEntityTracker().getTrackingPlayers(event.getItem()))
            {
                Network.WRAPPER.sendTo(new Network.PickupSoundPacket(player.posX, player.posY, player.posZ), (EntityPlayerMP) trackingPlayer);
            }

            event.getItem().setItem(event.getItem().getItem());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) entity;
        InventoryPlayer playerInventory = player.inventory;

        int slotCount = getCurrentInventorySize(player);
        int[] availableSlots = new int[Tools.min(Tools.max(slotCount, 0), 27)];
        System.arraycopy(ORDERED_SLOT_INDICES, 0, availableSlots, 0, availableSlots.length);

        boolean changed = false;
        //Completely blocked hotbar slots (all except first)
        for (int i = 1; i < 9; i++)
        {
            ItemStack stack = playerInventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                autoplaceItem(player, stack, availableSlots);
                if (!stack.isEmpty())
                {
                    ItemStack copy = stack.copy();
                    stack.setCount(0);
                    player.entityDropItem(copy, 0);
                }
                changed = true;
            }
        }
        //Completely blocked "cargo" slots
        int[] blockedSlots = new int[Tools.min(Tools.max(27 - slotCount, 0), 27)];
        System.arraycopy(ORDERED_SLOT_INDICES, slotCount, blockedSlots, 0, blockedSlots.length);
        for (int i : blockedSlots)
        {
            ItemStack stack = playerInventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                autoplaceItem(player, stack, availableSlots);
                if (!stack.isEmpty())
                {
                    ItemStack copy = stack.copy();
                    stack.setCount(0);
                    player.entityDropItem(copy, 0);
                }
                changed = true;
            }
        }

        if (changed)
        {
            for (IContainerListener listener : (List<IContainerListener>) ReflectionTool.get(CONTAINER_LISTENERS_FIELD, player.openContainer))
            {
                listener.sendAllContents(player.openContainer, player.openContainer.inventoryItemStacks);
            }
        }
    }

    protected static void autoplaceItem(EntityPlayer player, ItemStack stack, int[] availableSlots)
    {
        InventoryPlayer playerInventory = player.inventory;

        ArrayList<Integer> emptySlots = new ArrayList<>();
        for (int i : availableSlots)
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
    }
}