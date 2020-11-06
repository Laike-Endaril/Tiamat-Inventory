package com.fantasticsource.tiamatinventory;

import com.fantasticsource.tiamatinventory.config.TiamatConfig;

public class AttributeDisplayData
{
    public static String[] displayAttributes = new String[0], displayAttributeDescriptions = new String[0];

    public static void updateDisplayList()
    {
        String[] array = TiamatConfig.clientSettings.inventory.attributes;
        displayAttributes = new String[array.length];
        displayAttributeDescriptions = new String[array.length];

        for (int i = 0; i < array.length; i++)
        {
            String s = array[i];
            int index = s.indexOf(',');
            if (index == -1)
            {
                displayAttributes[i] = s.trim();
                displayAttributeDescriptions[i] = "";
            }
            else
            {
                displayAttributes[i] = s.substring(0, index).trim();
                displayAttributeDescriptions[i] = s.substring(index + 1).trim();
            }
        }
    }
}
