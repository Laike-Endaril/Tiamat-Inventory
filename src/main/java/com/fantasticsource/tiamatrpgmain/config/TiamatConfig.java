package com.fantasticsource.tiamatrpgmain.config;

import com.fantasticsource.tiamatrpgmain.TiamatRPGMain;
import com.fantasticsource.tiamatrpgmain.config.client.ClientConfig;
import com.fantasticsource.tiamatrpgmain.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = TiamatRPGMain.MODID)
public class TiamatConfig
{
    @Config.Name("Client")
    public static ClientConfig client = new ClientConfig();

    @Config.Name("Server")
    public static ServerConfig server = new ServerConfig();
}
