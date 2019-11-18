package com.fantasticsource.tiamatrpgmain.gui;

import com.fantasticsource.tiamatrpgmain.config.server.items.TexturedSlot;
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

public class TiamatInventoryContainer extends Container
{
    private final EntityPlayer player;

    public TiamatInventoryContainer(EntityPlayer playerIn)
    {
        player = playerIn;
        InventoryPlayer playerInventory = playerIn.inventory;
        TiamatPlayerInventory tiamatPlayerInventory = TiamatPlayerInventory.tiamatInventories.get(playerIn.getPersistentID());

        //Offhand slots
        //Index 0 - 1
        //Internal index 0 (tiamat player inventory; inactive offhand), 40 (vanilla player inventory; active offhand)
//        addSlotToContainer(new TexturedSlot(tiamatPlayerInventory, 0, 25, 191, 112, 496));
        addSlotToContainer(new TexturedSlot(playerInventory, 40, 25, 209, 112, 496));

        //Mainhand slots
        //Index 2 - 3
        //Internal index 1 (tiamat player inventory; inactive mainhand), 0 (vanilla player inventory; active mainhand)
//        addSlotToContainer(new TexturedSlot(tiamatPlayerInventory, 1, 43, 191, 96, 496));
        addSlotToContainer(new TexturedSlot(playerInventory, 0, 43, 209, 96, 496));

        //Hotbar, other than the first slot (which is done above and reserved for active weaponset
        //Index 4 - 11
        //Internal index 1 - 8 (vanilla player inventory)
        for (int i = 1; i < 9; ++i)
        {
            addSlotToContainer(new Slot(playerInventory, i, 63 + (i - 1) * 18, 209));
        }

        //Main inventory
        //Index 13 - 39
        //Internal index 9 - 35 (vanilla player inventory)
        for (int yy = 0; yy < 3; ++yy)
        {
            for (int xx = 0; xx < 9; ++xx)
            {
                addSlotToContainer(new Slot(playerInventory, xx + (yy + 1) * 9, 43 + xx * 18, 133 + yy * 18));
            }
        }

        //Armor slots
        //Index 40 - 45
        //Internal indices...
        //...39 (vanilla head)
        //...2 (tiamat shoulders)
        //...3 (tiamat cape)
        //...38 (vanilla chest)
        //...37 (vanilla legs)
        //...36 (vanilla feet)
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.HEAD, 39, 7, 22, 0, 496);
//        addSlotToContainer(new TexturedSlot(tiamatPlayerInventory, 2, 7, 40, 16, 496));
//        addSlotToContainer(new TexturedSlot(tiamatPlayerInventory, 3, 7, 58, 32, 496));
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
}
