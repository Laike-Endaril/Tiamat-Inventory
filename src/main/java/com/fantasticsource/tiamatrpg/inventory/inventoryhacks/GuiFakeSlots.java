package com.fantasticsource.tiamatrpg.inventory.inventoryhacks;

import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

public class GuiFakeSlots extends GuiButton
{
    protected GuiContainer gui;

    public GuiFakeSlots(GuiContainer gui)
    {
        super(Integer.MIN_VALUE + 777, -10000, 10000, 0, 0, "");
        this.gui = gui;
    }

    @Override
    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        mc.renderEngine.bindTexture(TiamatInventoryGUI.TEXTURE);

        for (int i = 0; i < gui.inventorySlots.inventorySlots.size(); i++)
        {
            Slot slot = gui.inventorySlots.inventorySlots.get(i);
            if (slot == null || !slot.isHere(mc.player.inventory, slot.getSlotIndex())) continue;

            int slotIndex = slot.getSlotIndex();
            if (slotIndex < 9 || slotIndex == 40)
            {
                drawAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, slotIndex < 9);
            }
        }

        GlStateManager.disableBlend();
    }

    protected void drawAt(int x, int y, boolean hotbarSlot)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        if (hotbarSlot)
        {
            bufferbuilder.pos(x, y + 18, zLevel).tex(TiamatInventoryGUI.U_PIXEL * 64, TiamatInventoryGUI.V_PIXEL * (512 + 18)).endVertex();
            bufferbuilder.pos(x + 18, y + 18, zLevel).tex(TiamatInventoryGUI.U_PIXEL * (64 + 18), TiamatInventoryGUI.V_PIXEL * (512 + 18)).endVertex();
            bufferbuilder.pos(x + 18, y, zLevel).tex(TiamatInventoryGUI.U_PIXEL * (64 + 18), TiamatInventoryGUI.V_PIXEL * 512).endVertex();
            bufferbuilder.pos(x, y, zLevel).tex(TiamatInventoryGUI.U_PIXEL * 64, TiamatInventoryGUI.V_PIXEL * 512).endVertex();
        }
        else
        {
            bufferbuilder.pos(x, y + 18, zLevel).tex(TiamatInventoryGUI.U_PIXEL * 32, TiamatInventoryGUI.V_PIXEL * (512 + 18)).endVertex();
            bufferbuilder.pos(x + 18, y + 18, zLevel).tex(TiamatInventoryGUI.U_PIXEL * (32 + 18), TiamatInventoryGUI.V_PIXEL * (512 + 18)).endVertex();
            bufferbuilder.pos(x + 18, y, zLevel).tex(TiamatInventoryGUI.U_PIXEL * (32 + 18), TiamatInventoryGUI.V_PIXEL * 512).endVertex();
            bufferbuilder.pos(x, y, zLevel).tex(TiamatInventoryGUI.U_PIXEL * 32, TiamatInventoryGUI.V_PIXEL * 512).endVertex();
        }
        tessellator.draw();
    }
}
