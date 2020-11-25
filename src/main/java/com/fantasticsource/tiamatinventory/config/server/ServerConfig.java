package com.fantasticsource.tiamatinventory.config.server;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class ServerConfig
{
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

    @Config.Name("Allow Auto-Pickup to Pet")
    @Config.Comment("Whether players can auto-pickup items to their pet slot")
    @Config.LangKey(MODID + ".config.allowPickupPet")
    public boolean allowPickupPet = true;

    @Config.Name("Allow Auto-Pickup to Deck")
    @Config.Comment("Whether players can auto-pickup items to their deck slot")
    @Config.LangKey(MODID + ".config.allowPickupDeck")
    public boolean allowPickupDeck = true;

    @Config.Name("Allow Auto-Pickup to Armor")
    @Config.Comment("Whether players can auto-pickup items to their armor slots")
    @Config.LangKey(MODID + ".config.allowPickupArmor")
    public boolean allowPickupArmor = true;

    @Config.Name("Allow Auto-Pickup to Quickslots")
    @Config.Comment("Whether players can auto-pickup items to their quickslots")
    @Config.LangKey(MODID + ".config.allowPickupQuickslots")
    public boolean allowPickupQuickslots = true;

    @Config.Name("Allow Auto-Pickup to Main Hand")
    @Config.Comment("Whether players can auto-pickup items to their vanilla main hand slot")
    @Config.LangKey(MODID + ".config.allowPickupMainHand")
    public boolean allowPickupMainHand = true;

    @Config.Name("Allow Auto-Pickup to Offhand")
    @Config.Comment("Whether players can auto-pickup items to their vanilla offhand slot")
    @Config.LangKey(MODID + ".config.allowPickupOffhand")
    public boolean allowPickupOffhand = true;

    @Config.Name("Allow Auto-Pickup to Weaponsets")
    @Config.Comment("Whether players can auto-pickup items to their weaponset slots")
    @Config.LangKey(MODID + ".config.allowPickupWeaponset")
    public boolean allowPickupWeaponset = true;

    @Config.Name("Allow Auto-Pickup to Hotbar")
    @Config.Comment("Whether players can auto-pickup items to their not-in-hand hotbar slots (requires hotbar to be enabled)")
    @Config.LangKey(MODID + ".config.allowPickupHotbar")
    public boolean allowPickupHotbar = true;

    @Config.Name("Allow Auto-Pickup to Cargo")
    @Config.Comment("Whether players can auto-pickup items to their main/cargo inventory slots")
    @Config.LangKey(MODID + ".config.allowPickupCargo")
    public boolean allowPickupCargo = true;
}
