package com.fantasticsource.tiamatrpg.config.server;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class ServerConfig
{
    @Config.Name("Default Inventory Size")
    @Config.Comment("The default inventory size, without a backpack")
    @Config.LangKey(MODID + ".config.defaultInvSize")
    @Config.RangeInt(min = 0, max = 36)
    public int defaultInventorySize = 3;
}
