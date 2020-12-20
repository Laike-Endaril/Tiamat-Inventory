package com.fantasticsource.tiamatinventory.inventory.inventoryhacks;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import com.fantasticsource.tiamatinventory.TiamatInventory;
import com.fantasticsource.tiamatinventory.inventory.ClientInventoryData;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryGUI;
import com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory;
import com.google.common.collect.Lists;
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
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.List;

import static com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer.*;

public class ClientInventoryHacks extends GuiButton
{
    protected static ClientInventoryHacks currentInstance = null;

    protected GuiContainer gui;
    protected boolean isTiamat;
    protected List<Slot> inactiveInventorySlots = Lists.newArrayList();
    protected Container container = null;

    public ClientInventoryHacks(GuiContainer gui)
    {
        super(Integer.MIN_VALUE + 777, -10000, -10000, 0, 0, "");
        this.gui = gui;
        isTiamat = gui instanceof TiamatInventoryGUI;
    }

    public static void update()
    {
        if (currentInstance != null) currentInstance.updateInternal();
    }

    protected void updateInternal()
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || container == null) return;


        //Activate all, then deactivate any necessary
        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            activateSlot(container, i);
        }


        HashSet<Integer> allowedCraftingSlots = new HashSet<>();
        for (int x = 0; x < ClientInventoryData.craftW; x++)
        {
            for (int y = 0; y < ClientInventoryData.craftH; y++)
            {
                //Expand from bottom-left
                allowedCraftingSlots.add(6 + x - y * 3);
            }
        }


        int invSize = ClientInventoryData.inventorySize;
        TiamatPlayerInventory tiamatInventory = TiamatPlayerInventory.tiamatClientInventory;
        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null) continue;


            int slotIndex = slot.getSlotIndex();

            if (container instanceof TiamatInventoryContainer)
            {
                //For TiamatInventoryContainer
                if (slot.inventory instanceof InventoryCraftResult)
                {
                    if (ClientInventoryData.craftW == 0 || ClientInventoryData.craftH == 0)
                    {
                        deactivateSlot(container, i);
                    }
                }
                else if (slot.inventory instanceof InventoryCrafting)
                {
                    if (!allowedCraftingSlots.contains(slotIndex))
                    {
                        deactivateSlot(container, i);
                    }
                }
                else if (slot.inventory instanceof InventoryPlayer)
                {
                    if (!TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player) && (slotIndex < 9 || slotIndex == 40))
                    {
                        deactivateSlot(container, i);
                    }
                    else if (slotIndex >= 9 + invSize && slotIndex < 36)
                    {
                        deactivateSlot(container, i);
                    }
                }
            }
            else
            {
                //For Non-TiamatInventoryContainer
                if (slot.inventory instanceof InventoryPlayer)
                {
                    if (!TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player) && (slotIndex < 9 || slotIndex == 40))
                    {
                        if (tiamatInventory != null)
                        {
                            if (slotIndex < 4)
                            {
                                //Replace first 4 (unavailable) hotbar slots with weaponsets
                                int pairedIndex = slotIndex % 2 == 0 ? slotIndex + 1 : slotIndex - 1;

                                Slot oldSlot = container.inventorySlots.get(i);
                                //First 4 slot indices just happen to line up here; first four hotbar in vanilla inv -> weaponsets in Tiamat inv
                                Slot newSlot = new FilteredSlot(tiamatInventory, slotIndex, oldSlot.xPos, oldSlot.yPos, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0, false, WEAPON_SLOT_STACK_LIMIT, stack ->
                                {
                                    ItemStack other = tiamatInventory.getStackInSlot(pairedIndex);
                                    return other.isEmpty() || (!Slottings.isTwoHanded(stack) && !Slottings.isTwoHanded(other));
                                });
                                newSlot.slotNumber = oldSlot.slotNumber;
                                container.inventorySlots.set(i, newSlot);
                            }
                            else if (slotIndex < 7)
                            {
                                //Replace 5th - 7th (unavailable) hotbar slots with quickslots
                                Slot oldSlot = container.inventorySlots.get(i);
                                Slot newSlot = new FilteredSlot(tiamatInventory, slotIndex + 2, oldSlot.xPos, oldSlot.yPos, TEXTURE, TEXTURE_W, TEXTURE_H, 784, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Quick Item", player));
                                newSlot.slotNumber = oldSlot.slotNumber;
                                container.inventorySlots.set(i, newSlot);
                            }
                            else if (slotIndex == 7)
                            {
                                //Replace 8th (unavailable) hotbar slot with pet
                                Slot oldSlot = container.inventorySlots.get(i);
                                Slot newSlot = new FilteredSlot(tiamatInventory, 10, oldSlot.xPos, oldSlot.yPos, TEXTURE, TEXTURE_W, TEXTURE_H, 640, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Pet", player));
                                newSlot.slotNumber = oldSlot.slotNumber;
                                container.inventorySlots.set(i, newSlot);
                            }
                            else if (slotIndex == 8)
                            {
                                //Replace 9th (unavailable) hotbar slot with deck
                                Slot oldSlot = container.inventorySlots.get(i);
                                Slot newSlot = new FilteredSlot(tiamatInventory, 11, oldSlot.xPos, oldSlot.yPos, TEXTURE, TEXTURE_W, TEXTURE_H, 752, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.slotTypeValidForItemstack(stack, "Tiamat Deck", player));
                                newSlot.slotNumber = oldSlot.slotNumber;
                                container.inventorySlots.set(i, newSlot);
                            }
                            else deactivateSlot(container, i);
                        }
                    }
                    else if (slotIndex >= 9 + invSize && slotIndex < 36)
                    {
                        deactivateSlot(container, i);
                    }
                }
            }
        }
    }

    protected void activateSlot(Container container, int i)
    {
        if (!(container.inventorySlots.get(i) instanceof FakeSlot)) return;

        Slot swap = container.inventorySlots.get(i);
        container.inventorySlots.set(i, inactiveInventorySlots.get(i));
        inactiveInventorySlots.set(i, swap);
    }

    protected void deactivateSlot(Container container, int i)
    {
        if (container.inventorySlots.get(i) instanceof FakeSlot) return;

        Slot swap = container.inventorySlots.get(i);
        container.inventorySlots.set(i, inactiveInventorySlots.get(i));
        inactiveInventorySlots.set(i, swap);
    }

    @SubscribeEvent
    public static void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        //First-time setup; after this, run update() to update
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        GameType gameType = MCTools.getGameType(player);
        if (gameType == GameType.CREATIVE || gameType == GameType.SPECTATOR) return;

        Gui gui = event.getGui();
        if (!(gui instanceof GuiContainer)) return;

        Container container = ((GuiContainer) gui).inventorySlots;
        if (container == null) return;


        ClientInventoryHacks clientInventoryHacks = null;
        List<GuiButton> buttonList = event.getButtonList();
        for (GuiButton button : buttonList)
        {
            if (button instanceof ClientInventoryHacks)
            {
                clientInventoryHacks = (ClientInventoryHacks) button;
                break;
            }
        }
        if (clientInventoryHacks == null)
        {
            clientInventoryHacks = new ClientInventoryHacks((GuiContainer) gui);
            buttonList.add(clientInventoryHacks);

            for (int i = 0; i < container.inventorySlots.size(); i++)
            {
                Slot slot = container.inventorySlots.get(i);
                clientInventoryHacks.inactiveInventorySlots.add(new FakeSlot(slot.inventory, slot.getSlotIndex(), slot.xPos, slot.yPos));
            }
        }


        currentInstance = clientInventoryHacks;
        clientInventoryHacks.container = container;
        clientInventoryHacks.updateInternal();
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

        mc.renderEngine.bindTexture(TEXTURE);

        TiamatPlayerInventory tiamatInventory = TiamatPlayerInventory.tiamatClientInventory;

        Container container = gui.inventorySlots;
        if (container == null) return;


        HashSet<Integer> allowedCraftingSlots = new HashSet<>();
        for (int x = 0; x < ClientInventoryData.craftW; x++)
        {
            for (int y = 0; y < ClientInventoryData.craftH; y++)
            {
                //Expand from bottom-left
                allowedCraftingSlots.add(6 + x - y * 3);
            }
        }


        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null) continue;

            int slotIndex = slot.getSlotIndex();
            int invSize = ClientInventoryData.inventorySize;
            if (isTiamat)
            {
                //For Tiamat Inventory GUI
                if (slot.inventory instanceof InventoryCraftResult)
                {
                    //Render blank textures over blocked crafting result slot
                    if (ClientInventoryData.craftW == 0 || ClientInventoryData.craftH == 0)
                    {
                        renderTextureAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, TiamatInventoryGUI.U_PIXEL * 576, TiamatInventoryGUI.V_PIXEL * 16, 18);
                    }
                }
                else if (slot.inventory instanceof InventoryCrafting)
                {
                    //Render blank textures over blocked crafting slots
                    if (!allowedCraftingSlots.contains(slotIndex))
                    {
                        renderTextureAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, TiamatInventoryGUI.U_PIXEL * 576, TiamatInventoryGUI.V_PIXEL * 16, 18);
                    }
                }
                else if (slot.inventory instanceof InventoryPlayer)
                {
                    //Render blank textures over blocked hotbar and cargo slots
                    if (!TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player) && (slotIndex < 9 || slotIndex == 40))
                    {
                        renderTextureAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, TiamatInventoryGUI.U_PIXEL * 576, TiamatInventoryGUI.V_PIXEL * 16, 18);
                    }
                    else if (slotIndex >= 9 + invSize && slotIndex < 36)
                    {
                        renderTextureAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, TiamatInventoryGUI.U_PIXEL * 576, TiamatInventoryGUI.V_PIXEL * 16, 18);
                    }
                }
            }
            else
            {
                //For Non-Tiamat Inventory GUI
                if (slot.inventory == tiamatInventory)
                {
                    //Textures for tiamat slots that exist in non-tiamat-inventory GUIs
                    if (slot.getStack().isEmpty())
                    {
                        if (slotIndex < 4) renderTextureAt(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, TiamatInventoryGUI.U_PIXEL * (slotIndex % 2 == 0 ? 608 : 624), 0, 16);
                        else if (slotIndex >= 6 && slotIndex <= 8) renderTextureAt(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, TiamatInventoryGUI.U_PIXEL * 784, 0, 16);
                        else if (slotIndex == 10) renderTextureAt(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, TiamatInventoryGUI.U_PIXEL * 640, 0, 16);
                        else if (slotIndex == 11) renderTextureAt(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, TiamatInventoryGUI.U_PIXEL * 752, 0, 16);
                        else System.out.println(slotIndex + ", " + i + ", " + slot.getSlotIndex() + ", " + slot.slotNumber);
                    }
                }
                else if (slot.inventory instanceof InventoryPlayer)
                {
                    //Render blank textures over blocked hotbar and cargo slots
                    if (!TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player) && (slotIndex < 9 || slotIndex == 40))
                    {
                        renderTextureAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, TiamatInventoryGUI.U_PIXEL * 544, TiamatInventoryGUI.V_PIXEL * 16, 18);
                    }
                    else if (slotIndex >= 9 + invSize && slotIndex < 36)
                    {
                        renderTextureAt(gui.getGuiLeft() + slot.xPos - 1, gui.getGuiTop() + slot.yPos - 1, TiamatInventoryGUI.U_PIXEL * 576, TiamatInventoryGUI.V_PIXEL * 16, 18);
                    }
                }
            }
        }

        GlStateManager.disableBlend();
    }

    protected void renderTextureAt(int x, int y, double u, double v, int size)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        bufferbuilder.pos(x, y + size, zLevel).tex(u, v + TiamatInventoryGUI.V_PIXEL * size).endVertex();
        bufferbuilder.pos(x + size, y + size, zLevel).tex(u + TiamatInventoryGUI.U_PIXEL * size, v + TiamatInventoryGUI.V_PIXEL * size).endVertex();
        bufferbuilder.pos(x + size, y, zLevel).tex(u + TiamatInventoryGUI.U_PIXEL * size, v).endVertex();
        bufferbuilder.pos(x, y, zLevel).tex(u, v).endVertex();

        tessellator.draw();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderHotbar(RenderGameOverlayEvent.Pre event)
    {
        if (TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player)) return;

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) event.setCanceled(true);
    }
}
