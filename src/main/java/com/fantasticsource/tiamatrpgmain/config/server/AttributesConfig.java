package com.fantasticsource.tiamatrpgmain.config.server;

import net.minecraftforge.common.config.Config;

public class AttributesConfig
{
    @Config.Name("Enable Melee AOE Attributes")
    @Config.Comment("Enables attributes which turn melee attacks into AOE cones")
    public boolean meleeAOEAttributes = true;
}
