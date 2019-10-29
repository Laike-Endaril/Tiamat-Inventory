package com.fantasticsource.tiamatrpgmain.config.server.items;

import com.fantasticsource.tiamatrpgmain.TiamatRPGMain;
import net.minecraftforge.common.config.Config;

public class ItemsConfig
{
    @Config.Name("Affixes")
    @Config.LangKey(TiamatRPGMain.MODID + ".config.affixes")
    public AffixesConfig affixes = new AffixesConfig();
}
