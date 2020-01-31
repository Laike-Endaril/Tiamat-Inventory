package com.fantasticsource.tiamatrpg;

import com.fantasticsource.tiamatrpg.config.TiamatConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Attributes
{
    public static String[] displayAttributes = new String[0], displayAttributeDescriptions = new String[0];

//    public static final double DEFAULT_MELEE_ANGLE = 0;

//    public static RangedAttribute MELEE_ANGLE;

    public static void init()
    {
//        MELEE_ANGLE = new RangedAttribute(null, MODID + ".meleeAngle", DEFAULT_MELEE_ANGLE, 0, Double.MAX_VALUE);
    }

    public static void addAttributes(EntityLivingBase livingBase)
    {
        if (livingBase instanceof EntityPlayer)
        {
            AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();

            //Add new attributes to entity
//            attributeMap.registerAttribute(MELEE_ANGLE);
        }
    }

    public static void editAttributes(EntityLivingBase livingBase)
    {
        if (livingBase instanceof EntityPlayer)
        {
            AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();

            //Add edit entity's base values for attributes
//            attributeMap.getAttributeInstance(MELEE_ANGLE).setBaseValue(MELEE_ANGLE.getDefaultValue());
        }
    }


    public static void clientInit(FMLPreInitializationEvent event)
    {
        updateDisplayList();
    }

    public static void configChanged(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        updateDisplayList();
    }

    private static void updateDisplayList()
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
