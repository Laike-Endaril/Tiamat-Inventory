package com.fantasticsource.tiamatinventory.inventory;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.inventory.slot.BetterSlot;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import com.fantasticsource.tools.Tools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class TiamatInventoryContainer extends Container
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "gui/inventory.png");
    public static final int TEXTURE_W = 1024, TEXTURE_H = 1024;

    public static final int WEAPON_SLOT_STACK_LIMIT = 64;
    protected final EntityPlayer player;

    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public InventoryCraftResult craftResult = new InventoryCraftResult();

    public TiamatInventoryContainer(EntityPlayer player)
    {
        this.player = player;
        InventoryPlayer playerInventory = player.inventory;
        TiamatPlayerInventory tiamatPlayerInventory;
        if (player.world.isRemote)
        {
            tiamatPlayerInventory = TiamatPlayerInventory.tiamatClientInventory;
            if (tiamatPlayerInventory == null)
            {
                tiamatPlayerInventory = new TiamatPlayerInventory(player);
                TiamatPlayerInventory.tiamatClientInventory = tiamatPlayerInventory;
            }
        }
        else
        {
            tiamatPlayerInventory = TiamatPlayerInventory.tiamatServerInventories.computeIfAbsent(player.getPersistentID(), o -> new TiamatPlayerInventory(player));
        }

        //Weaponset 1
        //Index 0 - 1
        //Internal index 0 and 1 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 0, 61, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0, false, WEAPON_SLOT_STACK_LIMIT, stack -> getSlot(1).getStack().isEmpty() || (!isTwoHanded(stack) && !isTwoHanded(getSlot(1).getStack()))));
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 1, 43, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 624, 0, false, WEAPON_SLOT_STACK_LIMIT, stack -> getSlot(0).getStack().isEmpty() || (!isTwoHanded(stack) && !isTwoHanded(getSlot(0).getStack()))));

        //Weaponset 2
        //Index 2 - 3
        //Internal index 2 and 3 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 2, 97, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0, false, WEAPON_SLOT_STACK_LIMIT, stack -> getSlot(3).getStack().isEmpty() || (!isTwoHanded(stack) && !isTwoHanded(getSlot(3).getStack()))));
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 3, 79, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 624, 0, false, WEAPON_SLOT_STACK_LIMIT, stack -> getSlot(2).getStack().isEmpty() || (!isTwoHanded(stack) && !isTwoHanded(getSlot(2).getStack()))));

        //"Cargo" inventory
        //Index 4 - 30
        //Internal index 9 - 35 (vanilla)
        for (int yy = 0; yy < 3; ++yy)
        {
            for (int xx = 0; xx < 9; ++xx)
            {
                addSlotToContainer(new BetterSlot(playerInventory, xx + (yy + 1) * 9, 133 + xx * 18, 60 + yy * 18, TEXTURE, TEXTURE_W, TEXTURE_H, -16, -16));
            }
        }

        //Armor slots
        //Index 31 - 36
        //Internal indices...
        //...39 (vanilla head)
        //...4 (tiamat shoulders)
        //...5 (tiamat cape)
        //...38 (vanilla chest)
        //...37 (vanilla legs)
        //...36 (vanilla feet)
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.HEAD, 39, 25, 6, 512, 0);
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 4, 25, 24, TEXTURE, TEXTURE_W, TEXTURE_H, 528, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Shoulders", player)));
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 5, 25, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 544, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Cape", player)));
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.CHEST, 38, 25, 60, 560, 0);
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.LEGS, 37, 25, 78, 576, 0);
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.FEET, 36, 25, 96, 592, 0);

        //Quick slots
        //Index 37 - 39
        //Internal index 6 - 8 (tiamat)
        for (int xx = 0; xx < 3; xx++)
        {
            addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 6 + xx, 169 + xx * 18, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 784, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Quick Item", player)));
        }

        //Backpack slot
        //Index 40
        //Internal index 9 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 9, 133, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 768, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Backpack", player)));

        //Pet slot
        //Index 41
        //Internal index 10 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 10, 223, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 640, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Pet", player)));

        //Deck slot
        //Index 42
        //Internal index 11 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 11, 241, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 752, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Deck", player)));

        //Hotbar slots (for creative usage or if hotbar is enabled)
        //Index 43 - 51
        //Internal index 0 - 8 (vanilla)
        for (int xx = 0; xx < 9; ++xx)
        {
            addSlotToContainer(new BetterSlot(playerInventory, xx, 133 + xx * 18, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0));
        }

        //Vanilla offhand slot
        //Index 52
        //Internal index 40 (vanilla)
        addSlotToContainer(new BetterSlot(playerInventory, 40, 115, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 624, 0));

        //Crafting result slot
        //Index 53
        //Internal index 0 (crafting result)
        addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 277, 42));

        //Crafting matrix slots
        //Index 54 - 57
        //Internal index 0-3 (crafting matrix)
        for (int i = 0; i < 2; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                addSlotToContainer(new Slot(craftMatrix, j + i * 2, 259 + j * 18, 6 + i * 18));
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        craftResult.clear();
        if (!player.world.isRemote) clearContainer(player, player.world, craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        slotChangedCraftingGrid(player.world, player, craftMatrix, craftResult);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != craftResult && super.canMergeSlot(stack, slotIn);
    }

    public static boolean isTwoHanded(ItemStack stack)
    {
        return stack.getItem() == Items.BOW || GlobalInventory.getItemSlotting(stack).equals("Tiamat 2H");
    }

    public static boolean canCombine(ItemStack from, ItemStack to)
    {
        if (from.isEmpty()) return false;
        if (to.isEmpty()) return true;
        return from.getItem() == to.getItem() && (!to.getHasSubtypes() || to.getMetadata() == from.getMetadata()) && ItemStack.areItemStackTagsEqual(to, from);
    }

    private void addVanillaEquipmentSlot(IInventory inventory, EntityEquipmentSlot slotEnum, int index, int x, int y, int u, int v)
    {
        addSlotToContainer(new BetterSlot(inventory, index, x, y, TEXTURE, TEXTURE_W, TEXTURE_H, u, v)
        {
            public int getSlotStackLimit()
            {
                return 1;
            }

            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem().isValidArmor(stack, slotEnum, player);
            }

            public boolean canTakeStack(EntityPlayer playerIn)
            {
                ItemStack itemstack = getStack();
                return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack));
            }

            @Nullable
            @SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return ItemArmor.EMPTY_SLOT_NAMES[slotEnum.getIndex()];
            }
        });
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    //Returning ItemStack.EMPTY from this method indicates that we are done with the transfer.  Mine always finishes in one go, so it always returns ItemStack.EMPTY
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;


        ItemStack stack = slot.getStack();

        //TODO redo or remove; indices are old ones and would need to be changed, hotbar is no longer accessible, etc
