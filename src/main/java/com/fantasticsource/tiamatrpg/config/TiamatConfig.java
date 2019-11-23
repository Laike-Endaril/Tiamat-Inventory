package com.fantasticsource.tiamatrpg.config;

import com.fantasticsource.tiamatrpg.TiamatRPG;
import com.fantasticsource.tiamatrpg.config.client.ClientConfig;
import com.fantasticsource.tiamatrpg.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = TiamatRPG.MODID)
public class TiamatConfig
{
    @Config.Name("Client")
    public static ClientConfig client = new ClientConfig();

    @Config.Name("Server")
    public static ServerConfig server = new ServerConfig();
}
