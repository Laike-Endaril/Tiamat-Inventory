package com.fantasticsource.tiamatrpgmain.config.server.items;

import com.fantasticsource.mctools.ConfigHandler;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatrpgmain.config.TiamatConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

public class AffixesConfig
{
    public static void init() throws IllegalAccessException
    {
        for (String typeName : TiamatConfig.server.items.affixes.affixTypeNames)
        {
            MCTools.addLangKey("general.server settings.items.affixes." + typeName.toLowerCase(), typeName);
        }
    }

    @Config.Name("Affix Type Names")
    @Config.LangKey(MODID + ".config.affixes.typeNames")
    @Config.Comment({"Each name entered here will create an affix type group to add affixes to (need to completely close and reopen config menu to populate)"})
    public String[] affixTypeNames = new String[0];

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event) throws IOException, IllegalAccessException
    {
        if (event.getModID().equals(MODID))
        {
            ConfigHandler handler = new ConfigHandler(MODID).load();
            for (String typeName : TiamatConfig.server.items.affixes.affixTypeNames)
            {
                handler.addCategory("general.server settings.items.affixes." + typeName).addProperty("general.server settings.items.affixes." + typeName + ".Test", "yay").save().sync();
            }
        }
    }
}
