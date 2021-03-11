package com.fantasticsource.tiamatinventory.inventory;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.inventory.slot.BetterSlot;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import com.fantasticsource.tiamatinventory.TiamatInventory;
import com.fantasticsource.tools.Tools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;

public class TiamatInventoryContainer extends Container
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "gui/inventory.png");
    public static final int TEXTURE_W = 1024, TEXTURE_H = 1024;

    public static final int WEAPON_SLOT_STACK_LIMIT = 64;
    protected final EntityPlayer player;

    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public InventoryCraftResult craftResult = new InventoryCraftResult();

    public TiamatInventoryContainer(EntityPlayer player)
    {
        this.player = player;
        InventoryPlayer playerInventory = player.inventory;
        TiamatPlayerInventory tiamatPlayerInventory;
        if (player.world.isRemote)
        {
            tiamatPlayerInventory = TiamatPlayerInventory.tiamatClientInventory;
            if (tiamatPlayerInventory == null)
            {
                tiamatPlayerInventory = new TiamatPlayerInventory(player);
                TiamatPlayerInventory.tiamatClientInventory = tiamatPlayerInventory;
            }
        }
        else
        {
            tiamatPlayerInventory = TiamatPlayerInventory.tiamatServerInventories.computeIfAbsent(player.getPersistentID(), o -> new TiamatPlayerInventory(player));
        }

        //Weaponset 1
        //Index 0 - 1
        //Internal index 0 and 1 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 0, 61, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0, false, WEAPON_SLOT_STACK_LIMIT, stack -> getSlot(1).getStack().isEmpty() || (!Slottings.isTwoHanded(stack) && !Slottings.isTwoHanded(getSlot(1).getStack()))));
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 1, 43, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 624, 0, false, WEAPON_SLOT_STACK_LIMIT, stack -> getSlot(0).getStack().isEmpty() || (!Slottings.isTwoHanded(stack) && !Slottings.isTwoHanded(getSlot(0).getStack()))));

        //Weaponset 2
        //Index 2 - 3
        //Internal index 2 and 3 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 2, 97, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0, false, WEAPON_SLOT_STACK_LIMIT, stack -> getSlot(3).getStack().isEmpty() || (!Slottings.isTwoHanded(stack) && !Slottings.isTwoHanded(getSlot(3).getStack()))));
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 3, 79, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 624, 0, false, WEAPON_SLOT_STACK_LIMIT, stack -> getSlot(2).getStack().isEmpty() || (!Slottings.isTwoHanded(stack) && !Slottings.isTwoHanded(getSlot(2).getStack()))));

        //"Cargo" inventory
        //Index 4 - 30
        //Internal index 9 - 35 (vanilla)
        for (int yy = 0; yy < 3; ++yy)
        {
            for (int xx = 0; xx < 9; ++xx)
            {
                addSlotToContainer(new BetterSlot(playerInventory, xx + (yy + 1) * 9, 133 + xx * 18, 60 + yy * 18, TEXTURE, TEXTURE_W, TEXTURE_H, -16, -16));
            }
        }

        //Armor slots
        //Index 31 - 36
        //Internal indices...
        //...39 (vanilla head)
        //...4 (tiamat shoulders)
        //...5 (tiamat cape)
        //...38 (vanilla chest)
        //...37 (vanilla legs)
        //...36 (vanilla feet)
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.HEAD, 39, 25, 6, 512, 0);
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 4, 25, 24, TEXTURE, TEXTURE_W, TEXTURE_H, 528, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.itemIsValidForSlot(stack, "Tiamat Shoulders")));
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 5, 25, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 544, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.itemIsValidForSlot(stack, "Tiamat Cape")));
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.CHEST, 38, 25, 60, 560, 0);
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.LEGS, 37, 25, 78, 576, 0);
        addVanillaEquipmentSlot(playerInventory, EntityEquipmentSlot.FEET, 36, 25, 96, 592, 0);

        //Quick slots
        //Index 37 - 39
        //Internal index 6 - 8 (tiamat)
        for (int xx = 0; xx < 3; xx++)
        {
            addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 6 + xx, 133 + xx * 18, 24, TEXTURE, TEXTURE_W, TEXTURE_H, 784, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.itemIsValidForSlot(stack, "Tiamat Quick Item")));
        }

        //Backpack slot
        //Index 40
        //Internal index 9 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 9, 133, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 768, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.itemIsValidForSlot(stack, "Tiamat Backpack")));

        //Pet slot
        //Index 41
        //Internal index 10 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 10, 151, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 640, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.itemIsValidForSlot(stack, "Tiamat Pet")));

        //Deck slot
        //Index 42
        //Internal index 11 (tiamat)
        addSlotToContainer(new FilteredSlot(tiamatPlayerInventory, 11, 169, 42, TEXTURE, TEXTURE_W, TEXTURE_H, 752, 0, true, 1, stack -> stack.hasTagCompound() && Slottings.itemIsValidForSlot(stack, "Tiamat Deck")));

        //Hotbar slots (for creative usage or if hotbar is enabled)
        //Index 43 - 51
        //Internal index 0 - 8 (vanilla)
        for (int xx = 0; xx < 9; ++xx)
        {
            addSlotToContainer(new BetterSlot(playerInventory, xx, 133 + xx * 18, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 608, 0));
        }

        //Vanilla offhand slot
        //Index 52
        //Internal index 40 (vanilla)
        addSlotToContainer(new BetterSlot(playerInventory, 40, 115, 114, TEXTURE, TEXTURE_W, TEXTURE_H, 624, 0));

        //Crafting result slot
        //Index 53
        //Internal index 0 (crafting result)
        addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 223, 42));

        //Crafting matrix slots
        //Index 54 - 62
        //Internal index 0-8 (crafting matrix)
        for (int i = 0; i < craftMatrix.getWidth(); ++i)
        {
            for (int j = 0; j < craftMatrix.getHeight(); ++j)
            {
                addSlotToContainer(new Slot(craftMatrix, j + i * craftMatrix.getWidth(), 241 + j * 18, 6 + i * 18));
            }
        }
    }


    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        InventoryPlayer inventoryplayer = player.inventory;
        ItemStack stack = inventoryplayer.getItemStack();
        if (!stack.isEmpty())
        {
            if (player instanceof EntityPlayerMP) MCTools.give((EntityPlayerMP) player, stack);
            inventoryplayer.setItemStack(ItemStack.EMPTY);
        }

        craftResult.clear();
        if (!player.world.isRemote) clearContainer(player, player.world, craftMatrix);
    }

    @Override
    protected void clearContainer(EntityPlayer player, World worldIn, IInventory inventoryIn)
    {
        if (!player.isEntityAlive() || player instanceof EntityPlayerMP && ((EntityPlayerMP) player).hasDisconnected())
        {
            for (int j = 0; j < inventoryIn.getSizeInventory(); ++j)
            {
                player.dropItem(inventoryIn.removeStackFromSlot(j), false);
            }
        }
        else
        {
            for (int i = 0; i < inventoryIn.getSizeInventory(); ++i)
            {
                ItemStack stack = inventoryIn.removeStackFromSlot(i);
                if (player instanceof EntityPlayerMP) MCTools.give((EntityPlayerMP) player, stack);
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        slotChangedCraftingGrid(player.world, player, craftMatrix, craftResult);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != craftResult && super.canMergeSlot(stack, slotIn);
    }

    @Override
    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftMatrix, InventoryCraftResult craftResult)
    {
        if (!world.isRemote)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) player;
            ItemStack itemstack = ItemStack.EMPTY;
            IRecipe irecipe = CraftingManager.findMatchingRecipe(craftMatrix, world);

            if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe)))
            {
                craftResult.setRecipeUsed(irecipe);
                itemstack = irecipe.getCraftingResult(craftMatrix);
            }

            craftResult.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(windowId, 53, itemstack));
        }
    }


    public static boolean canCombine(ItemStack from, ItemStack to)
    {
        if (from.isEmpty()) return false;
        if (to.isEmpty()) return true;
        return from.getItem() == to.getItem() && (!to.getHasSubtypes() || to.getMetadata() == from.getMetadata()) && ItemStack.areItemStackTagsEqual(to, from);
    }

    private void addVanillaEquipmentSlot(IInventory inventory, EntityEquipmentSlot slotEnum, int index, int x, int y, int u, int v)
    {
        addSlotToContainer(new BetterSlot(inventory, index, x, y, TEXTURE, TEXTURE_W, TEXTURE_H, u, v)
        {
            public int getSlotStackLimit()
            {
                return 1;
            }

            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem().isValidArmor(stack, slotEnum, player);
            }

            public boolean canTakeStack(EntityPlayer playerIn)
            {
                ItemStack itemstack = getStack();
                return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack));
            }

            @Nullable
            @SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return ItemArmor.EMPTY_SLOT_NAMES[slotEnum.getIndex()];
            }
        });
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    //Returning ItemStack.EMPTY from this method indicates that we are done with the transfer.  Mine always finishes in one go, so it always returns ItemStack.EMPTY
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;


        ItemStack stack = slot.getStack();

        int cargoSize = TiamatInventory.inventorySize(player);
        int maxCargoIndex = 3 + cargoSize;
        if (index <= 3)
        {
            //From any weaponset slot
            tryMergeItemStackRanges(stack, 31, 42); //To restricted slots
            if (cargoSize > 0) tryMergeItemStackRanges(stack, 4, maxCargoIndex); //To main inventory (cargo)
            if (TiamatInventory.playerHasHotbar(player)) tryMergeItemStackRanges(stack, 43, 52); //To hotbar and offhand (in that order)
        }
        else if (index <= 30)
        {
            //From main inventory (cargo)
            tryMergeItemStackRanges(stack, 31, 42); //To restricted slots
            if (TiamatInventory.playerHasHotbar(player)) tryMergeItemStackRanges(stack, 43, 52); //To hotbar and offhand (in that order)
            tryMergeItemStackRanges(stack, 0, 3); //To weaponsets
        }
        else if (index <= 42)
        {
            //From restricted slots
            if (cargoSize > 0) tryMergeItemStackRanges(stack, 4, maxCargoIndex); //To main inventory (cargo)
            if (TiamatInventory.playerHasHotbar(player)) tryMergeItemStackRanges(stack, 43, 52); //To hotbar and offhand (in that order)
            tryMergeItemStackRanges(stack, 0, 3); //To weaponsets
        }
        else if (index <= 51)
        {
            //From hotbar (not offhand)
            tryMergeItemStackRanges(stack, 31, 42); //To restricted slots
            if (TiamatInventory.playerHasHotbar(player)) tryMergeItemStackRanges(stack, 52, 52); //To offhand
            if (cargoSize > 0) tryMergeItemStackRanges(stack, 4, maxCargoIndex); //To main inventory (cargo)
            tryMergeItemStackRanges(stack, 0, 3); //To weaponsets
        }
        else if (index == 52)
        {
            //From vanilla offhand
            tryMergeItemStackRanges(stack, 31, 42); //To restricted slots
            if (cargoSize > 0) tryMergeItemStackRanges(stack, 4, maxCargoIndex); //To main inventory (cargo)
            if (TiamatInventory.playerHasHotbar(player)) tryMergeItemStackRanges(stack, 43, 51); //To hotbar
            tryMergeItemStackRanges(stack, 0, 3); //To weaponsets
        }


        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.putStack(stack);

        return ItemStack.EMPTY;
    }

    public void tryMergeItemStackRanges(ItemStack stackFrom, int... ranges)
    {
        if (stackFrom.isEmpty()) return;


        for (int ii = 0; ii < ranges.length; ii += 2)
        {
            int startIndex = ranges[ii];
            int endIndex = ranges[ii + 1];

            if (startIndex <= endIndex)
            {
                for (int i = startIndex; i <= endIndex; i++)
                {
                    tryMergeItemStack(stackFrom, i);
                }
            }
            else
            {
                for (int i = startIndex; i >= endIndex; i--)
                {
                    tryMergeItemStack(stackFrom, i);
                }
            }
        }
    }

    protected void tryMergeItemStack(ItemStack stackFrom, int slotIDTo)
    {
        Slot slotTo = this.inventorySlots.get(slotIDTo);
        if (!slotTo.isItemValid(stackFrom)) return;

        ItemStack stackTo = slotTo.getStack();
        if (!canCombine(stackFrom, stackTo)) return;

        int limit = Tools.min(slotTo.getSlotStackLimit(), !stackTo.isEmpty() ? stackTo.getMaxStackSize() : stackFrom.getMaxStackSize());

        int moveAmount = Tools.min(stackFrom.getCount(), limit - stackTo.getCount());

        if (stackTo.isEmpty())
        {
            stackTo = stackFrom.copy();
            stackTo.setCount(moveAmount);
            slotTo.putStack(stackTo);
        }
        else
        {
            stackTo.grow(moveAmount);
        }

        stackFrom.shrink(moveAmount);

        slotTo.onSlotChanged();
    }
}
