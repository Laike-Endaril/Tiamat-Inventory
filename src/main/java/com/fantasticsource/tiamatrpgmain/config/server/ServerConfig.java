package com.fantasticsource.tiamatrpgmain.config.server;

import com.fantasticsource.tiamatrpgmain.TiamatRPGMain;
import com.fantasticsource.tiamatrpgmain.config.server.items.ItemsConfig;
import net.minecraftforge.common.config.Config;

public class ServerConfig
{
    @Config.Name("Attributes")
    @Config.LangKey(TiamatRPGMain.MODID + ".config.attributes")
    public AttributesConfig attributes = new AttributesConfig();

    @Config.Name("Inventory")
    @Config.LangKey(TiamatRPGMain.MODID + ".config.inventory")
    public InventoryConfig inventory = new InventoryConfig();

    @Config.Name("Items")
    @Config.LangKey(TiamatRPGMain.MODID + ".config.items")
    public ItemsConfig items = new ItemsConfig();
}
