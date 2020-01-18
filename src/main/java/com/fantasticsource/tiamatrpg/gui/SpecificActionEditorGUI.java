package com.fantasticsource.tiamatrpg.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatrpg.action.Action;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.client.Minecraft;

public class SpecificActionEditorGUI extends GUIScreen
{
    public String saveName = "New Action";
    protected String initialName;

    protected static FilterBlacklist nameFilter = new FilterBlacklist("New Action");
    protected GUILabeledTextInput name;
    protected GUIList subActions;
    protected boolean reordering = false;

    public SpecificActionEditorGUI(String actionName)
    {
        show(actionName);
    }

    protected void show(String actionName)
    {
        Minecraft.getMinecraft().displayGuiScreen(this);
        initialName = actionName;


        //Background
        root.add(new GUIDarkenedBackground(this));


        //Header
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Name
        name = new GUILabeledTextInput(this, "Action Name: ", actionName, nameFilter);
        name.input.addRecalcActions(() ->
        {
            if (name.valid() && !name.getText().equals(saveName))
            {
                saveName = name.getText();
                navbar.recalc(0);
            }
        });
        root.add(name);


        //List of subactions
        subActions = new GUIList(this, true, 0.98, 1 - (name.y + name.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUILabeledTextInput time = new GUILabeledTextInput(screen, "Time: ", "0", FilterInt.INSTANCE);
                time.addEditActions(() ->
                {
                    reorder();
                    subActions.focus(time.parent);
                });
                return new GUIElement[]{time};
            }

            @Override
            public GUIList addLine(int index, GUIElement... lineElements)
            {
                super.addLine(index, lineElements);
                Line line = getLine(index);

                reorder();

                root.setActiveRecursive(false);
                ((GUILabeledTextInput) line.getLineElement(0)).input.setActive(true);
                focus(line);

                return this;
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1 - (name.y + name.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, subActions);
        root.addAll(subActions, scrollbar);

        name.addRecalcActions(() ->
        {
            double h = 1 - (name.y + name.height);
            subActions.height = h;
            scrollbar.height = h;
        });
    }

    @Override
    public String title()
    {
        return saveName + " (Action)";
    }

    public Action getAction()
    {
        if (!name.valid()) return null;

        if (!name.getText().equals(initialName))
        {
            //TODO warn about overwriting other action of same name
            return null;
        }

        //TODO return new action instead of null
        return null;
    }

    protected void reorder()
    {
        if (reordering) return;
        reordering = true;


        GUIList.Line[] lines = subActions.getLines();

        ExplicitPriorityQueue<GUIList.Line> lineQueue = new ExplicitPriorityQueue<>(subActions.lineCount());
        for (GUIList.Line line : lines)
        {
            GUILabeledTextInput time = (GUILabeledTextInput) line.getLineElement(0);
            if (!time.valid())
            {
                reordering = false;
                return;
            }

            lineQueue.add(line, FilterInt.INSTANCE.parse(time.getText()));
        }

        for (GUIList.Line line : lines) subActions.remove(line);

        int offset = subActions.size();
        while (lineQueue.size() > 0) subActions.add(subActions.size() - offset, lineQueue.poll());


        root.recalc(0);


        reordering = false;
    }
}
