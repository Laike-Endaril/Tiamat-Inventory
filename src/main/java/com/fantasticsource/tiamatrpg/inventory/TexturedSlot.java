package com.fantasticsource.tiamatrpg.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class TexturedSlot extends Slot
{
    public final int u, v;
    public boolean enabled = true;

    public TexturedSlot(IInventory inventoryIn, int index, int x, int y, int u, int v)
    {
        super(inventoryIn, index, x, y);
        this.u = u;
        this.v = v;
    }

    public TexturedSlot enable()
    {
        enabled = true;
        return this;
    }

    public TexturedSlot disable()
    {
        enabled = false;
        return this;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public TexturedSlot setEnabled(boolean enabled)
    {
        if (enabled) enable();
        else disable();
        return this;
    }
}
