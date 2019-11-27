package com.fantasticsource.tiamatrpg.config.server;

import com.fantasticsource.tiamatrpg.config.server.items.ItemsConfig;
import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class ServerConfig
{
    @Config.Name("Items")
    @Config.LangKey(MODID + ".config.serverItems")
    public ItemsConfig items = new ItemsConfig();
}
