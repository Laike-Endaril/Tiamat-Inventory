package com.fantasticsource.tiamatrpg.config;

import com.fantasticsource.tiamatrpg.config.client.ClientConfig;
import com.fantasticsource.tiamatrpg.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

@Config(modid = MODID)
public class TiamatConfig
{
    @Config.Name("Client Settings")
    @Config.LangKey(MODID + ".config.clientSettings")
    public static ClientConfig clientSettings = new ClientConfig();

    @Config.Name("Server Settings")
    @Config.LangKey(MODID + ".config.serverSettings")
    public static ServerConfig serverSettings = new ServerConfig();
}
