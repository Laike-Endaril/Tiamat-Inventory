package com.fantasticsource.tiamatinventory.inventory.inventoryhacks;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatinventory.Network;
import com.fantasticsource.tiamatinventory.config.TiamatConfig;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer;
import com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.nbt.SlotDataTags;
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
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer.*;

public class InventoryHacks
{
    protected static final Field CONTAINER_LISTENERS_FIELD = ReflectionTool.getField(Container.class, "field_75149_d", "listeners");

    public static int clientInventorySize = TiamatConfig.serverSettings.defaultInventorySize;
    public static boolean clientAllowHotbar = TiamatConfig.serverSettings.allowHotbar;

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
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        GameType gameType = MCTools.getGameType(event.player);
        if (TiamatConfig.serverSettings.allowHotbar || gameType == null || gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) return;

        event.player.inventory.currentItem = 0;
    }

    @SubscribeEvent
    public static void playerContainer(PlayerContainerEvent.Open event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        GameType gameType = MCTools.getGameType(player);
        if (gameType == null || gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) return;


        Container container = event.getContainer();
        TiamatPlayerInventory inventory = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());
        if (inventory != null && !inventory.forceEmptyHands())
        {
            player.closeScreen();
            return;
        }


        int invSize = getCurrentInventorySize(player);
        HashMap<Integer, Integer> tiamatSlotToCurrentSlot = new HashMap<>();
        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null || !(slot.inventory instanceof InventoryPlayer)) continue;

            int slotIndex = slot.getSlotIndex();
            if (container instanceof TiamatInventoryContainer)
            {
                if (slotIndex < 9 && !TiamatConfig.serverSettings.allowHotbar)
                {
                    container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                }
                else if (slotIndex >= 9 + invSize && slotIndex < 36)
                {
                    container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                }
            }
            else
            {
                if (slotIndex < 9 && !TiamatConfig.serverSettings.allowHotbar)
                {
                    if (inventory != null && slotIndex < 4)
                    {
                        tiamatSlotToCurrentSlot.put(slotIndex, i);
                    }
                }
                else if (slotIndex >= 9 + invSize && slotIndex < 36)
                {
                    container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                }
            }
        }

        if (inventory != null)
        {
            for (Map.Entry<Integer, Integer> entry : tiamatSlotToCurrentSlot.entrySet())
            {
                int tiamatIndex = entry.getKey(), currentIndex = entry.getValue();
                int pairedIndex = tiamatIndex % 2 == 0 ? tiamatIndex + 1 : tiamatIndex - 1;

                Slot oldSlot = container.inventorySlots.get(currentIndex);
                Slot newSlot = new FilteredSlot(inventory, tiamatIndex, oldSlot.xPos, oldSlot.yPos, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0, false, WEAPON_SLOT_STACK_LIMIT, stack ->
                {
                    ItemStack other = inventory.getStackInSlot(pairedIndex);
                    return other.isEmpty() || (!Slottings.isTwoHanded(stack) && !Slottings.isTwoHanded(other));
                });
                newSlot.slotNumber = oldSlot.slotNumber;
                container.inventorySlots.set(currentIndex, newSlot);
            }
        }
    }

    @SubscribeEvent
    public static void itemPickup(EntityItemPickupEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        GameType gameType = MCTools.getGameType(player);
        if (gameType == null || gameType == GameType.SPECTATOR) return;


        ItemStack stack = event.getItem().getItem();
        int oldCount = stack.getCount();


        autoPickup(player, stack);
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
        GameType gameType = MCTools.getGameType(player);
        if (gameType == null || gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) return;


        InventoryPlayer playerInventory = player.inventory;

        int slotCount = getCurrentInventorySize(player);

        boolean changed = false;

        //Completely blocked hotbar slots (all except first, unless hotbar is enabled)
        if (!TiamatConfig.serverSettings.allowHotbar)
        {
            for (int i = 1; i < 9; i++)
            {
                ItemStack stack = playerInventory.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    autoPickup(player, stack);
                    if (!stack.isEmpty())
                    {
                        ItemStack copy = stack.copy();
                        stack.setCount(0);
                        player.entityDropItem(copy, 0);
                    }
                    changed = true;
                }
            }
        }

        //Completely blocked "cargo" slots
        for (int i = 9 + getCurrentInventorySize(player); i < 36; i++)
        {
            ItemStack stack = playerInventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                autoPickup(player, stack);
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

    protected static void autoPickup(EntityPlayerMP player, ItemStack stack)
    {
        ArrayList<Integer> slotOrder = new ArrayList<>();
        if (player.isCreative() || TiamatConfig.serverSettings.allowPickupMainHand) slotOrder.add(player.inventory.currentItem);
        if (player.isCreative() || TiamatConfig.serverSettings.allowPickupOffhand) slotOrder.add(40);
        if (player.isCreative() || TiamatConfig.serverSettings.allowPickupHotbar)
        {
            for (int i = 1; i <= 8; i++)
            {
                slotOrder.add(Tools.posMod(i + player.inventory.currentItem, 9));
            }
        }
        if (player.isCreative() || TiamatConfig.serverSettings.allowPickupCargo)
        {
            for (int i = getCurrentInventorySize(player) + 8; i >= 9; i--) slotOrder.add(i);
        }


        InventoryPlayer playerInventory = player.inventory;

        //Fill slots that already have the same type of item
        for (int i : slotOrder)
        {
            ItemStack stack2 = playerInventory.getStackInSlot(i);
            if (stack2.isEmpty()) continue;

            int moveAmount = Tools.min(stack2.getMaxStackSize() - stack2.getCount(), stack.getCount());
            if (moveAmount > 0 && ItemMatcher.stacksMatch(stack, stack2))
            {
                stack.shrink(moveAmount);
                stack2.grow(moveAmount);
                if (stack.isEmpty()) break;
            }
        }

        //Fill empty slots
        if (!stack.isEmpty())
        {
            int max = stack.getMaxStackSize();
            for (int i : slotOrder)
            {
                if (!playerInventory.getStackInSlot(i).isEmpty()) continue;

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
