package com.fantasticsource.tiamatrpgmain.inventory;

import com.fantasticsource.tiamatrpgmain.Network;
import com.fantasticsource.tiamatrpgmain.Network.OpenTiamatInventoryPacket;
import com.fantasticsource.tools.Collision;
import com.fantasticsource.tools.Tools;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientKeyPress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

import static com.fantasticsource.tiamatrpgmain.Keys.TIAMAT_INVENTORY_KEY;
import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

@SideOnly(Side.CLIENT)
public class TiamatInventoryGUI extends GuiContainer
{
    private final String[] stats;
    private static double statsScroll = 0;
    private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "gui/inventory.png");
    private static final int TEXTURE_W = 512, TEXTURE_H = 512;
    private static final int MODEL_WINDOW_X = 25, MODEL_WINDOW_Y = 22, MODEL_WINDOW_W = 70, MODEL_WINDOW_H = 88;
    private static final int STAT_WINDOW_X = 118, STAT_WINDOW_Y = 22, STAT_WINDOW_W = 99, STAT_WINDOW_H = 106;
    private static final int STAT_SCROLLBAR_X = 219, STAT_SCROLLBAR_Y = 22, STAT_SCROLLBAR_W = 5, STAT_SCROLLBAR_H = 106;
    private static final int STAT_SCROLLKNOB_H = 5;
    private static int lineHeight, statsHeight, difHeight;
    private static final double U_PIXEL = 1d / TEXTURE_W, V_PIXEL = 1d / TEXTURE_H;
    private static int tab = 0;
    private static boolean reopen = false;

    private boolean buttonClicked, statsScrollGrabbed = false, modelGrabbed = false;
    private int uOffset, vOffset, modelGrabX, modelGrabY;
    private double modelYaw = 0, modelPitch = 0, modelScale = 1;

    public TiamatInventoryGUI()
    {
        super(new TiamatInventoryContainer(Minecraft.getMinecraft().player));
        allowUserInput = true;
        stats = new String[]{
                "Level",
                "",
                "HP",
                "MP",
                "Stamina",
                "Hunger",
                "",
                "Strength",
                "Dexterity",
                "Constitution",
                "Intelligence",
                "Wisdom",
                "Charisma",
        };

        mc = Minecraft.getMinecraft();
        fontRenderer = mc.fontRenderer;
        lineHeight = fontRenderer.FONT_HEIGHT + 1;
        statsHeight = lineHeight * stats.length;
        difHeight = Tools.max(0, statsHeight - STAT_WINDOW_H);
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent event)
    {
        if (TIAMAT_INVENTORY_KEY.isPressed() && TIAMAT_INVENTORY_KEY.getKeyConflictContext().isActive())
        {
            Minecraft.getMinecraft().displayGuiScreen(new TiamatInventoryGUI());
            Network.WRAPPER.sendToServer(new OpenTiamatInventoryPacket());
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        if (reopen && Minecraft.getMinecraft().currentScreen == null)
        {
            reopen = false;
            Minecraft.getMinecraft().displayGuiScreen(new TiamatInventoryGUI());
        }
    }

    public void initGui()
    {
        inventorySlots = new TiamatInventoryContainer(Minecraft.getMinecraft().player);
        setTab(tab);
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (tab == 0)
        {
            //Render stats
            scissor(STAT_WINDOW_X, STAT_WINDOW_Y, STAT_WINDOW_W, STAT_WINDOW_H);

            int yy = STAT_WINDOW_Y;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, -statsScroll * difHeight, 0);
            for (String stat : stats)
            {
                drawString(fontRenderer, stat, STAT_WINDOW_X, yy, 0xffffffff);
                yy += lineHeight;
            }
            GlStateManager.popMatrix();

            unScissor();

            //Render scrollknob
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(TEXTURE);

            double scrollKnobY = STAT_SCROLLBAR_Y + (STAT_SCROLLBAR_H - STAT_SCROLLKNOB_H) * statsScroll;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(STAT_SCROLLBAR_X, scrollKnobY + STAT_SCROLLKNOB_H, zLevel).tex(288 * U_PIXEL, (240 + STAT_SCROLLKNOB_H) * V_PIXEL).endVertex();
            bufferbuilder.pos(STAT_SCROLLBAR_X + STAT_SCROLLBAR_W, scrollKnobY + STAT_SCROLLKNOB_H, zLevel).tex((288 + STAT_SCROLLBAR_W) * U_PIXEL, (240 + STAT_SCROLLKNOB_H) * V_PIXEL).endVertex();
            bufferbuilder.pos(STAT_SCROLLBAR_X + STAT_SCROLLBAR_W, scrollKnobY, zLevel).tex((288 + STAT_SCROLLBAR_W) * U_PIXEL, 240 * V_PIXEL).endVertex();
            bufferbuilder.pos(STAT_SCROLLBAR_X, scrollKnobY, zLevel).tex(288 * U_PIXEL, 240 * V_PIXEL).endVertex();
            tessellator.draw();
        }
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(guiLeft, guiTop + ySize, zLevel).tex(uOffset * U_PIXEL, (vOffset + ySize) * V_PIXEL).endVertex();
        bufferbuilder.pos(guiLeft + xSize, guiTop + ySize, zLevel).tex((uOffset + xSize) * U_PIXEL, (vOffset + ySize) * V_PIXEL).endVertex();
        bufferbuilder.pos(guiLeft + xSize, guiTop, zLevel).tex((uOffset + xSize) * U_PIXEL, vOffset * V_PIXEL).endVertex();
        bufferbuilder.pos(guiLeft, guiTop, zLevel).tex(uOffset * U_PIXEL, vOffset * V_PIXEL).endVertex();
        tessellator.draw();

        scissor(MODEL_WINDOW_X, MODEL_WINDOW_Y, MODEL_WINDOW_W, MODEL_WINDOW_H);
        drawEntityOnScreen(guiLeft + MODEL_WINDOW_X + (MODEL_WINDOW_W >> 1), guiTop + MODEL_WINDOW_Y + (MODEL_WINDOW_H >> 1), modelScale, modelYaw, modelPitch, mc.player);
        unScissor();
    }

    private void setTab(int tab)
    {
        TiamatInventoryGUI.tab = tab;

        buttonList.clear();

        TiamatInventoryContainer container = (TiamatInventoryContainer) inventorySlots;

        switch (tab)
        {
            case 0:
                //Stats
                xSize = 250;
                ySize = 232;
                uOffset = 0;
                vOffset = 0;
                for (TexturedSlot slot : container.classTabSlots) slot.disable();
                for (TexturedSlot slot : container.professionTabSlots) slot.disable();
                break;

            case 1:
                //Classes and skills
                xSize = 250;
                ySize = 232;
                uOffset = 0;
                vOffset = 256;
                for (TexturedSlot slot : container.classTabSlots) slot.enable();
                for (TexturedSlot slot : container.professionTabSlots) slot.disable();
                break;

            case 2:
                //Crafting
                xSize = 250;
                ySize = 232;
                uOffset = 256;
                vOffset = 0;
                for (TexturedSlot slot : container.classTabSlots) slot.disable();
                for (TexturedSlot slot : container.professionTabSlots) slot.enable();
                break;

            case 3:
                //Party
                xSize = 250;
                ySize = 232;
                uOffset = 256;
                vOffset = 256;
                for (TexturedSlot slot : container.classTabSlots) slot.disable();
                for (TexturedSlot slot : container.professionTabSlots) slot.disable();
                break;
        }

        guiTop = (height - ySize) >> 1;
        guiLeft = (width - xSize) >> 1;

        //Tab buttons
        GuiButtonImage button = new GuiButtonImage(0, guiLeft + 231, guiTop + 7, 18, 21, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE);
        buttonList.add(button);
        button = new GuiButtonImage(1, guiLeft + 231, guiTop + 32, 18, 21, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE);
        buttonList.add(button);
        button = new GuiButtonImage(2, guiLeft + 231, guiTop + 57, 18, 21, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE);
        buttonList.add(button);
        button = new GuiButtonImage(3, guiLeft + 231, guiTop + 82, 18, 21, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE);
        buttonList.add(button);
        button = new GuiButtonImage(4, guiLeft + 231, guiTop + 107, 18, 21, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE);
        buttonList.add(button);
    }

    protected void actionPerformed(GuiButton button)
    {
        buttonClicked = true;

        if (button.id <= 3) setTab(button.id);
        else if (button.id == 4)
        {
            reopen = true;
            PacketHandler.networkWrapper.sendToServer(new MessageClientKeyPress(MessageClientKeyPress.Button.OPEN_WARDROBE));
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int scroll = Mouse.getDWheel();
        if (scroll != 0)
        {
            ScaledResolution sr = new ScaledResolution(mc);

            int mouseX = Mouse.getX() / sr.getScaleFactor(), mouseY = sr.getScaledHeight() - Mouse.getY() / sr.getScaleFactor();
            if (Collision.pointRectangle(mouseX, mouseY, guiLeft + STAT_WINDOW_X, guiTop + STAT_WINDOW_Y, guiLeft + STAT_WINDOW_X + STAT_WINDOW_W, guiTop + STAT_WINDOW_Y + STAT_WINDOW_H)
                    || Collision.pointRectangle(mouseX, mouseY, guiLeft + STAT_SCROLLBAR_X, guiTop + STAT_SCROLLBAR_Y, guiLeft + STAT_SCROLLBAR_X + STAT_SCROLLBAR_W, guiTop + STAT_SCROLLBAR_Y + STAT_SCROLLBAR_H))
            {
                if (scroll > 0) statsScroll = Tools.max(0, statsScroll - (double) lineHeight / difHeight);
                else statsScroll = Tools.min(1, statsScroll + (double) lineHeight / difHeight);
            }
            else if (Collision.pointRectangle(mouseX, mouseY, guiLeft + MODEL_WINDOW_X, guiTop + MODEL_WINDOW_Y, guiLeft + MODEL_WINDOW_X + MODEL_WINDOW_W, guiTop + MODEL_WINDOW_Y + MODEL_WINDOW_H))
            {
                if (scroll > 0) modelScale *= 1.1;
                else modelScale /= 1.1;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0 && Collision.pointRectangle(mouseX - guiLeft, mouseY - guiTop, STAT_SCROLLBAR_X, STAT_SCROLLBAR_Y, STAT_SCROLLBAR_X + STAT_SCROLLBAR_W, STAT_SCROLLBAR_Y + STAT_SCROLLBAR_H))
        {
            statsScroll = Tools.min(Tools.max((mouseY - guiTop - STAT_SCROLLBAR_Y - (double) (STAT_SCROLLKNOB_H >> 1)) / (STAT_SCROLLBAR_H - STAT_SCROLLKNOB_H), 0), 1);
            statsScrollGrabbed = true;
        }
        else if (Collision.pointRectangle(mouseX - guiLeft, mouseY - guiTop, MODEL_WINDOW_X, MODEL_WINDOW_Y, MODEL_WINDOW_X + MODEL_WINDOW_W, MODEL_WINDOW_Y + MODEL_WINDOW_H))
        {
            modelGrabbed = true;
            modelGrabX = mouseX;
            modelGrabY = mouseY;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (statsScrollGrabbed)
        {
            statsScroll = Tools.min(Tools.max((mouseY - guiTop - STAT_SCROLLBAR_Y - (double) (STAT_SCROLLKNOB_H >> 1)) / (STAT_SCROLLBAR_H - STAT_SCROLLKNOB_H), 0), 1);
        }
        else if (modelGrabbed)
        {
            modelYaw += mouseX - modelGrabX;
            modelPitch += mouseY - modelGrabY;

            modelGrabX = mouseX;
            modelGrabY = mouseY;
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (buttonClicked) buttonClicked = false;
        else super.mouseReleased(mouseX, mouseY, state);

        statsScrollGrabbed = false;
        modelGrabbed = false;
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null && hoveredSlot.getHasStack() && (!(hoveredSlot instanceof TexturedSlot) || ((TexturedSlot) hoveredSlot).enabled))
        {
            renderToolTip(hoveredSlot.getStack(), mouseX, mouseY);
        }
    }

    @Override
    public void drawWorldBackground(int tint)
    {
        if (mc.world == null) drawBackground(tint);
    }

    @Override
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        //In this case, this is used for item slot highlighting only
        if (hoveredSlot == null || (hoveredSlot instanceof TexturedSlot && !((TexturedSlot) hoveredSlot).enabled)) return;

        //TODO Rarity color highlighting
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    @Override
    public void drawSlot(Slot slot)
    {
        if (slot instanceof TexturedSlot && !((TexturedSlot) slot).enabled) return;


        int x = slot.xPos;
        int y = slot.yPos;
        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
        ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
        String s = null;

        if (slot == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !itemstack.isEmpty())
        {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() >> 1);
        }
        else if (this.dragSplitting && this.dragSplittingSlots.contains(slot) && !itemstack1.isEmpty())
        {
            if (this.dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slot, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slot))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slot.getItemStackLimit(itemstack));

                if (itemstack.getCount() > k)
                {
                    s = TextFormatting.YELLOW.toString() + k;
                    itemstack.setCount(k);
                }
            }
            else
            {
                this.dragSplittingSlots.remove(slot);
                this.updateDragSplitting();
            }
        }

        this.zLevel = 100;
        this.itemRender.zLevel = 100;

        if (itemstack.isEmpty() && slot.isEnabled())
        {
            GlStateManager.disableLighting();

            if (slot instanceof TexturedSlot)
            {

                TexturedSlot texturedSlot = (TexturedSlot) slot;
                int u = texturedSlot.u, v = texturedSlot.v;

                mc.getTextureManager().bindTexture(TEXTURE);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(x, y + 16, zLevel).tex(u * U_PIXEL, (v + 16) * V_PIXEL).endVertex();
                bufferbuilder.pos(x + 16, y + 16, zLevel).tex((u + 16) * U_PIXEL, (v + 16) * V_PIXEL).endVertex();
                bufferbuilder.pos(x + 16, y, zLevel).tex((u + 16) * U_PIXEL, v * V_PIXEL).endVertex();
                bufferbuilder.pos(x, y, zLevel).tex(u * U_PIXEL, v * V_PIXEL).endVertex();
                tessellator.draw();
            }
            else
            {
                TextureAtlasSprite textureatlassprite = slot.getBackgroundSprite();
                if (textureatlassprite != null)
                {
                    mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
                    drawTexturedModalRect(x, y, textureatlassprite, 16, 16);
                }
            }

            GlStateManager.enableLighting();
            flag1 = true;
        }

        if (!flag1)
        {
            if (flag)
            {
                drawRect(x, y, x + 16, y + 16, -2130706433);
            }

            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, itemstack, x, y);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, x, y, s);
        }

        this.itemRender.zLevel = 0;
        this.zLevel = 0;
    }

    public void updateDragSplitting()
    {
        ItemStack itemstack = this.mc.player.inventory.getItemStack();

        if (!itemstack.isEmpty() && this.dragSplitting)
        {
            if (this.dragSplittingLimit == 2)
            {
                this.dragSplittingRemnant = itemstack.getMaxStackSize();
            }
            else
            {
                this.dragSplittingRemnant = itemstack.getCount();

                for (Slot slot : this.dragSplittingSlots)
                {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemstack2 = slot.getStack();
                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);
                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));

                    if (itemstack1.getCount() > j)
                    {
                        itemstack1.setCount(j);
                    }

                    this.dragSplittingRemnant -= itemstack1.getCount() - i;
                }
            }
        }
    }

    public static void drawEntityOnScreen(int posX, int posY, double scale, double yaw, double pitch, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50);
        GlStateManager.scale((float) (-30), (float) 30, (float) 30);
        GlStateManager.rotate(180, 0, 0, 1);

        GlStateManager.rotate((float) pitch, 1, 0, 0);
        GlStateManager.rotate((float) yaw + ent.renderYawOffset, 0, 1, 0);
        GlStateManager.scale((float) (scale), (float) scale, (float) scale);

        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0, -ent.height / 2, 0, 0, 1, false);
        rendermanager.setRenderShadow(true);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private void scissor(int x, int y, int w, int h)
    {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scale = sr.getScaleFactor();
        GL11.glEnable(GL_SCISSOR_TEST);
        GL11.glScissor((guiLeft + x) * scale, (sr.getScaledHeight() - (guiTop + y + h)) * scale, w * scale, h * scale);
    }

    private void unScissor()
    {
        GL11.glDisable(GL_SCISSOR_TEST);
    }
}
