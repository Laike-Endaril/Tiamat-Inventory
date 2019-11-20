package com.fantasticsource.tiamatrpgmain.inventory;

import com.fantasticsource.tiamatrpgmain.config.server.items.TexturedSlot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
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

public class TiamatInventoryContainer extends Container
{
    private final EntityPlayer player;

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
        addSlotToContainer(new TexturedSlot(playerInventory, 0, 43, 209, 96, 496));
        addSlotToContainer(new TexturedSlot(playerInventory, 40, 25, 209, 112, 496));

        //Tiamat extra mainhand and offhand
        //Index 2 - 3
        //Internal index 1 and 0 (tiamat player inventory; inactive mainhand and offhand)
        addSlotToContainer(new TexturedSlot(tiamatPlayerInventory, 1, 43, 191, 96, 496));
        addSlotToContainer(new TexturedSlot(tiamatPlayerInventory, 0, 25, 191, 112, 496));

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
        addSlotToContainer(new TexturedSlot(tiamatPlayerInventory, 2, 7, 40, 16, 496));
        addSlotToContainer(new TexturedSlot(tiamatPlayerInventory, 3, 7, 58, 32, 496));
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.CHEST, 38, 7, 76, 48, 496);
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.LEGS, 37, 7, 94, 64, 496);
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.FEET, 36, 7, 112, 80, 496);
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
                return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
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

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            //MAINHAND is the default value, and should be treated as if it were null
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

            if (index <= 3)
            {
                //From offhand or mainhand of either weaponset
                //To main inventory or hotbar, in that order
                if (!this.mergeItemStack(itemstack1, 4, 39, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index <= 11)
            {
                //From hotbar
                //To any equipment slot, if applicable, or to main inventory otherwise
                if (entityequipmentslot != EntityEquipmentSlot.MAINHAND)
                {
                    //TODO
                }
                else
                {
                    if (!this.mergeItemStack(itemstack1, 12, 39, true))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (index <= 38)
            {
                //From main inventory
                //To any equipment slot, if applicable, or to a hand or the hotbar otherwise
                if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.HAND)
                {
                    if (!this.mergeItemStack(itemstack1, 0, 12, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else
                {
                    //TODO
                }
            }
            else if (index <= 44)
            {
                //From armor slots
                //To main inventory or hotbar, in that order
                if (!this.mergeItemStack(itemstack1, 4, 39, true))
                {
                    return ItemStack.EMPTY;
                }
            }


            if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();

            if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
        }

        return itemstack;
    }
}
