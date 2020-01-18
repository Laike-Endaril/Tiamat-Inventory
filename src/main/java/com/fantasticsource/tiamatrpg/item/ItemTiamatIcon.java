package com.fantasticsource.tiamatrpg.item;

import com.fantasticsource.tiamatrpg.TiamatRPG;
import net.minecraft.item.Item;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class ItemTiamatIcon extends Item
{
    public ItemTiamatIcon()
    {
        setCreativeTab(TiamatRPG.creativeTab);

        setUnlocalizedName(MODID + ":tiamaticon");
        setRegistryName("tiamaticon");
    }
}
