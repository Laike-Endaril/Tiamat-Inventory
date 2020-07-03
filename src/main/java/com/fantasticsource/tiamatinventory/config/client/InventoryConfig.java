package com.fantasticsource.tiamatinventory.config.client;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class InventoryConfig
{
    @Config.Name("Attributes To Display")
    @Config.LangKey(MODID + ".config.displayAttributes")
    @Config.Comment(
            {
                    "Which attributes to display in the inventory, and their descriptions",
                    "eg. tiamatinventory.autoBlock, How likely you are to automatically block an attack"
            })
    public String[] attributes = new String[0];
}