//        if (index <= 3)
//        {
//            //From any weaponset slot
//            //To main inventory or hotbar, in that order
//            tryMergeItemStackRanges(stack, 12, 38);
//            tryMergeItemStackRanges(stack, 4, 11);
//        }
//        else if (index <= 11)
//        {
//            //From hotbar
//            //To any armor slot, if applicable, or to a weaponset slot or main inventory otherwise
//
//            tryMergeItemStackRanges(stack, 39, 44);
//            tryMergeItemStackRanges(stack, 0, 3);
//            tryMergeItemStackRanges(stack, 12, 38);
//        }
//        else if (index <= 38)
//        {
//            //From main inventory
//            //To any equipment slot, if applicable, or to a weaponset slot or hotbar otherwise
//            tryMergeItemStackRanges(stack, 39, 44);
//            tryMergeItemStackRanges(stack, 0, 3);
//            tryMergeItemStackRanges(stack, 4, 11);
//        }
//        else if (index <= 44)
//        {
//            //From armor slots
//            //To main inventory, hotbar, or weaponset slot, in that order
//            tryMergeItemStackRanges(stack, 12, 38);
//            tryMergeItemStackRanges(stack, 4, 11);
//            tryMergeItemStackRanges(stack, 0, 3);
//        }


        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.putStack(stack);

        return ItemStack.EMPTY;
    }

    public void tryMergeItemStackRanges(ItemStack stackFrom, int... ranges)
    {
        if (stackFrom.isEmpty()) return;


        for (int ii = 0; ii < ranges.length; ii += 2)
        {
            int startIndex = ranges[ii];
            int endIndex = ranges[ii + 1];

            if (startIndex <= endIndex)
            {
                for (int i = startIndex; i <= endIndex; i++)
                {
                    tryMergeItemStack(stackFrom, i);
                }
            }
            else
            {
                for (int i = startIndex; i >= endIndex; i--)
                {
                    tryMergeItemStack(stackFrom, i);
                }
            }
        }
    }

    protected void tryMergeItemStack(ItemStack stackFrom, int slotIDTo)
    {
        Slot slotTo = this.inventorySlots.get(slotIDTo);
        if (!slotTo.isItemValid(stackFrom)) return;

        ItemStack stackTo = slotTo.getStack();
        if (!canCombine(stackFrom, stackTo)) return;

        int limit = Tools.min(slotTo.getSlotStackLimit(), !stackTo.isEmpty() ? stackTo.getMaxStackSize() : stackFrom.getMaxStackSize());

        int moveAmount = Tools.min(stackFrom.getCount(), limit - stackTo.getCount());

        if (stackTo.isEmpty())
        {
            stackTo = stackFrom.copy();
            stackTo.setCount(moveAmount);
            slotTo.putStack(stackTo);
        }
        else
        {
            stackTo.grow(moveAmount);
        }

        stackFrom.shrink(moveAmount);

        slotTo.onSlotChanged();
    }
}
