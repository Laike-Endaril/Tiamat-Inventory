package com.fantasticsource.tiamatinventory.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class InterfaceTiamatInventory implements IInteractionObject
{
    public String getName()
    {
        return "inventory";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation("container.inventory");
    }

    public Container createContainer(InventoryPlayer ignored, EntityPlayer player)
    {
        return new TiamatInventoryContainer(player);
    }

    public String getGuiID()
    {
        return MODID + ":inventory";
    }
}
