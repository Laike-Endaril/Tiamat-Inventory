package com.fantasticsource.tiamatinventory.inventory.inventoryhacks;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import com.fantasticsource.tiamatinventory.TiamatInventory;
import com.fantasticsource.tiamatinventory.inventory.ClientInventoryData;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryGUI;
import com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer.*;

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

        mc.renderEngine.bindTexture(TEXTURE);

        TiamatPlayerInventory inventory = TiamatPlayerInventory.tiamatClientInventory;

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
                    if (slotIndex < 9 && !TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player))
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
                if (slot.inventory == inventory)
                {
                    //Textures for tiamat slots that exist in non-tiamat-inventory GUIs
                    if (inventory.getStackInSlot(slotIndex).isEmpty())
                    {
                        if (slotIndex < 4) renderTextureAt(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, TiamatInventoryGUI.U_PIXEL * (slotIndex % 2 == 0 ? 608 : 624), 0, 16);
                        else if (slotIndex < 7) renderTextureAt(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, TiamatInventoryGUI.U_PIXEL * 784, 0, 16);
                    }
                }
                else if (slot.inventory instanceof InventoryPlayer)
                {
                    if (slotIndex < 9 && !TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player))
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
        TiamatPlayerInventory inventory = TiamatPlayerInventory.tiamatClientInventory;
        HashMap<Integer, Integer> tiamatSlotToCurrentSlot = new HashMap<>();
        for (int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null) continue;


            int slotIndex = slot.getSlotIndex();

            if (container instanceof TiamatInventoryContainer)
            {
                if (slot.inventory instanceof InventoryCraftResult)
                {
                    if (ClientInventoryData.craftW == 0 || ClientInventoryData.craftH == 0)
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                }
                else if (slot.inventory instanceof InventoryCrafting)
                {
                    if (!allowedCraftingSlots.contains(slotIndex))
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                }
                else if (slot.inventory instanceof InventoryPlayer)
                {
                    if (slotIndex < 9 && !TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player))
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                    else if (slotIndex >= 9 + invSize && slotIndex < 36)
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                }
            }
            else
            {
                if (slot.inventory instanceof InventoryPlayer)
                {
                    if (slotIndex < 9 && !TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player))
                    {
                        if (inventory != null)
                        {
                            if (slotIndex < 4) tiamatSlotToCurrentSlot.put(slotIndex, i);
                            else if (slotIndex < 7) tiamatSlotToCurrentSlot.put(slotIndex, i + 33);
                        }
                    }
                    else if (slotIndex >= 9 + invSize && slotIndex < 36)
                    {
                        container.inventorySlots.set(i, new FakeSlot(slot.inventory, slotIndex, slot.xPos, slot.yPos));
                    }
                }
            }
        }

        if (inventory != null)
        {
            for (Map.Entry<Integer, Integer> entry : tiamatSlotToCurrentSlot.entrySet())
            {
                int tiamatIndex = entry.getKey(), currentIndex = entry.getValue();
                int pairedIndex = tiamatIndex % 2 == 0 ? tiamatIndex + 1 : tiamatIndex - 1;

                Slot oldSlot = container.inventorySlots.get(currentIndex);
                Slot newSlot = new FilteredSlot(inventory, tiamatIndex, oldSlot.xPos, oldSlot.yPos, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0, false, WEAPON_SLOT_STACK_LIMIT, stack ->
                {
                    ItemStack other = inventory.getStackInSlot(pairedIndex);
                    return other.isEmpty() || (!Slottings.isTwoHanded(stack) && !Slottings.isTwoHanded(other));
                });
                newSlot.slotNumber = oldSlot.slotNumber;
                container.inventorySlots.set(currentIndex, newSlot);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderHotbar(RenderGameOverlayEvent.Pre event)
    {
        if (TiamatInventory.playerHasHotbar(Minecraft.getMinecraft().player)) return;

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) event.setCanceled(true);
    }
}
