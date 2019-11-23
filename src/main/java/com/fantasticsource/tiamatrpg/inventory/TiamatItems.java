package com.fantasticsource.tiamatrpg.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class TiamatItems
{
    public static void removeTag(ItemStack stack, String key)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey(key)) return;

        compound.removeTag(key);
    }


    public static String getString(ItemStack stack, String key)
    {
        if (!stack.hasTagCompound()) return null;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return null;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey(key)) return null;

        return compound.getString(key);
    }

    public static void setString(ItemStack stack, String key, String value)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        compound.setString(key, value);
    }


    public static int getInteger(ItemStack stack, String key)
    {
        if (!stack.hasTagCompound()) return Integer.MIN_VALUE;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return Integer.MIN_VALUE;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey(key)) return Integer.MIN_VALUE;

        return compound.getInteger(key);
    }

    public static void setInteger(ItemStack stack, String key, int value)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        compound.setInteger(key, value);
    }


    public static double getDouble(ItemStack stack, String key)
    {
        if (!stack.hasTagCompound()) return Double.NaN;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return Double.NaN;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey(key)) return Double.NaN;

        return compound.getDouble(key);
    }

    public static void setDouble(ItemStack stack, String key, double value)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        compound.setDouble(key, value);
    }


    public static boolean hasFlag(ItemStack stack, String key)
    {
        return getString(stack, key) != null;
    }

    public static void setFlag(ItemStack stack, String key)
    {
        setString(stack, key, "");
    }


    public static String getItemType(ItemStack stack)
    {
        return getString(stack, "ItemType");
    }

    public static void setItemType(ItemStack stack, String itemType)
    {
        setString(stack, "ItemType", itemType);
    }

    public static boolean isItemType(ItemStack stack, String itemType)
    {
        String type = getString(stack, "ItemType");
        if (itemType == null) return type == null;
        return type != null && type.equals(itemType);
    }


    public static String getWeaponType(ItemStack stack)
    {
        return getString(stack, "WeaponType");
    }

    public static void setWeaponType(ItemStack stack, String weaponType)
    {
        setString(stack, "WeaponType", weaponType);
    }

    public static boolean isWeaponType(ItemStack stack, String weaponType)
    {
        String type = getString(stack, "WeaponType");
        if (weaponType == null) return type == null;
        return type != null && type.equals(weaponType);
    }


    public static boolean isTwoHanded(ItemStack stack)
    {
        if (stack.getItem() == Items.BOW) return true;
        return hasFlag(stack, "2H");
    }

    public static void setTwoHanded(ItemStack stack, boolean twoHanded)
    {
        if (twoHanded) setFlag(stack, "2H");
        else removeTag(stack, "2H");
    }
}
