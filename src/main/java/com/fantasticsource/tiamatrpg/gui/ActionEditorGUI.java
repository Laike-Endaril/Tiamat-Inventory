package com.fantasticsource.tiamatrpg.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class ActionEditorGUI extends GUIScreen
{
    public static void show()
    {
        ActionEditorGUI gui = new ActionEditorGUI();
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Background
        gui.root.add(new GUIGradient(gui, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        gui.root.add(navbar);
    }

    @Override
    public String title()
    {
        return "Action Editor";
    }
}
