package com.fantasticsource.tiamatinventory.config.server;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class ServerConfig
{
    @Config.Name("Default Inventory Size")
    @Config.Comment("The default inventory size, without a backpack")
    @Config.LangKey(MODID + ".config.defaultInvSize")
    @Config.RangeInt(min = 0, max = 27)
    public int defaultInventorySize = 27;

    @Config.Name("Allow Hotbar")
    @Config.Comment("Whether the hotbar is enabled in non-creative mode (always enabled in creative)")
    @Config.LangKey(MODID + ".config.allowHotbar")
    public boolean allowHotbar = true;
}
