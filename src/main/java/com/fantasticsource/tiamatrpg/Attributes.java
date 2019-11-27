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

    public static final double
            DEFAULT_AUTO_BLOCK = 0,
            DEFAULT_BLOCK_EFFICIENCY = 0,
            DEFAULT_BLOCK_STABILITY = 0,
            DEFAULT_AUTO_BARRIER = 0,
            DEFAULT_BARRIER_EFFICIENCY = 0,
            DEFAULT_DODGE_EFFICIENCY = 0,
            DEFAULT_AUTO_DODGE = 0,
            DEFAULT_PARRY = 0;

    public static RangedAttribute
            MELEE_ANGLE,
            MELEE_TARGETS,
            MELEE_MODE,
            MELEE_BEST_DISTANCE,
            MELEE_TOLERANCE,
            MELEE_MULTIPLIER_GOOD,
            MELEE_MULTIPLIER_BAD;

    public static RangedAttribute
            AUTO_BLOCK,
            BLOCK_EFFICIENCY,
            BLOCK_STABILITY,
            AUTO_BARRIER,
            BARRIER_EFFICIENCY,
            DODGE_EFFICIENCY,
            AUTO_DODGE,
            PARRY;

    public static void init()
    {
        MELEE_ANGLE = new RangedAttribute(null, MODID + ".meleeAngle", DEFAULT_MELEE_ANGLE, 0, Double.MAX_VALUE);
        MELEE_TARGETS = new RangedAttribute(null, MODID + ".meleeTargets", DEFAULT_MELEE_TARGETS, 0, Double.MAX_VALUE);
        MELEE_MODE = new RangedAttribute(null, MODID + ".meleeMode", DEFAULT_MELEE_MODE, 0, Double.MAX_VALUE);
        MELEE_BEST_DISTANCE = new RangedAttribute(null, MODID + ".meleeBestDistance", DEFAULT_MELEE_BEST_DISTANCE, 0, Double.MAX_VALUE);
        MELEE_TOLERANCE = new RangedAttribute(null, MODID + ".meleeTolerance", DEFAULT_MELEE_TOLERANCE, 0, Double.MAX_VALUE);
        MELEE_MULTIPLIER_GOOD = new RangedAttribute(null, MODID + ".meleeMultiplierGood", DEFAULT_MELEE_MULTIPLIER_GOOD, 0, Double.MAX_VALUE);
        MELEE_MULTIPLIER_BAD = new RangedAttribute(null, MODID + ".meleeMultiplierBad", DEFAULT_MELEE_MULTIPLIER_BAD, 0, Double.MAX_VALUE);

        AUTO_BLOCK = new RangedAttribute(null, MODID + ".autoBlock", DEFAULT_AUTO_BLOCK, 0, Double.MAX_VALUE);
        BLOCK_EFFICIENCY = new RangedAttribute(null, MODID + ".blockEfficiency", DEFAULT_BLOCK_EFFICIENCY, 0, Double.MAX_VALUE);
        BLOCK_STABILITY = new RangedAttribute(null, MODID + ".blockStability", DEFAULT_BLOCK_STABILITY, 0, Double.MAX_VALUE);
        AUTO_BARRIER = new RangedAttribute(null, MODID + ".autoBarrier", DEFAULT_AUTO_BARRIER, 0, Double.MAX_VALUE);
        BARRIER_EFFICIENCY = new RangedAttribute(null, MODID + ".barrierEfficiency", DEFAULT_BARRIER_EFFICIENCY, 0, Double.MAX_VALUE);
        DODGE_EFFICIENCY = new RangedAttribute(null, MODID + ".dodgeEfficiency", DEFAULT_DODGE_EFFICIENCY, 0, Double.MAX_VALUE);
        AUTO_DODGE = new RangedAttribute(null, MODID + ".autoDodge", DEFAULT_AUTO_DODGE, 0, Double.MAX_VALUE);
        PARRY = new RangedAttribute(null, MODID + ".parry", DEFAULT_PARRY, 0, Double.MAX_VALUE);
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

            attributeMap.registerAttribute(AUTO_BLOCK);
            attributeMap.registerAttribute(BLOCK_EFFICIENCY);
            attributeMap.registerAttribute(BLOCK_STABILITY);
            attributeMap.registerAttribute(AUTO_BARRIER);
            attributeMap.registerAttribute(BARRIER_EFFICIENCY);
            attributeMap.registerAttribute(DODGE_EFFICIENCY);
            attributeMap.registerAttribute(AUTO_DODGE);
            attributeMap.registerAttribute(PARRY);
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

            attributeMap.getAttributeInstance(AUTO_BLOCK).setBaseValue(AUTO_BLOCK.getDefaultValue());
            attributeMap.getAttributeInstance(BLOCK_EFFICIENCY).setBaseValue(BLOCK_EFFICIENCY.getDefaultValue());
            attributeMap.getAttributeInstance(BLOCK_STABILITY).setBaseValue(BLOCK_STABILITY.getDefaultValue());
            attributeMap.getAttributeInstance(AUTO_BARRIER).setBaseValue(AUTO_BARRIER.getDefaultValue());
            attributeMap.getAttributeInstance(BARRIER_EFFICIENCY).setBaseValue(BARRIER_EFFICIENCY.getDefaultValue());
            attributeMap.getAttributeInstance(DODGE_EFFICIENCY).setBaseValue(DODGE_EFFICIENCY.getDefaultValue());
            attributeMap.getAttributeInstance(AUTO_DODGE).setBaseValue(AUTO_DODGE.getDefaultValue());
            attributeMap.getAttributeInstance(PARRY).setBaseValue(PARRY.getDefaultValue());
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
