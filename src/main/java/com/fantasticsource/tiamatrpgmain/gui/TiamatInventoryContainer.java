package com.fantasticsource.tiamatrpgmain.gui;

import com.fantasticsource.tiamatrpgmain.config.server.items.TexturedSlot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

public class TiamatInventoryContainer extends Container
{
    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    public boolean isLocalWorld;
    private final EntityPlayer player;

    public TiamatInventoryContainer(EntityPlayer playerIn)
    {
        player = playerIn;
        InventoryPlayer playerInventory = playerIn.inventory;
        isLocalWorld = !playerIn.world.isRemote;

        //Armor
        for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            addSlotToContainer(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18)
            {
                public int getSlotStackLimit()
                {
                    return 1;
                }

                public boolean isItemValid(ItemStack stack)
                {
                    return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
                }

                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    ItemStack itemstack = getStack();
                    return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
                }

                @Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }

        //Main inventory
        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        //Hotbar
        for (int i1 = 0; i1 < 9; ++i1)
        {
            addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }

        //Offhand
        TexturedSlot slot = new TexturedSlot(playerInventory, 40, 77, 62, 112, 496);
        addSlotToContainer(slot);
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

            if (index == 0)
            {
                if (!mergeItemStack(itemstack1, 9, 45, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index < 5)
            {
                if (!mergeItemStack(itemstack1, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index < 9)
            {
                if (!mergeItemStack(itemstack1, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !inventorySlots.get(8 - entityequipmentslot.getIndex()).getHasStack())
            {
                int i = 8 - entityequipmentslot.getIndex();

                if (!mergeItemStack(itemstack1, i, i + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !inventorySlots.get(45).getHasStack())
            {
                if (!mergeItemStack(itemstack1, 45, 46, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index < 36)
            {
                if (!mergeItemStack(itemstack1, 36, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index < 45)
            {
                if (!mergeItemStack(itemstack1, 9, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!mergeItemStack(itemstack1, 9, 45, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0)
            {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }
}