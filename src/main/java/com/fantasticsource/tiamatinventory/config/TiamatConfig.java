package com.fantasticsource.tiamatinventory.config;

import com.fantasticsource.tiamatinventory.config.server.ServerConfig;
import com.fantasticsource.tiamatinventory.config.client.ClientConfig;
import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

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
