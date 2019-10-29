package com.fantasticsource.tiamatrpgmain.config.server.items;

import com.fantasticsource.mctools.ConfigHandler;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatrpgmain.config.TiamatConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.Arrays;

import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

public class AffixesConfig
{
    public static void init() throws IllegalAccessException, IOException
    {
        ConfigHandler handler = new ConfigHandler(MODID).load();
        for (ConfigCategory category : MCTools.getConfig(MODID).getCategory("general.server.items.affixes").getChildren())
        {
            String name = category.getName();
            boolean found = false;
            for (String s : TiamatConfig.server.items.affixes.affixTypeNames)
            {
                if (s.toLowerCase().equals(name))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                handler.removeCategory(category.getQualifiedName());
            }
        }
        handler.save().sync();

        for (String typeName : TiamatConfig.server.items.affixes.affixTypeNames)
        {
            MCTools.addLangKey(typeName.toLowerCase(), typeName);
        }
    }

    @Config.Name("Affix Type Names")
    @Config.Comment({"Each name entered here will create an affix type group to add affixes to (need to completely close and reopen config menu to populate)"})
    public String[] affixTypeNames = new String[0];

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event) throws IOException, IllegalAccessException
    {
        if (event.getModID().equals(MODID))
        {
            ConfigHandler handler = new ConfigHandler(MODID).load();
            for (ConfigCategory category : MCTools.getConfig(MODID).getCategory("general.server.items.affixes").getChildren())
            {
                if (!Arrays.asList(TiamatConfig.server.items.affixes.affixTypeNames).contains(category.getName().toLowerCase()))
                {
                    handler.removeCategory(category.getQualifiedName());
                }
            }
            for (String typeName : TiamatConfig.server.items.affixes.affixTypeNames)
            {
                handler.addCategory("general.server.items.affixes." + typeName).addProperty("general.server.items.affixes." + typeName + ".Test", "Stuff!");
            }
            handler.save().sync();
        }
    }
}
