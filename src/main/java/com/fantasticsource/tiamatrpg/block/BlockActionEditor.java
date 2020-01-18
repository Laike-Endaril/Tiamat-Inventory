package com.fantasticsource.tiamatrpg.block;

import com.fantasticsource.tiamatrpg.TiamatRPG;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class BlockActionEditor extends Block
{
    public BlockActionEditor()
    {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);

        setBlockUnbreakable();
        setResistance(Float.MAX_VALUE);

        setCreativeTab(TiamatRPG.creativeTab);

        setUnlocalizedName(MODID + ":actioneditor");
        setRegistryName("actioneditor");
    }
}
