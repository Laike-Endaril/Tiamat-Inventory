package com.fantasticsource.tiamatinventory.inventory.inventoryhacks;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ClientInventoryHacks extends GuiButton
{
    protected GuiContainer gui;
    protected boolean isTiamat;

    public ClientInventoryHacks(GuiContainer gui)
    {
        super(Integer.MIN_VALUE + 777, -10000, -10000, 0, 0, "");
        this.gui = gui;
        isTiamat = gui instanceof TiamatInventoryGUI;
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
            if (slot == null || !(slot.inventory instanceof InventoryPlayer)) continue;

            int slotIndex = slot.getSlotIndex();
            if (isTiamat)
            {
                if (slotIndex > 0 && (slotIndex < 9 || (slotIndex < 36 && !InventoryHacks.getAvailableClientInventorySlots().contains(slotIndex))))
                {
                    drawAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, slotIndex < 9);
                }
            }
            else
            {
                if (slotIndex < 9 || slotIndex >= 36 || !InventoryHacks.getAvailableClientInventorySlots().contains(slotIndex))
                {
                    drawAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, slotIndex < 9);
                }
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
            bufferbuilder.pos(x, y + 18, zLevel).tex(TiamatInventoryGUI.U_PIXEL * 544, TiamatInventoryGUI.V_PIXEL * (16 + 18)).endVertex();
            bufferbuilder.pos(x + 18, y + 18, zLevel).tex(TiamatInventoryGUI.U_PIXEL * (544 + 18), TiamatInventoryGUI.V_PIXEL * (16 + 18)).endVertex();
            bufferbuilder.pos(x + 18, y, zLevel).tex(TiamatInventoryGUI.U_PIXEL * (544 + 18), TiamatInventoryGUI.V_PIXEL * 16).endVertex();
            bufferbuilder.pos(x, y, zLevel).tex(TiamatInventoryGUI.U_PIXEL * 544, TiamatInventoryGUI.V_PIXEL * 16).endVertex();
        }
        else
        {
            bufferbuilder.pos(x, y + 18, zLevel).tex(TiamatInventoryGUI.U_PIXEL * 576, TiamatInventoryGUI.V_PIXEL * (16 + 18)).endVertex();
            bufferbuilder.pos(x + 18, y + 18, zLevel).tex(TiamatInventoryGUI.U_PIXEL * (576 + 18), TiamatInventoryGUI.V_PIXEL * (16 + 18)).endVertex();
            bufferbuilder.pos(x + 18, y, zLevel).tex(TiamatInventoryGUI.U_PIXEL * (576 + 18), TiamatInventoryGUI.V_PIXEL * 16).endVertex();
            bufferbuilder.pos(x, y, zLevel).tex(TiamatInventoryGUI.U_PIXEL * 576, TiamatInventoryGUI.V_PIXEL * 16).endVertex();
        }
        tessellator.draw();
    }

    @SubscribeEvent
    public static void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        GameType gameType = MCTools.getGameType(player);
        if (gameType == null || gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) return;

        Gui gui = event.getGui();
        if (!(gui instanceof GuiContainer)) return;

        Container container = ((GuiContainer) gui).inventorySlots;
        if (container == null) return;


        List<GuiButton> buttonList = event.getButtonList();
        boolean found = false;
        for (GuiButton button : buttonList)
        {
            if (button instanceof ClientInventoryHacks)
            {
                found = true;
                break;
            }
        }
        if (!found) buttonList.add(new ClientInventoryHacks((GuiContainer) gui));

        ArrayList<Integer> availableSlots = InventoryHacks.getAvailableClientInventorySlots();

        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null || !(slot.inventory instanceof InventoryPlayer)) continue;

            int slotIndex = slot.getSlotIndex();
            //Graphically block hotbar, vanilla offhand, armor, and blocked cargo slots
            if (container instanceof TiamatInventoryContainer)
            {
                if (slotIndex > 0 && (slotIndex < 9 || (slotIndex < 36 && !availableSlots.contains(slotIndex))))
                {
                    container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                }
            }
            else
            {
                if (slotIndex < 9 || slotIndex >= 36 || !availableSlots.contains(slotIndex))
                {
                    container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderHotbar(RenderGameOverlayEvent.Pre event)
    {
        GameType gameType = MCTools.getGameType(Minecraft.getMinecraft().player);
        if (gameType == null || gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) return;

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) event.setCanceled(true);
    }
}
