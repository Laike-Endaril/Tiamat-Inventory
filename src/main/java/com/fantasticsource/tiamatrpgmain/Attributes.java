package com.fantasticsource.tiamatrpgmain;

import com.fantasticsource.tiamatrpgmain.config.TiamatConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;

import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

public class Attributes
{
    public static RangedAttribute MELEE_DISTANCE;
    public static RangedAttribute MELEE_ANGLE;
    public static RangedAttribute MELEE_TARGETS;

    public static void init()
    {
        if (TiamatConfig.server.attributes.meleeAOEAttributes)
        {
            MELEE_DISTANCE = (RangedAttribute) EntityPlayer.REACH_DISTANCE; //generic.reachDistance
            MELEE_ANGLE = new RangedAttribute(null, MODID + ".meleeAngle", 10, 0, Double.MAX_VALUE);
            MELEE_TARGETS = new RangedAttribute(null, MODID + ".meleeTargets", 1, 0, Double.MAX_VALUE);
        }
    }


    public static void addAttributes(EntityLivingBase livingBase)
    {
        //Add new attributes to entity
        if (TiamatConfig.server.attributes.meleeAOEAttributes)
        {
            //Distance is already added to players
            if (!(livingBase instanceof EntityPlayer)) livingBase.getAttributeMap().registerAttribute(Attributes.MELEE_DISTANCE).setBaseValue(3);
            livingBase.getAttributeMap().registerAttribute(Attributes.MELEE_ANGLE);
            livingBase.getAttributeMap().registerAttribute(Attributes.MELEE_TARGETS);
        }
    }
}
