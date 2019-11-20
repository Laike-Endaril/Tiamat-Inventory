package com.fantasticsource.tiamatrpgmain.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class TexturedSlot extends Slot
{
    public final int u, v;

    public TexturedSlot(IInventory inventoryIn, int index, int x, int y, int u, int v)
    {
        super(inventoryIn, index, x, y);
        this.u = u;
        this.v = v;
    }
}
