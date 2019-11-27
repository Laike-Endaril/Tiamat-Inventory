package com.fantasticsource.tiamatrpg.config.server.items;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class ItemsConfig
{
    @Config.Name("Affixes")
    @Config.LangKey(MODID + ".config.affixes")
    public AffixesConfig affixes = new AffixesConfig();
}
