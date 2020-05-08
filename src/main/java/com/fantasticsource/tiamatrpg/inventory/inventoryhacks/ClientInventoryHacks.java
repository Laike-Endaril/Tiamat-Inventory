package com.fantasticsource.tiamatrpg.inventory.inventoryhacks;

import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ClientInventoryHacks extends GuiButton
{
    protected GuiContainer gui;

    public ClientInventoryHacks(GuiContainer gui)
    {
        super(Integer.MIN_VALUE + 777, -10000, -10000, 0, 0, "");
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
            if (slotIndex < 9 || slotIndex == 40 || (slotIndex < 36 && !InventoryHacks.getAvailableClientInventorySlots().contains(slotIndex)))
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

    @SubscribeEvent
    public static void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        Gui gui = event.getGui();
        if (gui instanceof TiamatInventoryGUI || !(gui instanceof GuiContainer)) return;

        event.getButtonList().add(new ClientInventoryHacks((GuiContainer) gui));
    }

    @SubscribeEvent
    public static void guiOpen(GuiOpenEvent event)
    {
        Gui gui = event.getGui();
        if (!(gui instanceof GuiContainer)) return;

        Minecraft mc = Minecraft.getMinecraft();
        GuiContainer guiContainer = (GuiContainer) gui;
        for (int i = 0; i < guiContainer.inventorySlots.inventorySlots.size(); i++)
        {
            Slot slot = guiContainer.inventorySlots.inventorySlots.get(i);
            if (slot == null || !slot.isHere(mc.player.inventory, slot.getSlotIndex())) continue;

            int slotIndex = slot.getSlotIndex();
            //Graphically block hotbar, vanilla offhand, armor, and blocked cargo slots
            if (slotIndex < 9 || slotIndex >= 36 || !InventoryHacks.getAvailableClientInventorySlots().contains(slotIndex))
            {
                guiContainer.inventorySlots.inventorySlots.set(i, new FakeSlot(mc.player.inventory, slotIndex, slot.xPos, slot.yPos));
            }
        }
    }
}
