package com.fantasticsource.tiamatrpg.config.server;

import com.fantasticsource.tiamatrpg.config.server.items.ItemsConfig;
import net.minecraftforge.common.config.Config;

public class ServerConfig
{
    @Config.Name("Items")
    public ItemsConfig items = new ItemsConfig();
}