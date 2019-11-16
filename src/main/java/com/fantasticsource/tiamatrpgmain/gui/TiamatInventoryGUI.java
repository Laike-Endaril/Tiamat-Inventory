package com.fantasticsource.tiamatrpgmain.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.fantasticsource.tiamatrpgmain.Keys.TIAMAT_INVENTORY_KEY;
import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

@SideOnly(Side.CLIENT)
public class TiamatInventoryGUI extends GuiContainer
{
    private static int tab = 0;
    private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "gui/inventory.png");
    private static final int TEXTURE_W = 512, TEXTURE_H = 512;
    private static final double U_PIXEL = 1d / TEXTURE_W, V_PIXEL = 1d / TEXTURE_H;

    private int uOffset, vOffset;

    public TiamatInventoryGUI()
    {
        super(new TiamatInventoryContainer(Minecraft.getMinecraft().player));
        allowUserInput = true;
    }

    private boolean buttonClicked;

    public void initGui()
    {
        setTab(tab);
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(guiLeft, guiTop + ySize, zLevel).tex(uOffset * U_PIXEL, (vOffset + ySize) * V_PIXEL).endVertex();
        bufferbuilder.pos(guiLeft + xSize, guiTop + ySize, zLevel).tex((uOffset + xSize) * U_PIXEL, (vOffset + ySize) * V_PIXEL).endVertex();
        bufferbuilder.pos(guiLeft + xSize, guiTop, zLevel).tex((uOffset + xSize) * U_PIXEL, vOffset * V_PIXEL).endVertex();
        bufferbuilder.pos(guiLeft, guiTop, zLevel).tex(uOffset * U_PIXEL, vOffset * V_PIXEL).endVertex();
        tessellator.draw();

        GuiInventory.drawEntityOnScreen(guiLeft + 60, guiTop + 66 + 30, 30, 0, 0, mc.player);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (buttonClicked) buttonClicked = false;
        else super.mouseReleased(mouseX, mouseY, state);
    }


    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent event)
    {
        if (TIAMAT_INVENTORY_KEY.isPressed() && TIAMAT_INVENTORY_KEY.getKeyConflictContext().isActive())
        {
            Minecraft.getMinecraft().displayGuiScreen(new TiamatInventoryGUI());
        }
    }

    private void setTab(int tab)
    {
        TiamatInventoryGUI.tab = tab;

        buttonList.clear();

        switch (tab)
        {
            case 0:
                xSize = 250;
                ySize = 214;
                uOffset = 0;
                vOffset = 0;
                break;

            case 1:
                xSize = 250;
                ySize = 214;
                uOffset = 0;
                vOffset = 256;
                break;

            case 2:
                xSize = 250;
                ySize = 214;
                uOffset = 256;
                vOffset = 0;
                break;
        }

        guiTop = (height - ySize) >> 1;
        guiLeft = (width - xSize) >> 1;

        //Tab buttons
        GuiButtonImage button = new GuiButtonImage(0, guiLeft + 230, guiTop + 4, 19, 31, TEXTURE_W - 19, TEXTURE_H - 31, 0, TEXTURE);
        buttonList.add(button);
        button = new GuiButtonImage(1, guiLeft + 230, guiTop + 36, 19, 31, TEXTURE_W - 19, TEXTURE_H - 31, 0, TEXTURE);
        buttonList.add(button);
        button = new GuiButtonImage(2, guiLeft + 230, guiTop + 68, 19, 31, TEXTURE_W - 19, TEXTURE_H - 31, 0, TEXTURE);
        buttonList.add(button);

        //Container
        inventorySlots = new TiamatInventoryContainer(Minecraft.getMinecraft().player);
    }

    protected void actionPerformed(GuiButton button)
    {
        buttonClicked = true;

        if (button.id <= 2) setTab(button.id);
    }
}
