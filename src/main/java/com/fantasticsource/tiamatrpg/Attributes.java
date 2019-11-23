package com.fantasticsource.tiamatrpg;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.RangedAttribute;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class Attributes
{
    public static final double
            DEFAULT_MELEE_ANGLE = 0,
            DEFAULT_MELEE_TARGETS = 1,
            DEFAULT_MELEE_MODE = 0,
            DEFAULT_MELEE_BEST_DISTANCE = 1.5,
            DEFAULT_MELEE_TOLERANCE = 1.5,
            DEFAULT_MELEE_MULTIPLIER_GOOD = 1,
            DEFAULT_MELEE_MULTIPLIER_BAD = 0,
            DEFAULT_BLOCK = 0,
            DEFAULT_PARRY = 0,
            DEFAULT_DODGE = 0;

    public static RangedAttribute
            MELEE_ANGLE,
            MELEE_TARGETS,
            MELEE_MODE,
            MELEE_BEST_DISTANCE,
            MELEE_TOLERANCE,
            MELEE_MULTIPLIER_GOOD,
            MELEE_MULTIPLIER_BAD,
            BLOCK,
            PARRY,
            DODGE;

    public static void init()
    {
        MELEE_ANGLE = new RangedAttribute(null, MODID + ".meleeAngle", DEFAULT_MELEE_ANGLE, 0, Double.MAX_VALUE);
        MELEE_TARGETS = new RangedAttribute(null, MODID + ".meleeTargets", DEFAULT_MELEE_TARGETS, 0, Double.MAX_VALUE);

        MELEE_MODE = new RangedAttribute(null, MODID + ".meleeMode", DEFAULT_MELEE_MODE, 0, Double.MAX_VALUE);
        MELEE_BEST_DISTANCE = new RangedAttribute(null, MODID + ".meleeBestDistance", DEFAULT_MELEE_BEST_DISTANCE, 0, Double.MAX_VALUE);
        MELEE_TOLERANCE = new RangedAttribute(null, MODID + ".meleeTolerance", DEFAULT_MELEE_TOLERANCE, 0, Double.MAX_VALUE);
        MELEE_MULTIPLIER_GOOD = new RangedAttribute(null, MODID + ".meleeMultiplierGood", DEFAULT_MELEE_MULTIPLIER_GOOD, 0, Double.MAX_VALUE);
        MELEE_MULTIPLIER_BAD = new RangedAttribute(null, MODID + ".meleeMultiplierBad", DEFAULT_MELEE_MULTIPLIER_BAD, 0, Double.MAX_VALUE);

        BLOCK = new RangedAttribute(null, MODID + ".block", DEFAULT_BLOCK, 0, Double.MAX_VALUE);
        PARRY = new RangedAttribute(null, MODID + ".parry", DEFAULT_PARRY, 0, Double.MAX_VALUE);
        DODGE = new RangedAttribute(null, MODID + ".dodge", DEFAULT_DODGE, 0, Double.MAX_VALUE);
    }


    public static void addAttributes(EntityLivingBase livingBase)
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

        attributeMap.registerAttribute(BLOCK);
        attributeMap.registerAttribute(PARRY);
        attributeMap.registerAttribute(DODGE);
    }

    public static void editAttributes(EntityLivingBase livingBase)
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

        attributeMap.getAttributeInstance(BLOCK).setBaseValue(BLOCK.getDefaultValue());
        attributeMap.getAttributeInstance(PARRY).setBaseValue(PARRY.getDefaultValue());
        attributeMap.getAttributeInstance(DODGE).setBaseValue(DODGE.getDefaultValue());
    }

    public static String[][] getDisplayList(EntityLivingBase livingBase)
    {
        String[][] result = new String[2][];
        result[0] = new String[]{
                "Level",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Strength",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
        };
        result[1] = new String[]{
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "How much you lift\nlift\nlift\nlift\nlift\nlift\nlift\nlift\nlift\nlift\nlift\nlift",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
                "Test",
        };

        return result;
    }
}
