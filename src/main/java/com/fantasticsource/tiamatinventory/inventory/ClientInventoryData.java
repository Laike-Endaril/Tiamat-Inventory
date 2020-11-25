package com.fantasticsource.tiamatinventory.inventory;

import com.fantasticsource.tiamatinventory.config.TiamatConfig;

public class ClientInventoryData
{
    public static int
            inventorySize = TiamatConfig.serverSettings.defaultInventorySize,
            craftW = TiamatConfig.serverSettings.craftW,
            craftH = TiamatConfig.serverSettings.craftH;
    public static boolean allowHotbar = TiamatConfig.serverSettings.allowHotbar;
    public static String[] additionalSyncedAttributes = new String[0];
}
