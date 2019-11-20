package com.fantasticsource.tiamatrpgmain.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

public class Equipment
{
    public static final String TAG = MODID + "Equipment";

    public static boolean isType(ItemStack stack, String type)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(TAG)) return false;

        return compound.getString(TAG).equals(type);
    }

    public static boolean isTwoHanded(ItemStack stack)
    {
        return isType(stack, "2H") || stack.getItem() == Items.BOW;
    }
}
