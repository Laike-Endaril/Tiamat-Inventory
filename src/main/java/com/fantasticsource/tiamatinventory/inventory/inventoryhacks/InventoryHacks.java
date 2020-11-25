package com.fantasticsource.tiamatinventory.inventory.inventoryhacks;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatinventory.Network;
import com.fantasticsource.tiamatinventory.TiamatInventory;
import com.fantasticsource.tiamatinventory.config.TiamatConfig;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer;
import com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.nbt.SlotDataTags;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
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
import java.util.*;

import static com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer.*;

public class InventoryHacks
{
    protected static final Field CONTAINER_LISTENERS_FIELD = ReflectionTool.getField(Container.class, "field_75149_d", "listeners");


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
        EntityPlayer player = event.player;
        GameType gameType = MCTools.getGameType(player);
        if (gameType == null || gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) return;


        if (!TiamatInventory.playerHasHotbar(player)) player.inventory.currentItem = 0;

        if (player instanceof EntityPlayerMP) dropItemsInBlockedSlots((EntityPlayerMP) player);
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


        HashSet<Integer> allowedCraftingSlots = new HashSet<>();
        for (int x = 0; x < TiamatConfig.serverSettings.craftW; x++)
        {
            for (int y = 0; y < TiamatConfig.serverSettings.craftH; y++)
            {
                //Expand from bottom-left
                allowedCraftingSlots.add(6 + x - y * 3);
            }
        }


