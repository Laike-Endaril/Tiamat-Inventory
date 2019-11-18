package com.fantasticsource.tiamatrpgmain.gui;

import com.fantasticsource.tiamatrpgmain.config.server.items.TexturedSlot;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientKeyPress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

import static com.fantasticsource.tiamatrpgmain.Keys.TIAMAT_INVENTORY_KEY;
import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;
import static org.lwjgl.opengl.GL11.GL_QUADS;

@SideOnly(Side.CLIENT)
public class TiamatInventoryGUI extends GuiContainer
{
    private static int tab = 0;
    private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "gui/inventory.png");
    private static final int TEXTURE_W = 512, TEXTURE_H = 512;
    private static final double U_PIXEL = 1d / TEXTURE_W, V_PIXEL = 1d / TEXTURE_H;
    private static boolean reopen = false;

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
                ySize = 232;
                uOffset = 0;
                vOffset = 0;
                break;

            case 1:
                xSize = 250;
                ySize = 232;
                uOffset = 0;
                vOffset = 256;
                break;

            case 2:
                xSize = 250;
                ySize = 232;
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
        button = new GuiButtonImage(3, guiLeft + 230, guiTop + 100, 19, 31, TEXTURE_W - 19, TEXTURE_H - 31, 0, TEXTURE);
        buttonList.add(button);

        //Container
        inventorySlots = new TiamatInventoryContainer(Minecraft.getMinecraft().player);
    }

    protected void actionPerformed(GuiButton button)
    {
        buttonClicked = true;

        if (button.id <= 2) setTab(button.id);
        else if (button.id == 3)
        {
            reopen = true;
            PacketHandler.networkWrapper.sendToServer(new MessageClientKeyPress(MessageClientKeyPress.Button.OPEN_WARDROBE));
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

    @Override
    public void drawSlot(Slot slot)
    {
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
            itemstack.setCount(itemstack.getCount() / 2);
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

        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;

        if (itemstack.isEmpty() && slot.isEnabled())
        {
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
                    GlStateManager.disableLighting();
                    mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
                    drawTexturedModalRect(x, y, textureatlassprite, 16, 16);
                    GlStateManager.enableLighting();
                    flag1 = true;
                }
            }
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

        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
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
}
