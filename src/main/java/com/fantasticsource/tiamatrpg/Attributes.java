package com.fantasticsource.tiamatrpg;

import com.fantasticsource.tiamatrpg.config.TiamatConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class Attributes
{
    public static String[] displayAttributes = new String[0], displayAttributeDescriptions = new String[0];

    public static final double
            DEFAULT_MELEE_ANGLE = 0,
            DEFAULT_MELEE_TARGETS = 1,
            DEFAULT_MELEE_MODE = 0, //0 means normal damage anywhere in tolerance; 1 means good mult anywhere in tolerance, bad mult outside; 2 means fade from good mult at best distance to bad mult at edges of tolerance, and bad mult outside tolerance
            DEFAULT_MELEE_BEST_DISTANCE = 1.5,
            DEFAULT_MELEE_TOLERANCE = 1.5,
            DEFAULT_MELEE_MULTIPLIER_GOOD = 1,
            DEFAULT_MELEE_MULTIPLIER_BAD = 0;

    public static RangedAttribute
            MELEE_ANGLE,
            MELEE_TARGETS,
            MELEE_MODE,
            MELEE_BEST_DISTANCE,
            MELEE_TOLERANCE,
            MELEE_MULTIPLIER_GOOD,
            MELEE_MULTIPLIER_BAD;

    public static void init()
    {
        MELEE_ANGLE = new RangedAttribute(null, MODID + ".meleeAngle", DEFAULT_MELEE_ANGLE, 0, Double.MAX_VALUE);
        MELEE_TARGETS = new RangedAttribute(null, MODID + ".meleeTargets", DEFAULT_MELEE_TARGETS, 0, Double.MAX_VALUE);
        MELEE_MODE = new RangedAttribute(null, MODID + ".meleeMode", DEFAULT_MELEE_MODE, 0, Double.MAX_VALUE);
        MELEE_BEST_DISTANCE = new RangedAttribute(null, MODID + ".meleeBestDistance", DEFAULT_MELEE_BEST_DISTANCE, 0, Double.MAX_VALUE);
        MELEE_TOLERANCE = new RangedAttribute(null, MODID + ".meleeTolerance", DEFAULT_MELEE_TOLERANCE, 0, Double.MAX_VALUE);
        MELEE_MULTIPLIER_GOOD = new RangedAttribute(null, MODID + ".meleeMultiplierGood", DEFAULT_MELEE_MULTIPLIER_GOOD, 0, Double.MAX_VALUE);
        MELEE_MULTIPLIER_BAD = new RangedAttribute(null, MODID + ".meleeMultiplierBad", DEFAULT_MELEE_MULTIPLIER_BAD, 0, Double.MAX_VALUE);
    }


    public static void addAttributes(EntityLivingBase livingBase)
    {
        if (livingBase instanceof EntityPlayer)
        {
            AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();

            //Add new attributes to entity
            attributeMap.registerAttribute(MELEE_ANGLE);
            attributeMap.registerAttribute(MELEE_TARGETS);

            attributeMap.registerAttribute(MELEE_MODE);
            attributeMap.registerAttribute(MELEE_BEST_DISTANCE);
            attributeMap.registerAttribute(MELEE_TOLERANCE);
            attributeMap.registerAttribute(MELEE_MULTIPLIER_GOOD);
            attributeMap.registerAttribute(MELEE_MULTIPLIER_BAD);
        }
    }

    public static void editAttributes(EntityLivingBase livingBase)
    {
        if (livingBase instanceof EntityPlayer)
        {
            AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();

            //Add new attributes to entity
            attributeMap.getAttributeInstance(MELEE_ANGLE).setBaseValue(MELEE_ANGLE.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_TARGETS).setBaseValue(MELEE_TARGETS.getDefaultValue());

            attributeMap.getAttributeInstance(MELEE_MODE).setBaseValue(MELEE_MODE.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_BEST_DISTANCE).setBaseValue(MELEE_BEST_DISTANCE.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_TOLERANCE).setBaseValue(MELEE_TOLERANCE.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_MULTIPLIER_GOOD).setBaseValue(MELEE_MULTIPLIER_GOOD.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_MULTIPLIER_BAD).setBaseValue(MELEE_MULTIPLIER_BAD.getDefaultValue());
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
