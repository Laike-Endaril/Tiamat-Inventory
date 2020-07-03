package com.fantasticsource.tiamatinventory.config.client;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class ClientConfig
{
    @Config.Name("Inventory")
    @Config.LangKey(MODID + ".config.clientInventory")
    public InventoryConfig inventory = new InventoryConfig();
}
