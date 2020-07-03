package com.fantasticsource.tiamatinventory.inventory.inventoryhacks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FakeSlot extends Slot
{
    public FakeSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return 0;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 0;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    @Override
    public void onSlotChanged()
    {
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return false;
    }

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public boolean getHasStack()
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getStack()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
    {
    }

    @Override
    public void putStack(ItemStack stack)
    {
    }
}
