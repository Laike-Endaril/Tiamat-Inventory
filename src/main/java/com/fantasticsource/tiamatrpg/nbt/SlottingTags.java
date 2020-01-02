package com.fantasticsource.tiamatrpg.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class SlottingTags
{
    public static ArrayList<String> getItemSlottings(ItemStack stack)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return result;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("slots")) return result;

        NBTTagList list = compound.getTagList("slots", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) result.add(list.getStringTagAt(i));

        return result;
    }

    public static void clearItemSlottings(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(MODID)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("slots")) return;

        compound.removeTag("slots");
        if (compound.hasNoTags()) mainTag.removeTag(MODID);
    }

    public static void removeItemSlotting(ItemStack stack, String slot)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(MODID)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("slots")) return;

        NBTTagList list = compound.getTagList("slots", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++)
        {
            if (list.getStringTagAt(i).equals(slot))
            {
                list.removeTag(i);
                if (list.tagCount() == 0)
                {
                    compound.removeTag("slots");
                    if (compound.hasNoTags()) mainTag.removeTag(MODID);
                }
                break;
            }
        }
    }

    public static void addItemSlotting(ItemStack stack, String slot)
    {
        if (itemHasSlotting(stack, slot)) return;

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        if (!compound.hasKey("slots")) compound.setTag("slots", new NBTTagList());
        NBTTagList list = compound.getTagList("slots", Constants.NBT.TAG_STRING);

        list.appendTag(new NBTTagString(slot));
    }

    public static boolean itemHasSlotting(ItemStack stack, String slot)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return false;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("slots")) return false;

        NBTTagList list = compound.getTagList("slots", Constants.NBT.TAG_STRING);
        for (int i = list.tagCount() - 1; i >= 0; i--) if (list.getStringTagAt(i).equals(slot)) return true;
        return false;
    }
}
