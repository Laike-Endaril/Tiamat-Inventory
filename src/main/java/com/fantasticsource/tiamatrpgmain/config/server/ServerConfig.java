package com.fantasticsource.tiamatrpgmain.config.server;

import com.fantasticsource.tiamatrpgmain.config.server.items.ItemsConfig;
import net.minecraftforge.common.config.Config;

public class ServerConfig
{
    @Config.Name("Attributes")
    public AttributesConfig attributes = new AttributesConfig();

    @Config.Name("Inventory")
    public InventoryConfig inventory = new InventoryConfig();

    @Config.Name("Items")
    public ItemsConfig items = new ItemsConfig();
}
