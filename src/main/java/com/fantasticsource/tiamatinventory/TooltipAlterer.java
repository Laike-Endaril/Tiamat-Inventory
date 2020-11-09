package com.fantasticsource.tiamatinventory;

import com.fantasticsource.mctools.Slottings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class TooltipAlterer
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltips(ItemTooltipEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;

        ItemStack stack = event.getItemStack();
        if (Slottings.isTwoHanded(stack))
        {
            List<String> tooltip = event.getToolTip();
            tooltip.add(1, TextFormatting.GOLD + "Needs both hands!");
        }
    }
}
