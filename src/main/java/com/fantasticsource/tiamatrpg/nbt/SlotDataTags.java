package com.fantasticsource.tiamatrpg.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class SlotDataTags
{
    public static int getInvSlotCount(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return 0;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("invSlotCount")) return 0;

        return compound.getInteger("invSlotCount");
    }

    public static void clearInvSlots(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(MODID)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("invSlotCount")) return;

        compound.removeTag("invSlotCount");
        if (compound.hasNoTags()) mainTag.removeTag(MODID);
    }

    public static void setInvSlots(ItemStack stack, int count)
    {
        if (count == 0)
        {
            clearInvSlots(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        compound.setInteger("invSlotCount", count);
    }
}
