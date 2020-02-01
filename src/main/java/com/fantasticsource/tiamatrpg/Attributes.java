package com.fantasticsource.tiamatrpg;

import com.fantasticsource.tiamatrpg.config.TiamatConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;

public class Attributes
{
    public static String[] displayAttributes = new String[0], displayAttributeDescriptions = new String[0];

    public static void updateDisplayList(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        String[] array = TiamatConfig.clientSettings.inventory.attributes;
        displayAttributes = new String[array.length];
        displayAttributeDescriptions = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            String s = array[i];
            displayAttributes[i] = s.substring(0, s.indexOf(',')).trim();
            displayAttributeDescriptions[i] = s.substring(s.indexOf(',') + 1).trim();
        }
    }
}
