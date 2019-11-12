package com.fantasticsource.tiamatrpgmain;

import com.fantasticsource.tiamatrpgmain.config.TiamatConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;

import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

public class Attributes
{
    public static final double
            DEFAULT_MELEE_DISTANCE = 3,
            DEFAULT_MELEE_ANGLE = 0,
            DEFAULT_MELEE_TARGETS = 1,
            DEFAULT_MELEE_MODE = 0,
            DEFAULT_MELEE_BEST_DISTANCE = DEFAULT_MELEE_DISTANCE / 2,
            DEFAULT_MELEE_WIDTH = DEFAULT_MELEE_ANGLE / 2,
            DEFAULT_MELEE_MULTIPLIER_GOOD = 1,
            DEFAULT_MELEE_MULTIPLIER_BAD = 0;

    public static RangedAttribute
            MELEE_RANGE,
            MELEE_ANGLE,
            MELEE_TARGETS,
            MELEE_MODE,
            MELEE_BEST_DISTANCE,
            MELEE_WIDTH,
            MELEE_MULTIPLIER_GOOD,
            MELEE_MULTIPLIER_BAD;

    public static void init()
    {
        MELEE_RANGE = (RangedAttribute) EntityPlayer.REACH_DISTANCE; //generic.reachDistance
        MELEE_ANGLE = new RangedAttribute(null, MODID + ".meleeAngle", DEFAULT_MELEE_ANGLE, 0, Double.MAX_VALUE);
        MELEE_TARGETS = new RangedAttribute(null, MODID + ".meleeTargets", DEFAULT_MELEE_TARGETS, 0, Double.MAX_VALUE);

        MELEE_MODE = new RangedAttribute(null, MODID + ".meleeMode", DEFAULT_MELEE_MODE, 0, Double.MAX_VALUE);
        MELEE_BEST_DISTANCE = new RangedAttribute(null, MODID + ".meleeBestDistance", DEFAULT_MELEE_BEST_DISTANCE, 0, Double.MAX_VALUE);
        MELEE_WIDTH = new RangedAttribute(null, MODID + ".meleeWidth", DEFAULT_MELEE_WIDTH, 0, Double.MAX_VALUE);
        MELEE_MULTIPLIER_GOOD = new RangedAttribute(null, MODID + ".meleeMultiplierGood", DEFAULT_MELEE_MULTIPLIER_GOOD, 0, Double.MAX_VALUE);
        MELEE_MULTIPLIER_BAD = new RangedAttribute(null, MODID + ".meleeMultiplierBad", DEFAULT_MELEE_MULTIPLIER_BAD, 0, Double.MAX_VALUE);
    }


    public static void addAttributes(EntityLivingBase livingBase)
    {
        AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();

        //Add new attributes to entity
        if (TiamatConfig.server.attributes.meleeAOEAttributes)
        {
            if (!(livingBase instanceof EntityPlayer)) attributeMap.registerAttribute(MELEE_RANGE).setBaseValue(DEFAULT_MELEE_DISTANCE);
            attributeMap.registerAttribute(MELEE_ANGLE);
            attributeMap.registerAttribute(MELEE_TARGETS);

            attributeMap.registerAttribute(MELEE_MODE);
            attributeMap.registerAttribute(MELEE_BEST_DISTANCE);
            attributeMap.registerAttribute(MELEE_WIDTH);
            attributeMap.registerAttribute(MELEE_MULTIPLIER_GOOD);
            attributeMap.registerAttribute(MELEE_MULTIPLIER_BAD);
        }
    }

    public static void editAttributes(EntityLivingBase livingBase)
    {
        AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();

        //Add new attributes to entity
        if (TiamatConfig.server.attributes.meleeAOEAttributes)
        {
            attributeMap.getAttributeInstance(MELEE_RANGE).setBaseValue(DEFAULT_MELEE_DISTANCE);
            attributeMap.getAttributeInstance(MELEE_ANGLE).setBaseValue(MELEE_ANGLE.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_TARGETS).setBaseValue(MELEE_TARGETS.getDefaultValue());

            attributeMap.getAttributeInstance(MELEE_MODE).setBaseValue(MELEE_MODE.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_BEST_DISTANCE).setBaseValue(MELEE_BEST_DISTANCE.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_WIDTH).setBaseValue(MELEE_WIDTH.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_MULTIPLIER_GOOD).setBaseValue(MELEE_MULTIPLIER_GOOD.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_MULTIPLIER_BAD).setBaseValue(MELEE_MULTIPLIER_BAD.getDefaultValue());
        }
    }
}
