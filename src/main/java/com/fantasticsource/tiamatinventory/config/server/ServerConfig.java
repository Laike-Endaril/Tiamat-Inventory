package com.fantasticsource.tiamatinventory.config.server;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class ServerConfig
{
    @Config.Name("Autopickup")
    @Config.LangKey(MODID + ".config.autopickup")
    public AutopickupConfig autopickup = new AutopickupConfig();

    @Config.Name("Crafting Grid Width")
    @Config.Comment("The width of the crafting grid in the Tiamat Inventory")
    @Config.LangKey(MODID + ".config.craftW")
    @Config.RangeInt(min = 0, max = 3)
    public int craftW = 2;

    @Config.Name("Crafting Grid Height")
    @Config.Comment("The height of the crafting grid in the Tiamat Inventory")
    @Config.LangKey(MODID + ".config.craftH")
    @Config.RangeInt(min = 0, max = 3)
    public int craftH = 2;

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
