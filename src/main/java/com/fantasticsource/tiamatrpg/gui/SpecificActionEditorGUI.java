package com.fantasticsource.tiamatrpg.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import net.minecraft.client.Minecraft;

public class SpecificActionEditorGUI extends GUIScreen
{
    protected String saveName = "New Action";

    protected static FilterBlacklist filter = new FilterBlacklist("New Action");

    public SpecificActionEditorGUI(String actionName)
    {
        show(actionName);
    }

    protected void show(String actionName)
    {
        Minecraft.getMinecraft().displayGuiScreen(this);


        //Background
        root.add(new GUIDarkenedBackground(this));


        //Header
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Name
        GUILabeledTextInput name = new GUILabeledTextInput(this, "Action Name: ", actionName, filter);
        name.input.addRecalcActions(() ->
        {
            if (name.valid() && !name.getText().equals(saveName))
            {
                saveName = name.getText();
                navbar.recalc(0);
            }
        });
        root.add(name);
    }

    @Override
    public String title()
    {
        return saveName + " (Action)";
    }
}
