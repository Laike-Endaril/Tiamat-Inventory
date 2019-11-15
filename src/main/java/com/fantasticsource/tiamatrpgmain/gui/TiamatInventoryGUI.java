package com.fantasticsource.tiamatrpgmain.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import static com.fantasticsource.tiamatrpgmain.Keys.TIAMAT_INVENTORY_KEY;

public class TiamatInventoryGUI extends GuiContainer
{
    public TiamatInventoryGUI(EntityPlayer player)
    {
        super(player.inventoryContainer);
        allowUserInput = true;
    }

    private float oldMouseX, oldMouseY;
    private boolean buttonClicked;

    public void updateScreen()
    {
        if (mc.playerController.isInCreativeMode())
        {
            mc.displayGuiScreen(new GuiContainerCreative(mc.player));
        }
    }

    public void initGui()
    {
        buttonList.clear();

        if (mc.playerController.isInCreativeMode())
        {
            mc.displayGuiScreen(new GuiContainerCreative(mc.player));
        }
        else
        {
            super.initGui();
        }

        guiLeft = (width - xSize) >> 1;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
        oldMouseX = (float) mouseX;
        oldMouseY = (float) mouseY;
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = guiLeft;
        int j = guiTop;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
        drawEntityOnScreen(i + 51, j + 75, 30, (float) (i + 51) - oldMouseX, (float) (j + 75 - 50) - oldMouseY, mc.player);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180, 0, 0, 1);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135, 0, 1, 0);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135, 0, 1, 0);
        GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40))) * 20, 1, 0, 0);
        ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40)) * 20;
        ent.rotationYaw = (float) Math.atan((double) (mouseX / 40)) * 40;
        ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40))) * 20;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0, 0, 0);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0, 0, 0, 0, 1, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
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
            Minecraft.getMinecraft().displayGuiScreen(new TiamatInventoryGUI(Minecraft.getMinecraft().player));
        }
    }
}
