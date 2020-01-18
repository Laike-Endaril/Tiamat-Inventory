package com.fantasticsource.tiamatrpg.action;

import java.util.LinkedHashMap;

public class Action
{
    public static LinkedHashMap<String, Action> actions = new LinkedHashMap<>();

    protected Action(String name)
    {
        actions.put(name, this);
    }

    public static Action getInstance(String name)
    {
        if (name == null || name.equals("") || name.equals("New Action")) throw new IllegalArgumentException("Action name must not be null, empty, or default (New Action)!");

        return new Action(name);
    }
}
