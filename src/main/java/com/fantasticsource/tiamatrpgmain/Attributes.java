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
            DEFAULT_MELEE_TARGETS = 1;

    public static RangedAttribute MELEE_DISTANCE;
    public static RangedAttribute MELEE_ANGLE;
    public static RangedAttribute MELEE_TARGETS;

    public static void init()
    {
        MELEE_DISTANCE = (RangedAttribute) EntityPlayer.REACH_DISTANCE; //generic.reachDistance
        MELEE_ANGLE = new RangedAttribute(null, MODID + ".meleeAngle", DEFAULT_MELEE_ANGLE, 0, Double.MAX_VALUE);
        MELEE_TARGETS = new RangedAttribute(null, MODID + ".meleeTargets", DEFAULT_MELEE_TARGETS, 0, Double.MAX_VALUE);
    }


    public static void addAttributes(EntityLivingBase livingBase)
    {
        AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();

        //Add new attributes to entity
        if (TiamatConfig.server.attributes.meleeAOEAttributes)
        {
            if (!(livingBase instanceof EntityPlayer)) attributeMap.registerAttribute(MELEE_DISTANCE).setBaseValue(DEFAULT_MELEE_DISTANCE);
            attributeMap.registerAttribute(MELEE_ANGLE);
            attributeMap.registerAttribute(MELEE_TARGETS);
        }
    }

    public static void editAttributes(EntityLivingBase livingBase)
    {
        AttributeMap attributeMap = (AttributeMap) livingBase.getAttributeMap();

        //Add new attributes to entity
        if (TiamatConfig.server.attributes.meleeAOEAttributes)
        {
            attributeMap.getAttributeInstance(MELEE_DISTANCE).setBaseValue(DEFAULT_MELEE_DISTANCE);
            attributeMap.getAttributeInstance(MELEE_ANGLE).setBaseValue(MELEE_ANGLE.getDefaultValue());
            attributeMap.getAttributeInstance(MELEE_TARGETS).setBaseValue(MELEE_TARGETS.getDefaultValue());
        }
    }
}
