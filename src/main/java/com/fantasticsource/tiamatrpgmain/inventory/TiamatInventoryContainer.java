package com.fantasticsource.tiamatrpgmain.inventory;

import com.fantasticsource.tools.Tools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TiamatInventoryContainer extends Container
{
    private final EntityPlayer player;

    public ArrayList<TexturedSlot> classTabSlots = new ArrayList<>(), professionTabSlots = new ArrayList<>();

    public TiamatInventoryContainer(EntityPlayer player)
    {
        this.player = player;
        InventoryPlayer playerInventory = player.inventory;
        TiamatPlayerInventory tiamatPlayerInventory = player.world.isRemote ? new TiamatPlayerInventory(player) : TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());
        if (tiamatPlayerInventory == null)
        {
            tiamatPlayerInventory = new TiamatPlayerInventory(player);
        }

        //Vanilla mainhand and offhand
        //Index 0 - 1
        //Internal index 0 and 40 (vanilla player inventory; active mainhand and offhand)
        addSlotToContainer(new TexturedFilteredSlot(playerInventory, 0, 43, 209, 96, 496, true, 1, stack -> getSlot(1).getStack().isEmpty() || !Equipment.isTwoHanded(stack)));
        addSlotToContainer(new TexturedFilteredSlot(playerInventory, 40, 25, 209, 112, 496, true, 1, stack -> getSlot(0).getStack().isEmpty() || !Equipment.isTwoHanded(stack)));

        //Tiamat extra mainhand and offhand
        //Index 2 - 3
        //Internal index 1 and 0 (tiamat player inventory; inactive mainhand and offhand)
        addSlotToContainer(new TexturedFilteredSlot(tiamatPlayerInventory, 1, 43, 191, 96, 496, false, 1, stack -> getSlot(3).getStack().isEmpty() || !Equipment.isTwoHanded(stack)));
        addSlotToContainer(new TexturedFilteredSlot(tiamatPlayerInventory, 0, 25, 191, 112, 496, false, 1, stack -> getSlot(2).getStack().isEmpty() || !Equipment.isTwoHanded(stack)));

        //Hotbar, other than the first slot (which is done above and reserved for active weaponset
        //Index 4 - 11
        //Internal index 1 - 8 (vanilla player inventory)
        for (int i = 1; i < 9; ++i)
        {
            addSlotToContainer(new Slot(playerInventory, i, 63 + (i - 1) * 18, 209));
        }

        //Main inventory
        //Index 12 - 38
        //Internal index 9 - 35 (vanilla player inventory)
        for (int yy = 0; yy < 3; ++yy)
        {
            for (int xx = 0; xx < 9; ++xx)
            {
                addSlotToContainer(new Slot(playerInventory, xx + (yy + 1) * 9, 43 + xx * 18, 133 + yy * 18));
            }
        }

        //Armor slots
        //Index 39 - 44
        //Internal indices...
        //...39 (vanilla head)
        //...2 (tiamat shoulders)
        //...3 (tiamat cape)
        //...38 (vanilla chest)
        //...37 (vanilla legs)
        //...36 (vanilla feet)
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.HEAD, 39, 7, 22, 0, 496);
        addSlotToContainer(new TexturedFilteredSlot(tiamatPlayerInventory, 2, 7, 40, 16, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "Shoulder")));
        addSlotToContainer(new TexturedFilteredSlot(tiamatPlayerInventory, 3, 7, 58, 32, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "Cape")));
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.CHEST, 38, 7, 76, 48, 496);
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.LEGS, 37, 7, 94, 64, 496);
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.FEET, 36, 7, 112, 80, 496);

        //Pet slot
        //Index 45
        //Internal index 4 (tiamat pet slot)
        addSlotToContainer(new TexturedFilteredSlot(tiamatPlayerInventory, 4, 52, 112, 128, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "Pet")));


        //Class slots
        //Index 46 - 47
        //Internal index 5 - 6 (tiamat class slots)
        TexturedSlot slot = new TexturedFilteredSlot(tiamatPlayerInventory, 5, 135, 37, 160, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "Class"));
        classTabSlots.add(slot);
        addSlotToContainer(slot);
        slot = new TexturedFilteredSlot(tiamatPlayerInventory, 6, 191, 37, 160, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "Class"));
        classTabSlots.add(slot);
        addSlotToContainer(slot);

        //Skill slots
        //Index 48 - 65
        //Internal index 7 - 24 (tiamat skill slots)
        for (int yy = 0; yy < 3; yy++)
        {
            for (int xx = 0; xx < 3; xx++)
            {
                slot = new TexturedFilteredSlot(tiamatPlayerInventory, 7 + yy * 3 + xx, 117 + xx * 18, 58 + yy * 18, 176, 496, true, 1, stack -> stack.hasTagCompound() && (Equipment.isType(stack, "ActiveSkill") || Equipment.isType(stack, "PassiveSkill")));
                classTabSlots.add(slot);
                addSlotToContainer(slot);
            }
        }
        for (int yy = 0; yy < 3; yy++)
        {
            for (int xx = 0; xx < 3; xx++)
            {
                slot = new TexturedFilteredSlot(tiamatPlayerInventory, 16 + yy * 3 + xx, 173 + xx * 18, 58 + yy * 18, 176, 496, true, 1, stack -> stack.hasTagCompound() && (Equipment.isType(stack, "ActiveSkill") || Equipment.isType(stack, "PassiveSkill")));
                classTabSlots.add(slot);
                addSlotToContainer(slot);
            }
        }


        //Profession slots
        //Index 66 - 69
        //Internal index 25 - 28 (tiamat profession slots; 2 gathering, then 2 crafting)
        slot = new TexturedFilteredSlot(tiamatPlayerInventory, 25, 135, 37, 192, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "ProfessionGather"));
        professionTabSlots.add(slot);
        addSlotToContainer(slot);
        slot = new TexturedFilteredSlot(tiamatPlayerInventory, 26, 153, 37, 192, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "ProfessionGather"));
        professionTabSlots.add(slot);
        addSlotToContainer(slot);
        slot = new TexturedFilteredSlot(tiamatPlayerInventory, 27, 173, 37, 208, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "ProfessionCraft"));
        professionTabSlots.add(slot);
        addSlotToContainer(slot);
        slot = new TexturedFilteredSlot(tiamatPlayerInventory, 28, 191, 37, 208, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "ProfessionCraft"));
        professionTabSlots.add(slot);
        addSlotToContainer(slot);

        //Recipe slots
        //Index 70 - 84
        //Internal index 29 - 43 (tiamat recipe slots)
        for (int yy = 0; yy < 3; yy++)
        {
            for (int xx = 0; xx < 5; xx++)
            {
                slot = new TexturedFilteredSlot(tiamatPlayerInventory, 29 + yy * 5 + xx, 127 + xx * 18, 58 + yy * 18, 224, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "Recipe"));
                professionTabSlots.add(slot);
                addSlotToContainer(slot);
            }
        }


        //Ready skill slots
        //Index 85 - 90
        //Internal index 44 - 49 (tiamat active skill slots)
        for (int yy = 0; yy < 6; yy++)
        {
            addSlotToContainer(new TexturedFilteredSlot(tiamatPlayerInventory, 44 + yy, 97, 22 + yy * 18, 176, 496, true, 1, stack -> stack.hasTagCompound() && Equipment.isType(stack, "ActiveSkill")));
        }
    }

    private void addVanillaEquipmentSlot(IInventory inventory, EntityEquipmentSlot slotEnum, int index, int x, int y, int u, int v)
    {
        addSlotToContainer(new TexturedSlot(inventory, index, x, y, u, v)
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

        if (index <= 3)
        {
            //From any weaponset slot
            //To main inventory or hotbar, in that order
            tryMergeItemStackRanges(stack, 12, 38);
            tryMergeItemStackRanges(stack, 4, 11);
        }
        else if (index <= 11)
        {
            //From hotbar
            //To any armor slot, if applicable, or to a weaponset slot or main inventory otherwise

            tryMergeItemStackRanges(stack, 39, 44);
            tryMergeItemStackRanges(stack, 0, 3);
            tryMergeItemStackRanges(stack, 12, 38);
        }
        else if (index <= 38)
        {
            //From main inventory
            //To any equipment slot, if applicable, or to a weaponset slot or hotbar otherwise
            tryMergeItemStackRanges(stack, 39, 44);
            tryMergeItemStackRanges(stack, 0, 3);
            tryMergeItemStackRanges(stack, 4, 11);
        }
        else if (index <= 44)
        {
            //From armor slots
            //To main inventory, hotbar, or weaponset slot, in that order
            tryMergeItemStackRanges(stack, 12, 38);
            tryMergeItemStackRanges(stack, 4, 11);
            tryMergeItemStackRanges(stack, 0, 3);
        }


        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.putStack(stack);

        slot.onSlotChanged();
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
                    Slot slot = this.inventorySlots.get(i);
                    if (!slot.isItemValid(stackFrom)) continue;

                    ItemStack stackTo = slot.getStack();
                    if (!canCombine(stackFrom, stackTo)) continue;

                    int limit = Tools.min(stackTo.getMaxStackSize(), slot.getSlotStackLimit());
                    if (!stackTo.isEmpty() && stackTo.getCount() >= limit) continue;


                    int sum = stackTo.isEmpty() ? stackFrom.getCount() : stackFrom.getCount() + stackTo.getCount();

                    if (stackTo.isEmpty()) slot.putStack(stackFrom.copy());

                    if (sum <= limit)
                    {
                        stackTo.setCount(sum);
                        stackFrom.setCount(0);
                        return;
                    }

                    stackTo.setCount(limit);
                    stackFrom.setCount(sum - limit);

                    slot.onSlotChanged();
                }
            }
            else
            {
                for (int i = startIndex; i >= endIndex; i--)
                {
                    Slot slot = this.inventorySlots.get(i);
                    if (!slot.isItemValid(stackFrom)) continue;

                    ItemStack stackTo = slot.getStack();
                    if (!canCombine(stackFrom, stackTo)) continue;

                    int limit = Tools.min(stackTo.getMaxStackSize(), slot.getSlotStackLimit());
                    if (!stackTo.isEmpty() && stackTo.getCount() >= limit) continue;


                    int sum = stackTo.isEmpty() ? stackFrom.getCount() : stackFrom.getCount() + stackTo.getCount();

                    if (stackTo.isEmpty()) slot.putStack(stackFrom.copy());

                    if (sum <= limit)
                    {
                        stackTo.setCount(sum);
                        stackFrom.setCount(0);
                        return;
                    }

                    stackTo.setCount(limit);
                    stackFrom.setCount(sum - limit);

                    slot.onSlotChanged();
                }
            }
        }
    }

    public static boolean canCombine(ItemStack from, ItemStack to)
    {
        if (from.isEmpty()) return false;
        if (to.isEmpty()) return true;
        return from.getItem() == to.getItem() && (!to.getHasSubtypes() || to.getMetadata() == from.getMetadata()) && ItemStack.areItemStackTagsEqual(to, from);
    }
}