        //Limit slots
        int invSize = getCurrentInventorySize(player);
        HashMap<Integer, Integer> tiamatSlotToCurrentSlot = new HashMap<>();
        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null) continue;


            int slotIndex = slot.getSlotIndex();
            if (container instanceof TiamatInventoryContainer)
            {
                if (slot.inventory instanceof InventoryCraftResult)
                {
                    if (TiamatConfig.serverSettings.craftW == 0 || TiamatConfig.serverSettings.craftH == 0)
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                }
                else if (slot.inventory instanceof InventoryCrafting)
                {
                    if (!allowedCraftingSlots.contains(slotIndex))
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                }
                else if (slot.inventory instanceof InventoryPlayer)
                {
                    if (slotIndex < 9 && !TiamatInventory.playerHasHotbar(player))
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                    else if (slotIndex >= 9 + invSize && slotIndex < 36)
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                }
            }
            else
            {
                if (slotIndex < 9 && !TiamatInventory.playerHasHotbar(player))
                {
                    if (inventory != null)
                    {
                        if (slotIndex < 4) tiamatSlotToCurrentSlot.put(slotIndex, i);
                        else if (slotIndex < 7) tiamatSlotToCurrentSlot.put(slotIndex, i + 33);
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) entity;
        GameType gameType = MCTools.getGameType(player);
        if (gameType == null || gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) return;

        dropItemsInBlockedSlots(player);
    }

    public static void dropItemsInBlockedSlots(EntityPlayerMP player)
    {
        InventoryPlayer playerInventory = player.inventory;

        boolean changed = false;

        //Blocked hotbar slots (all except first, unless hotbar is enabled)
        if (!TiamatInventory.playerHasHotbar(player))
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

        //Blocked "cargo" slots
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

        //If neither hand is empty and one of them is 2H, drop offhand
        ItemStack mainhand = player.getHeldItemMainhand(), offhand = player.getHeldItemOffhand();
        if (!mainhand.isEmpty() && !offhand.isEmpty() && (Slottings.isTwoHanded(mainhand) || Slottings.isTwoHanded(offhand)))
        {
            ItemStack copy = offhand.copy();
            offhand.setCount(0);
            player.entityDropItem(copy, 0);

            changed = true;
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
        InventoryPlayer vanilla = player.inventory;
        TiamatPlayerInventory tiamat = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());

        ArrayList<Pair<IInventory, Integer>> slotOrder = new ArrayList<>();
        ItemStack mainhand = player.getHeldItemMainhand(), offhand = player.getHeldItemOffhand();
        boolean stackIs2H = Slottings.isTwoHanded(stack);

        if (player.isCreative())
        {
            //Armor
            if (Slottings.slotTypeValidForItemstack(stack, "Head", player)) slotOrder.add(new Pair<>(vanilla, 39));
            if (Slottings.slotTypeValidForItemstack(stack, "Tiamat Shoulders", player)) slotOrder.add(new Pair<>(tiamat, 4));
            if (Slottings.slotTypeValidForItemstack(stack, "Tiamat Cape", player)) slotOrder.add(new Pair<>(tiamat, 5));
            if (Slottings.slotTypeValidForItemstack(stack, "Chest", player)) slotOrder.add(new Pair<>(vanilla, 38));
            if (Slottings.slotTypeValidForItemstack(stack, "Legs", player)) slotOrder.add(new Pair<>(vanilla, 37));
            if (Slottings.slotTypeValidForItemstack(stack, "Feet", player)) slotOrder.add(new Pair<>(vanilla, 36));

            //Vanilla mainhand
            slotOrder.add(new Pair<>(vanilla, player.inventory.currentItem));

            //Hotbar
            for (int i = 1; i <= 8; i++)
            {
                slotOrder.add(new Pair<>(vanilla, Tools.posMod(i + player.inventory.currentItem, 9)));
            }

            //Cargo
            int last = player.isCreative() ? 35 : getCurrentInventorySize(player) + 8;
            for (int i = 9; i <= last; i++) slotOrder.add(new Pair<>(vanilla, i));

            //Vanilla offhand
            slotOrder.add(new Pair<>(vanilla, 40));

            //Weaponsets
            for (int i = 0; i < 4; i++) slotOrder.add(new Pair<>(tiamat, i));
        }
        else
        {
            if (TiamatConfig.serverSettings.allowPickupArmor)
            {
                if (Slottings.slotTypeValidForItemstack(stack, "Head", player)) slotOrder.add(new Pair<>(vanilla, 39));
                if (Slottings.slotTypeValidForItemstack(stack, "Tiamat Shoulders", player)) slotOrder.add(new Pair<>(tiamat, 4));
                if (Slottings.slotTypeValidForItemstack(stack, "Tiamat Cape", player)) slotOrder.add(new Pair<>(tiamat, 5));
                if (Slottings.slotTypeValidForItemstack(stack, "Chest", player)) slotOrder.add(new Pair<>(vanilla, 38));
                if (Slottings.slotTypeValidForItemstack(stack, "Legs", player)) slotOrder.add(new Pair<>(vanilla, 37));
                if (Slottings.slotTypeValidForItemstack(stack, "Feet", player)) slotOrder.add(new Pair<>(vanilla, 36));
            }
            if (TiamatConfig.serverSettings.allowPickupMainHand && (offhand.isEmpty() || (!stackIs2H && !Slottings.isTwoHanded(offhand))))
            {
                slotOrder.add(new Pair<>(vanilla, player.inventory.currentItem));
            }
            if (TiamatConfig.serverSettings.allowPickupHotbar)
            {
                for (int i = 1; i <= 8; i++)
                {
                    slotOrder.add(new Pair<>(vanilla, Tools.posMod(i + player.inventory.currentItem, 9)));
                }
            }
            if (TiamatConfig.serverSettings.allowPickupCargo)
            {
                int last = player.isCreative() ? 35 : getCurrentInventorySize(player) + 8;
                for (int i = 9; i <= last; i++) slotOrder.add(new Pair<>(vanilla, i));
            }
            if (TiamatConfig.serverSettings.allowPickupOffhand && (mainhand.isEmpty() || (!stackIs2H && !Slottings.isTwoHanded(mainhand))))
            {
                slotOrder.add(new Pair<>(vanilla, 40));
            }
            if (TiamatConfig.serverSettings.allowPickupWeaponset)
            {
                if (tiamat.getSheathedOffhand1().isEmpty() || (!stackIs2H && !Slottings.isTwoHanded(tiamat.getSheathedOffhand1()))) slotOrder.add(new Pair<>(tiamat, 0));
                if (tiamat.getSheathedMainhand1().isEmpty() || (!stackIs2H && !Slottings.isTwoHanded(tiamat.getSheathedMainhand1()))) slotOrder.add(new Pair<>(tiamat, 1));
                if (tiamat.getSheathedOffhand2().isEmpty() || (!stackIs2H && !Slottings.isTwoHanded(tiamat.getSheathedOffhand2()))) slotOrder.add(new Pair<>(tiamat, 2));
                if (tiamat.getSheathedMainhand2().isEmpty() || (!stackIs2H && !Slottings.isTwoHanded(tiamat.getSheathedMainhand2()))) slotOrder.add(new Pair<>(tiamat, 3));
            }
        }


        //Fill slots that already have the same type of item in forward order, track whether any did (even if they were full), and remove them from the list if they're not empty
        Boolean[] found = new Boolean[]{false};
        slotOrder.removeIf(pair ->
        {
            ItemStack stack2 = pair.getKey().getStackInSlot(pair.getValue());
            if (stack2.isEmpty()) return false;

            if (ItemMatcher.stacksMatch(stack, stack2))
            {
                found[0] = true;

                int moveAmount = Tools.min(stack2.getMaxStackSize() - stack2.getCount(), stack.getCount());
                if (moveAmount > 0)
                {
                    stack.shrink(moveAmount);
                    stack2.grow(moveAmount);
                }
            }
            return true;
        });
        if (stack.isEmpty() || slotOrder.isEmpty()) return;


        //If none were already in inventory, fill up to one empty slot in forward order and remove it from the list
        int max = stack.getMaxStackSize();
        if (!found[0])
        {
            for (Pair<IInventory, Integer> pair : slotOrder)
            {
                IInventory inventory = pair.getKey();
                int slot = pair.getValue();

                if (!inventory.getStackInSlot(slot).isEmpty()) continue;

                ItemStack copy = stack.copy();
                int moveAmount = Tools.min(max, stack.getCount());
                stack.shrink(moveAmount);
                copy.setCount(moveAmount);
                inventory.setInventorySlotContents(slot, copy);

                //Only filling one slot max!
                slotOrder.remove(pair);
                break;
            }
            if (stack.isEmpty() || slotOrder.isEmpty()) return;
        }


        //Fill empty slots in reverse order
        for (int i = slotOrder.size() - 1; i >= 0; i--)
        {
            Pair<IInventory, Integer> pair = slotOrder.get(i);
            IInventory inventory = pair.getKey();
            int slot = pair.getValue();

            if (!inventory.getStackInSlot(slot).isEmpty()) continue;

            ItemStack copy = stack.copy();
            int moveAmount = Tools.min(max, stack.getCount());
            stack.shrink(moveAmount);
            copy.setCount(moveAmount);
            inventory.setInventorySlotContents(slot, copy);

            if (stack.isEmpty()) break;
        }
    }
}
