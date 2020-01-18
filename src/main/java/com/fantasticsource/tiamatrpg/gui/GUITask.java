package com.fantasticsource.tiamatrpg.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.tools.datastructures.Color;

public class GUITask extends GUIText
{
    public GUITask(GUIScreen screen)
    {
        this(screen, "Do Nothing");
    }

    public GUITask(GUIScreen screen, String text)
    {
        super(screen, text, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE);
    }
}
