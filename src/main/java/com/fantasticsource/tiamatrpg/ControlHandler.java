package com.fantasticsource.tiamatrpg;

import com.fantasticsource.mctools.controlintercept.ControlEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class ControlHandler
{
    protected static boolean skillset1Locked = false, skillset2Locked = false;

    @SubscribeEvent
    public static void controls(ControlEvent event)
    {
        if (event.identifier.equals(""))
        {
            //Client-side

            //Early exit for creative mode
            if (Minecraft.getMinecraft().player.isCreative()) return;

            if (event.state)
            {
                if (event.name.equals("key.attack"))
                {
                    //Mainhand / left click
                    if (Keys.SKILLSET_1.isKeyDown() && !skillset1Locked)
                    {
                        System.out.println("Skill 1");
                        skillset1Locked = true;
                        event.cancelOriginal();
                    }
                    else if (Keys.SKILLSET_2.isKeyDown() && !skillset2Locked)
                    {
                        System.out.println("Skill 4");
                        skillset2Locked = true;
                        event.cancelOriginal();
                    }
                    else if (Minecraft.getMinecraft().player.inventory.currentItem == 0)
                    {
                        System.out.println("Mainhand");
                        event.cancelOriginal();
                    }
                }
                else if (event.name.equals("key.use"))
                {
                    //Offhand / right click
                    if (Keys.SKILLSET_1.isKeyDown() && !skillset1Locked)
                    {
                        System.out.println("Skill 2");
                        skillset1Locked = true;
                        event.cancelOriginal();
                    }
                    else if (Keys.SKILLSET_2.isKeyDown() && !skillset2Locked)
                    {
                        System.out.println("Skill 5");
                        skillset2Locked = true;
                        event.cancelOriginal();
                    }
                    else if (Minecraft.getMinecraft().player.inventory.currentItem == 0)
                    {
                        System.out.println("Offhand");
                        event.cancelOriginal();
                    }
                }
            }
            else
            {
                if (event.name.equals(MODID + ".key.skillset1"))
                {
                    if (!skillset1Locked) System.out.println("Skill 0");
                    skillset1Locked = false;
                }
                else if (event.name.equals(MODID + ".key.skillset2"))
                {
                    if (!skillset2Locked) System.out.println("Skill 3");
                    skillset2Locked = false;
                }
            }
        }
    }
}
