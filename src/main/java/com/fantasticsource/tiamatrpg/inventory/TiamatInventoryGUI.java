package com.fantasticsource.tiamatrpg.inventory;

import com.fantasticsource.tiamatrpg.AttributeDisplayData;
import com.fantasticsource.tiamatrpg.Keys;
import com.fantasticsource.tools.Collision;
import com.fantasticsource.tools.Tools;
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
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Arrays;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

@SideOnly(Side.CLIENT)
public class TiamatInventoryGUI extends GuiContainer
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "gui/inventory.png");
    public static final int TEXTURE_W = 1024, TEXTURE_H = 1024;
    public static final int MODEL_WINDOW_X = 43, MODEL_WINDOW_Y = 6, MODEL_WINDOW_W = 88, MODEL_WINDOW_H = 106;
    public static final int STAT_WINDOW_X = 118000, STAT_WINDOW_Y = 22000, STAT_WINDOW_W = 99, STAT_WINDOW_H = 106;
    public static final int STAT_SCROLLBAR_X = 219000, STAT_SCROLLBAR_Y = 22000, STAT_SCROLLBAR_W = 5, STAT_SCROLLBAR_H = 106;
    public static final int STAT_SCROLLKNOB_H = 5;
    public static final double U_PIXEL = 1d / TEXTURE_W, V_PIXEL = 1d / TEXTURE_H;

    protected static double statsScroll = 0;
    protected static int statLineHeight;
    protected static int statHeightDif;
    protected int tab = 0;
    protected String[] stats, statTooltips;
    protected boolean buttonClicked, statsScrollGrabbed = false, modelGrabbed = false;
    protected int uOffset, vOffset, modelGrabX, modelGrabY;
    protected double modelYaw = 0, modelPitch = 0, modelScale = 1;
    protected Slot hoveredSlot;
    protected ItemStack draggedStack = ItemStack.EMPTY;
    protected boolean isRightMouseClick;
    protected int dragSplittingRemnant;
    protected ItemStack returningStack = ItemStack.EMPTY;
    protected long returningStackTime;
    protected Slot returningStackDestSlot;
    protected int touchUpX;
    protected int touchUpY;
    protected boolean doubleClick;
    protected Slot lastClickSlot;
    protected long lastClickTime;
    protected int lastClickButton;
    protected boolean ignoreMouseUp;
    protected Slot clickedSlot;
    protected ItemStack shiftClickedStack = ItemStack.EMPTY;
    protected int dragSplittingButton;
    protected int dragSplittingLimit;
    protected Slot currentDragTargetSlot;
    protected long dragItemDropDelay;

    public TiamatInventoryGUI()
    {
        super(new TiamatInventoryContainer(Minecraft.getMinecraft().player));
        ignoreMouseUp = true;

        allowUserInput = true;

        mc = Minecraft.getMinecraft();
        stats = new String[AttributeDisplayData.displayAttributes.length];
        statTooltips = new String[stats.length];
        for (int i = 0; i < stats.length; i++)
        {
            stats[i] = I18n.translateToLocal("attribute.name." + AttributeDisplayData.displayAttributes[i]);
            statTooltips[i] = I18n.translateToLocal(AttributeDisplayData.displayAttributeDescriptions[i]);
        }

        fontRenderer = mc.fontRenderer;
        statLineHeight = fontRenderer.FONT_HEIGHT + 1;
        statHeightDif = Tools.max(0, statLineHeight * stats.length - STAT_WINDOW_H);
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

    @Override
    public void initGui()
    {
        setTab(tab);

        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
    }

    @Override
    public void onGuiClosed()
    {
        if (mc.player != null && inventorySlots != null) inventorySlots.onContainerClosed(mc.player);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        for (int i = 0; i < buttonList.size(); ++i)
        {
            buttonList.get(i).drawButton(mc, mouseX, mouseY, partialTicks);
        }

        for (int j = 0; j < labelList.size(); ++j)
        {
            labelList.get(j).drawLabel(mc, mouseX, mouseY);
        }

        if (inventorySlots != null)
        {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) guiLeft, (float) guiTop, 0);
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableRescaleNormal();
            hoveredSlot = null;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            GlStateManager.color(1, 1, 1, 1);

            for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); ++i1)
            {
                Slot slot = inventorySlots.inventorySlots.get(i1);

                if (slot.isEnabled())
                {
                    drawSlot(slot);
                }

                if (isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled())
                {
                    hoveredSlot = slot;
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.colorMask(true, true, true, false);
                    drawGradientRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }
            }

            RenderHelper.disableStandardItemLighting();
            drawGuiContainerForegroundLayer(mouseX, mouseY);
            RenderHelper.enableGUIStandardItemLighting();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, mouseX, mouseY));
            InventoryPlayer inventoryplayer = mc.player.inventory;
            ItemStack itemstack = draggedStack.isEmpty() ? inventoryplayer.getItemStack() : draggedStack;

            if (!itemstack.isEmpty())
            {
                String s = null;

                if (!draggedStack.isEmpty() && isRightMouseClick)
                {
                    itemstack = itemstack.copy();
                    itemstack.setCount(MathHelper.ceil((float) itemstack.getCount() / 2));
                }
                else if (dragSplitting && dragSplittingSlots.size() > 1)
                {
                    itemstack = itemstack.copy();
                    itemstack.setCount(dragSplittingRemnant);

                    if (itemstack.isEmpty())
                    {
                        s = "" + TextFormatting.YELLOW + "0";
                    }
                }

                drawItemStack(itemstack, mouseX - guiLeft - 8, mouseY - guiTop - (draggedStack.isEmpty() ? 8 : 16), s);
            }

            if (!returningStack.isEmpty())
            {
                float f = (float) (Minecraft.getSystemTime() - returningStackTime) / 100;

                if (f >= 1)
                {
                    f = 1;
                    returningStack = ItemStack.EMPTY;
                }

                int l2 = returningStackDestSlot.xPos - touchUpX;
                int i3 = returningStackDestSlot.yPos - touchUpY;
                int l1 = touchUpX + (int) ((float) l2 * f);
                int i2 = touchUpY + (int) ((float) i3 * f);
                drawItemStack(returningStack, l1, i2, null);
            }

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();

            renderHoveredToolTip(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (tab == 0)
        {
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


            //Render stats
            scissor(STAT_WINDOW_X, STAT_WINDOW_Y, STAT_WINDOW_W, STAT_WINDOW_H);

            int yy = STAT_WINDOW_Y;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, -statsScroll * statHeightDif, 0);
            for (String stat : stats)
            {
                IAttributeInstance attributeInstance = mc.player.getAttributeMap().getAttributeInstanceByName(stat);
                if (attributeInstance == null) continue;

                drawString(fontRenderer, stat + ": " + attributeInstance.getAttributeValue(), STAT_WINDOW_X, yy, 0xffffffff);
                yy += statLineHeight;
            }
            GlStateManager.popMatrix();

            unScissor();


            //Render stat tooltips
            if (Collision.pointRectangle(mouseX - guiLeft, mouseY - guiTop, STAT_WINDOW_X, STAT_WINDOW_Y, STAT_WINDOW_X + STAT_WINDOW_W, STAT_WINDOW_Y + STAT_WINDOW_H))
            {
                int index = (int) ((mouseY - guiTop - STAT_WINDOW_Y + statHeightDif * statsScroll) / statLineHeight);
                if (index >= 0 && index < statTooltips.length)
                {
                    drawHoveringText(Arrays.asList(Tools.fixedSplit(statTooltips[index], "\n")), mouseX - guiLeft, mouseY - guiTop, fontRenderer);
                }
            }
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

        if (tab == 0)
        {
            scissor(MODEL_WINDOW_X, MODEL_WINDOW_Y, MODEL_WINDOW_W, MODEL_WINDOW_H);
            drawEntityOnScreen(guiLeft + MODEL_WINDOW_X + (MODEL_WINDOW_W >> 1), guiTop + MODEL_WINDOW_Y + (MODEL_WINDOW_H >> 1), modelScale, modelYaw, modelPitch, mc.player);
            unScissor();
        }
    }

    private void setTab(int tab)
    {
        inventorySlots = tab == 0 ? new TiamatInventoryContainer(Minecraft.getMinecraft().player) : null;
        mc.player.openContainer = inventorySlots;

        this.tab = tab;

        buttonList.clear();

        xSize = 318;
        ySize = 136;
        uOffset = 0;
        vOffset = 136 * tab;

        guiTop = (height - ySize) >> 1;
        guiLeft = (width - xSize) >> 1;

        //Tab buttons
        buttonList.add(new GuiButtonImage(0, guiLeft + 299, guiTop + 7, 19, 23, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE));
        buttonList.add(new GuiButtonImage(1, guiLeft + 299, guiTop + 32, 19, 23, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE));
        buttonList.add(new GuiButtonImage(2, guiLeft + 299, guiTop + 57, 19, 23, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE));
        buttonList.add(new GuiButtonImage(3, guiLeft + 299, guiTop + 82, 19, 23, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE));
        buttonList.add(new GuiButtonImage(4, guiLeft + 299, guiTop + 107, 19, 23, TEXTURE_W - 18, TEXTURE_H - 21, 0, TEXTURE));

        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Post(this, buttonList));
    }

    protected void actionPerformed(GuiButton button)
    {
        buttonClicked = true;

        if (button.id <= 5) setTab(button.id);
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
                if (scroll > 0) statsScroll = Tools.max(0, statsScroll - (double) statLineHeight / statHeightDif);
                else statsScroll = Tools.min(1, statsScroll + (double) statLineHeight / statHeightDif);
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
        if (mouseButton == 0)
        {
            for (int i = 0; i < buttonList.size(); ++i)
            {
                GuiButton guibutton = buttonList.get(i);

                if (guibutton.mousePressed(mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, buttonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    selectedButton = guibutton;
                    guibutton.playPressSound(mc.getSoundHandler());
                    actionPerformed(guibutton);
                    if (equals(mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), buttonList));
                }
            }
        }


        boolean flag = mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100);
        Slot slot = getSlotAtPosition(mouseX, mouseY);
        long i = Minecraft.getSystemTime();
        doubleClick = lastClickSlot == slot && i - lastClickTime < 250L && lastClickButton == mouseButton;
        ignoreMouseUp = false;

        if (mouseButton == 0 || mouseButton == 1 || flag)
        {
            int j = guiLeft;
            int k = guiTop;
            boolean flag1 = hasClickedOutside(mouseX, mouseY, j, k);
            if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
            int l = -1;

            if (slot != null)
            {
                l = slot.slotNumber;
            }

            if (flag1)
            {
                l = -999;
            }

            if (mc.gameSettings.touchscreen && flag1 && mc.player.inventory.getItemStack().isEmpty())
            {
                mc.displayGuiScreen(null);
                return;
            }

            if (l != -1)
            {
                if (mc.gameSettings.touchscreen)
                {
                    if (slot != null && slot.getHasStack())
                    {
                        clickedSlot = slot;
                        draggedStack = ItemStack.EMPTY;
                        isRightMouseClick = mouseButton == 1;
                    }
                    else
                    {
                        clickedSlot = null;
                    }
                }
                else if (!dragSplitting)
                {
                    if (mc.player.inventory.getItemStack().isEmpty())
                    {
                        if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))
                        {
                            handleMouseClick(slot, l, mouseButton, ClickType.CLONE);
                        }
                        else
                        {
                            boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            ClickType clicktype = ClickType.PICKUP;

                            if (flag2)
                            {
                                shiftClickedStack = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                                clicktype = ClickType.QUICK_MOVE;
                            }
                            else if (l == -999)
                            {
                                clicktype = ClickType.THROW;
                            }

                            handleMouseClick(slot, l, mouseButton, clicktype);
                        }

                        ignoreMouseUp = true;
                    }
                    else
                    {
                        dragSplitting = true;
                        dragSplittingButton = mouseButton;
                        dragSplittingSlots.clear();

                        if (mouseButton == 0)
                        {
                            dragSplittingLimit = 0;
                        }
                        else if (mouseButton == 1)
                        {
                            dragSplittingLimit = 1;
                        }
                        else if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))
                        {
                            dragSplittingLimit = 2;
                        }
                    }
                }
            }
        }

        lastClickSlot = slot;
        lastClickTime = i;
        lastClickButton = mouseButton;


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
        Slot slot = getSlotAtPosition(mouseX, mouseY);
        ItemStack itemstack = mc.player.inventory.getItemStack();

        if (clickedSlot != null && mc.gameSettings.touchscreen)
        {
            if (clickedMouseButton == 0 || clickedMouseButton == 1)
            {
                if (draggedStack.isEmpty())
                {
                    if (slot != clickedSlot && !clickedSlot.getStack().isEmpty())
                    {
                        draggedStack = clickedSlot.getStack().copy();
                    }
                }
                else if (draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, draggedStack, false))
                {
                    long i = Minecraft.getSystemTime();

                    if (currentDragTargetSlot == slot)
                    {
                        if (i - dragItemDropDelay > 500L)
                        {
                            handleMouseClick(clickedSlot, clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
                            handleMouseClick(clickedSlot, clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            dragItemDropDelay = i + 750L;
                            draggedStack.shrink(1);
                        }
                    }
                    else
                    {
                        currentDragTargetSlot = slot;
                        dragItemDropDelay = i;
                    }
                }
            }
        }
        else if (dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > dragSplittingSlots.size() || dragSplittingLimit == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && inventorySlots.canDragIntoSlot(slot))
        {
            dragSplittingSlots.add(slot);
            updateDragSplitting();
        }

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

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (buttonClicked) buttonClicked = false;
        else
        {
            if (selectedButton != null && state == 0)
            {
                selectedButton.mouseReleased(mouseX, mouseY);
                selectedButton = null;
            }

            Slot slot = getSlotAtPosition(mouseX, mouseY);
            int i = guiLeft;
            int j = guiTop;
            boolean flag = hasClickedOutside(mouseX, mouseY, i, j);
            if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
            int k = -1;

            if (slot != null)
            {
                k = slot.slotNumber;
            }

            if (flag)
            {
                k = -999;
            }

            if (doubleClick && slot != null && state == 0 && inventorySlots.canMergeSlot(ItemStack.EMPTY, slot))
            {
                if (isShiftKeyDown())
                {
                    if (!shiftClickedStack.isEmpty())
                    {
                        for (Slot slot2 : inventorySlots.inventorySlots)
                        {
                            if (slot2 != null && slot2.canTakeStack(mc.player) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, shiftClickedStack, true))
                            {
                                handleMouseClick(slot2, slot2.slotNumber, state, ClickType.QUICK_MOVE);
                            }
                        }
                    }
                }
                else
                {
                    handleMouseClick(slot, k, state, ClickType.PICKUP_ALL);
                }

                doubleClick = false;
                lastClickTime = 0L;
            }
            else
            {
                if (dragSplitting && dragSplittingButton != state)
                {
                    dragSplitting = false;
                    dragSplittingSlots.clear();
                    ignoreMouseUp = true;
                    return;
                }

                if (ignoreMouseUp)
                {
                    ignoreMouseUp = false;
                    return;
                }

                if (clickedSlot != null && mc.gameSettings.touchscreen)
                {
                    if (state == 0 || state == 1)
                    {
                        if (draggedStack.isEmpty() && slot != clickedSlot)
                        {
                            draggedStack = clickedSlot.getStack();
                        }

                        boolean flag2 = Container.canAddItemToSlot(slot, draggedStack, false);

                        if (k != -1 && !draggedStack.isEmpty() && flag2)
                        {
                            handleMouseClick(clickedSlot, clickedSlot.slotNumber, state, ClickType.PICKUP);
                            handleMouseClick(slot, k, 0, ClickType.PICKUP);

                            if (mc.player.inventory.getItemStack().isEmpty())
                            {
                                returningStack = ItemStack.EMPTY;
                            }
                            else
                            {
                                handleMouseClick(clickedSlot, clickedSlot.slotNumber, state, ClickType.PICKUP);
                                touchUpX = mouseX - i;
                                touchUpY = mouseY - j;
                                returningStackDestSlot = clickedSlot;
                                returningStack = draggedStack;
                                returningStackTime = Minecraft.getSystemTime();
                            }
                        }
                        else if (!draggedStack.isEmpty())
                        {
                            touchUpX = mouseX - i;
                            touchUpY = mouseY - j;
                            returningStackDestSlot = clickedSlot;
                            returningStack = draggedStack;
                            returningStackTime = Minecraft.getSystemTime();
                        }

                        draggedStack = ItemStack.EMPTY;
                        clickedSlot = null;
                    }
                }
                else if (dragSplitting && !dragSplittingSlots.isEmpty())
                {
                    handleMouseClick((Slot) null, -999, Container.getQuickcraftMask(0, dragSplittingLimit), ClickType.QUICK_CRAFT);

                    for (Slot slot1 : dragSplittingSlots)
                    {
                        handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, dragSplittingLimit), ClickType.QUICK_CRAFT);
                    }

                    handleMouseClick((Slot) null, -999, Container.getQuickcraftMask(2, dragSplittingLimit), ClickType.QUICK_CRAFT);
                }
                else if (!mc.player.inventory.getItemStack().isEmpty())
                {
                    if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(state - 100))
                    {
                        handleMouseClick(slot, k, state, ClickType.CLONE);
                    }
                    else
                    {
                        boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                        if (flag1)
                        {
                            shiftClickedStack = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                        }

                        handleMouseClick(slot, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                    }
                }
            }

            if (mc.player.inventory.getItemStack().isEmpty())
            {
                lastClickTime = 0L;
            }

            dragSplitting = false;
        }

        statsScrollGrabbed = false;
        modelGrabbed = false;
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null && hoveredSlot.getHasStack() && (!(hoveredSlot instanceof BetterSlot) || ((BetterSlot) hoveredSlot).enabled))
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
        if (hoveredSlot == null || (hoveredSlot instanceof BetterSlot && !((BetterSlot) hoveredSlot).enabled)) return;

        //TODO Rarity color highlighting
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    protected void drawSlot(Slot slot)
    {
        if (slot instanceof BetterSlot && !((BetterSlot) slot).enabled) return;


        int x = slot.xPos;
        int y = slot.yPos;
        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == clickedSlot && !draggedStack.isEmpty() && !isRightMouseClick;
        ItemStack itemstack1 = mc.player.inventory.getItemStack();
        String s = null;

        if (slot == clickedSlot && !draggedStack.isEmpty() && isRightMouseClick && !itemstack.isEmpty())
        {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() >> 1);
        }
        else if (dragSplitting && dragSplittingSlots.contains(slot) && !itemstack1.isEmpty())
        {
            if (dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slot, itemstack1, true) && inventorySlots.canDragIntoSlot(slot))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slot.getItemStackLimit(itemstack));

                if (itemstack.getCount() > k)
                {
                    s = TextFormatting.YELLOW.toString() + k;
                    itemstack.setCount(k);
                }
            }
            else
            {
                dragSplittingSlots.remove(slot);
                updateDragSplitting();
            }
        }

        zLevel = 100;
        itemRender.zLevel = 100;

        if (itemstack.isEmpty() && slot.isEnabled())
        {
            GlStateManager.disableLighting();

            if (slot instanceof BetterSlot && ((BetterSlot) slot).u >= 0 && ((BetterSlot) slot).v >= 0)
            {
                BetterSlot betterSlot = (BetterSlot) slot;
                int u = betterSlot.u, v = betterSlot.v;

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
            itemRender.renderItemAndEffectIntoGUI(mc.player, itemstack, x, y);
            itemRender.renderItemOverlayIntoGUI(fontRenderer, itemstack, x, y, s);
        }

        itemRender.zLevel = 0;
        zLevel = 0;
    }

    protected void updateDragSplitting()
    {
        ItemStack itemstack = mc.player.inventory.getItemStack();

        if (!itemstack.isEmpty() && dragSplitting)
        {
            if (dragSplittingLimit == 2)
            {
                dragSplittingRemnant = itemstack.getMaxStackSize();
            }
            else
            {
                dragSplittingRemnant = itemstack.getCount();

                for (Slot slot : dragSplittingSlots)
                {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemstack2 = slot.getStack();
                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack1, i);
                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));

                    if (itemstack1.getCount() > j)
                    {
                        itemstack1.setCount(j);
                    }

                    dragSplittingRemnant -= itemstack1.getCount() - i;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (keyCode == 1 || Keys.TIAMAT_INVENTORY_KEY.isActiveAndMatches(keyCode))
        {
            if (tab == 0) mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.openContainer.windowId));
            mc.player.closeScreenAndDropStack();
        }

        checkHotbarKeys(keyCode);

        if (hoveredSlot != null && hoveredSlot.getHasStack())
        {
            if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode))
            {
                handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, 0, ClickType.CLONE);
            }
            else if (mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode))
            {
                handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
            }
        }
    }

    @Override
    protected boolean checkHotbarKeys(int keyCode)
    {
        if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null)
        {
            for (int i = 0; i < 9; ++i)
            {
                if (mc.gameSettings.keyBindsHotbar[i].isActiveAndMatches(keyCode))
                {
                    handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, i, ClickType.SWAP);
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
        return isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }

    protected void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0, 0, 32);
        zLevel = 200;
        itemRender.zLevel = 200;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRenderer;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack.isEmpty() ? 0 : 8), altText);
        zLevel = 0;
        itemRender.zLevel = 0;
    }

    protected Slot getSlotAtPosition(int x, int y)
    {
        if (inventorySlots == null) return null;

        for (int i = 0; i < inventorySlots.inventorySlots.size(); ++i)
        {
            Slot slot = inventorySlots.inventorySlots.get(i);

            if (isMouseOverSlot(slot, x, y) && slot.isEnabled())
            {
                return slot;
            }
        }

        return null;
    }


    @javax.annotation.Nullable
    public Slot getSlotUnderMouse()
    {
        return hoveredSlot;
    }

    public int getGuiLeft()
    {
        return guiLeft;
    }

    public int getGuiTop()
    {
        return guiTop;
    }

    public int getXSize()
    {
        return xSize;
    }

    public int getYSize()
    {
        return ySize;
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
