package com.fantasticsource.tiamatrpg.block;

import com.fantasticsource.tiamatrpg.TiamatRPG;
import net.minecraft.item.ItemBlock;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class ItemActionEditor extends ItemBlock
{
    public ItemActionEditor()
    {
        super(TiamatRPG.blockActionEditor);

        setUnlocalizedName(MODID + ":actioneditor");
        setRegistryName("actioneditor");
    }
}
