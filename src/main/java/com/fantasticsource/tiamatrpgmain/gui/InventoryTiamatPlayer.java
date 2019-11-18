package com.fantasticsource.tiamatrpgmain.gui;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class InventoryTiamatPlayer implements IInventory
{
    public static LinkedHashMap<UUID, InventoryTiamatPlayer> tiamatInventories = new LinkedHashMap<>();
    public static File playerDataFolder;

    public final NonNullList<ItemStack> mainInventory = NonNullList.withSize(36, ItemStack.EMPTY);
    public final NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
    public final NonNullList<ItemStack> offHandInventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories;
    public int currentItem;
    public EntityPlayer player;
    private ItemStack itemStack;
    private int timesChanged;

    public InventoryTiamatPlayer(EntityPlayer playerIn)
    {
        allInventories = Arrays.asList(mainInventory, armorInventory, offHandInventory);
        itemStack = ItemStack.EMPTY;
        player = playerIn;
    }

    private boolean canMergeStacks(ItemStack stack1, ItemStack stack2)
    {
        return !stack1.isEmpty() && stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < getInventoryStackLimit();
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public int getFirstEmptyStack()
    {
        for (int i = 0; i < mainInventory.size(); ++i)
        {
            if (mainInventory.get(i).isEmpty())
            {
                return i;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    public void setPickedItemStack(ItemStack stack)
    {
        int i = getSlotFor(stack);

        if (i == -1)
        {
            currentItem = getBestHotbarSlot();

            if (!mainInventory.get(currentItem).isEmpty())
            {
                int j = getFirstEmptyStack();

                if (j != -1)
                {
                    mainInventory.set(j, mainInventory.get(currentItem));
                }
            }

            mainInventory.set(currentItem, stack);
        }
        else
        {
            pickItem(i);
        }
    }

    public void pickItem(int index)
    {
        currentItem = getBestHotbarSlot();
        ItemStack itemstack = mainInventory.get(currentItem);
        mainInventory.set(currentItem, mainInventory.get(index));
        mainInventory.set(index, itemstack);
    }

    @SideOnly(Side.CLIENT)
    public int getSlotFor(ItemStack stack)
    {
        for (int i = 0; i < mainInventory.size(); ++i)
        {
            if (!mainInventory.get(i).isEmpty() && stackEqualExact(stack, mainInventory.get(i)))
            {
                return i;
            }
        }

        return -1;
    }

    public int findSlotMatchingUnusedItem(ItemStack p_194014_1_)
    {
        for (int i = 0; i < mainInventory.size(); ++i)
        {
            ItemStack itemstack = mainInventory.get(i);

            if (!mainInventory.get(i).isEmpty() && stackEqualExact(p_194014_1_, mainInventory.get(i)) && !mainInventory.get(i).isItemDamaged() && !itemstack.isItemEnchanted() && !itemstack.hasDisplayName())
            {
                return i;
            }
        }

        return -1;
    }

    public int getBestHotbarSlot()
    {
        for (int i = 0; i < 9; ++i)
        {
            int j = (currentItem + i) % 9;

            if (mainInventory.get(j).isEmpty())
            {
                return j;
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            int l = (currentItem + k) % 9;

            if (!mainInventory.get(l).isItemEnchanted())
            {
                return l;
            }
        }

        return currentItem;
    }

    @SideOnly(Side.CLIENT)
    public void changeCurrentItem(int direction)
    {
        if (direction > 0)
        {
            direction = 1;
        }

        if (direction < 0)
        {
            direction = -1;
        }

        for (currentItem -= direction; currentItem < 0; currentItem += 9)
        {
            ;
        }

        while (currentItem >= 9)
        {
            currentItem -= 9;
        }
    }

    public int clearMatchingItems(@Nullable Item itemIn, int metadataIn, int removeCount, @Nullable NBTTagCompound itemNBT)
    {
        int i = 0;

        for (int j = 0; j < getSizeInventory(); ++j)
        {
            ItemStack itemstack = getStackInSlot(j);

            if (!itemstack.isEmpty() && (itemIn == null || itemstack.getItem() == itemIn) && (metadataIn <= -1 || itemstack.getMetadata() == metadataIn) && (itemNBT == null || NBTUtil.areNBTEquals(itemNBT, itemstack.getTagCompound(), true)))
            {
                int k = removeCount <= 0 ? itemstack.getCount() : Math.min(removeCount - i, itemstack.getCount());
                i += k;

                if (removeCount != 0)
                {
                    itemstack.shrink(k);

                    if (itemstack.isEmpty())
                    {
                        setInventorySlotContents(j, ItemStack.EMPTY);
                    }

                    if (removeCount > 0 && i >= removeCount)
                    {
                        return i;
                    }
                }
            }
        }

        if (!itemStack.isEmpty())
        {
            if (itemIn != null && itemStack.getItem() != itemIn)
            {
                return i;
            }

            if (metadataIn > -1 && itemStack.getMetadata() != metadataIn)
            {
                return i;
            }

            if (itemNBT != null && !NBTUtil.areNBTEquals(itemNBT, itemStack.getTagCompound(), true))
            {
                return i;
            }

            int l = removeCount <= 0 ? itemStack.getCount() : Math.min(removeCount - i, itemStack.getCount());
            i += l;

            if (removeCount != 0)
            {
                itemStack.shrink(l);

                if (itemStack.isEmpty())
                {
                    itemStack = ItemStack.EMPTY;
                }

                if (removeCount > 0 && i >= removeCount)
                {
                    return i;
                }
            }
        }

        return i;
    }

    private int storePartialItemStack(ItemStack itemStackIn)
    {
        int i = storeItemStack(itemStackIn);

        if (i == -1)
        {
            i = getFirstEmptyStack();
        }

        return i == -1 ? itemStackIn.getCount() : addResource(i, itemStackIn);
    }

    private int addResource(int p_191973_1_, ItemStack p_191973_2_)
    {
        int i = p_191973_2_.getCount();
        ItemStack itemstack = getStackInSlot(p_191973_1_);

        if (itemstack.isEmpty())
        {
            itemstack = p_191973_2_.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
            itemstack.setCount(0);

            if (p_191973_2_.hasTagCompound())
            {
                itemstack.setTagCompound(p_191973_2_.getTagCompound().copy());
            }

            setInventorySlotContents(p_191973_1_, itemstack);
        }

        int j = i;

        if (i > itemstack.getMaxStackSize() - itemstack.getCount())
        {
            j = itemstack.getMaxStackSize() - itemstack.getCount();
        }

        if (j > getInventoryStackLimit() - itemstack.getCount())
        {
            j = getInventoryStackLimit() - itemstack.getCount();
        }

        if (j == 0)
        {
            return i;
        }
        else
        {
            i = i - j;
            itemstack.grow(j);
            itemstack.setAnimationsToGo(5);
            return i;
        }
    }

    public int storeItemStack(ItemStack itemStackIn)
    {
        if (canMergeStacks(getStackInSlot(currentItem), itemStackIn))
        {
            return currentItem;
        }
        else if (canMergeStacks(getStackInSlot(40), itemStackIn))
        {
            return 40;
        }
        else
        {
            for (int i = 0; i < mainInventory.size(); ++i)
            {
                if (canMergeStacks(mainInventory.get(i), itemStackIn))
                {
                    return i;
                }
            }

            return -1;
        }
    }

    public void decrementAnimations()
    {
        for (NonNullList<ItemStack> nonnulllist : allInventories)
        {
            for (int i = 0; i < nonnulllist.size(); ++i)
            {
                if (!nonnulllist.get(i).isEmpty())
                {
                    nonnulllist.get(i).updateAnimation(player.world, player, i, currentItem == i);
                }
            }
        }
        for (ItemStack is : armorInventory) // FORGE: Tick armor on animation ticks
        {
            if (!is.isEmpty())
            {
                is.getItem().onArmorTick(player.world, player, is);
            }
        }
    }

    public boolean addItemStackToInventory(ItemStack itemStackIn)
    {
        return add(-1, itemStackIn);
    }

    public boolean add(int p_191971_1_, final ItemStack p_191971_2_)
    {
        if (p_191971_2_.isEmpty())
        {
            return false;
        }
        else
        {
            try
            {
                if (p_191971_2_.isItemDamaged())
                {
                    if (p_191971_1_ == -1)
                    {
                        p_191971_1_ = getFirstEmptyStack();
                    }

                    if (p_191971_1_ >= 0)
                    {
                        mainInventory.set(p_191971_1_, p_191971_2_.copy());
                        mainInventory.get(p_191971_1_).setAnimationsToGo(5);
                        p_191971_2_.setCount(0);
                        return true;
                    }
                    else if (player.capabilities.isCreativeMode)
                    {
                        p_191971_2_.setCount(0);
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    int i;

                    while (true)
                    {
                        i = p_191971_2_.getCount();

                        if (p_191971_1_ == -1)
                        {
                            p_191971_2_.setCount(storePartialItemStack(p_191971_2_));
                        }
                        else
                        {
                            p_191971_2_.setCount(addResource(p_191971_1_, p_191971_2_));
                        }

                        if (p_191971_2_.isEmpty() || p_191971_2_.getCount() >= i)
                        {
                            break;
                        }
                    }

                    if (p_191971_2_.getCount() == i && player.capabilities.isCreativeMode)
                    {
                        p_191971_2_.setCount(0);
                        return true;
                    }
                    else
                    {
                        return p_191971_2_.getCount() < i;
                    }
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Item.getIdFromItem(p_191971_2_.getItem()));
                crashreportcategory.addCrashSection("Item data", p_191971_2_.getMetadata());
                crashreportcategory.addDetail("Registry Name", () -> String.valueOf(p_191971_2_.getItem().getRegistryName()));
                crashreportcategory.addDetail("Item Class", () -> p_191971_2_.getItem().getClass().getName());
                crashreportcategory.addDetail("Item name", p_191971_2_::getDisplayName);
                throw new ReportedException(crashreport);
            }
        }
    }

    public void placeItemBackInInventory(World p_191975_1_, ItemStack p_191975_2_)
    {
        if (!p_191975_1_.isRemote)
        {
            while (!p_191975_2_.isEmpty())
            {
                int i = storeItemStack(p_191975_2_);

                if (i == -1)
                {
                    i = getFirstEmptyStack();
                }

                if (i == -1)
                {
                    player.dropItem(p_191975_2_, false);
                    break;
                }

                int j = p_191975_2_.getMaxStackSize() - getStackInSlot(i).getCount();

                if (add(i, p_191975_2_.splitStack(j)))
                {
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, i, getStackInSlot(i)));
                }
            }
        }
    }

    public ItemStack decrStackSize(int index, int count)
    {
        List<ItemStack> list = null;

        for (NonNullList<ItemStack> nonnulllist : allInventories)
        {
            if (index < nonnulllist.size())
            {
                list = nonnulllist;
                break;
            }

            index -= nonnulllist.size();
        }

        return list != null && !list.get(index).isEmpty() ? ItemStackHelper.getAndSplit(list, index, count) : ItemStack.EMPTY;
    }

    public void deleteStack(ItemStack stack)
    {
        for (NonNullList<ItemStack> nonnulllist : allInventories)
        {
            for (int i = 0; i < nonnulllist.size(); ++i)
            {
                if (nonnulllist.get(i) == stack)
                {
                    nonnulllist.set(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    public ItemStack removeStackFromSlot(int index)
    {
        NonNullList<ItemStack> nonnulllist = null;

        for (NonNullList<ItemStack> nonnulllist1 : allInventories)
        {
            if (index < nonnulllist1.size())
            {
                nonnulllist = nonnulllist1;
                break;
            }

            index -= nonnulllist1.size();
        }

        if (nonnulllist != null && !nonnulllist.get(index).isEmpty())
        {
            ItemStack itemstack = nonnulllist.get(index);
            nonnulllist.set(index, ItemStack.EMPTY);
            return itemstack;
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        NonNullList<ItemStack> nonnulllist = null;

        for (NonNullList<ItemStack> nonnulllist1 : allInventories)
        {
            if (index < nonnulllist1.size())
            {
                nonnulllist = nonnulllist1;
                break;
            }

            index -= nonnulllist1.size();
        }

        if (nonnulllist != null)
        {
            nonnulllist.set(index, stack);
        }
    }

    public float getDestroySpeed(IBlockState state)
    {
        float f = 1.0F;

        if (!mainInventory.get(currentItem).isEmpty())
        {
            f *= mainInventory.get(currentItem).getDestroySpeed(state);
        }

        return f;
    }

    public NBTTagList writeToNBT(NBTTagList nbtTagListIn)
    {
        for (int i = 0; i < mainInventory.size(); ++i)
        {
            if (!mainInventory.get(i).isEmpty())
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                mainInventory.get(i).writeToNBT(nbttagcompound);
                nbtTagListIn.appendTag(nbttagcompound);
            }
        }

        for (int j = 0; j < armorInventory.size(); ++j)
        {
            if (!armorInventory.get(j).isEmpty())
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) (j + 100));
                armorInventory.get(j).writeToNBT(nbttagcompound1);
                nbtTagListIn.appendTag(nbttagcompound1);
            }
        }

        for (int k = 0; k < offHandInventory.size(); ++k)
        {
            if (!offHandInventory.get(k).isEmpty())
            {
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                nbttagcompound2.setByte("Slot", (byte) (k + 150));
                offHandInventory.get(k).writeToNBT(nbttagcompound2);
                nbtTagListIn.appendTag(nbttagcompound2);
            }
        }

        return nbtTagListIn;
    }

    public void readFromNBT(NBTTagList nbtTagListIn)
    {
        mainInventory.clear();
        armorInventory.clear();
        offHandInventory.clear();

        for (int i = 0; i < nbtTagListIn.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbtTagListIn.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = new ItemStack(nbttagcompound);

            if (!itemstack.isEmpty())
            {
                if (j < mainInventory.size())
                {
                    mainInventory.set(j, itemstack);
                }
                else if (j >= 100 && j < armorInventory.size() + 100)
                {
                    armorInventory.set(j - 100, itemstack);
                }
                else if (j >= 150 && j < offHandInventory.size() + 150)
                {
                    offHandInventory.set(j - 150, itemstack);
                }
            }
        }
    }

    public int getSizeInventory()
    {
        return mainInventory.size() + armorInventory.size() + offHandInventory.size();
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : mainInventory)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        for (ItemStack itemstack1 : armorInventory)
        {
            if (!itemstack1.isEmpty())
            {
                return false;
            }
        }

        for (ItemStack itemstack2 : offHandInventory)
        {
            if (!itemstack2.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    public ItemStack getStackInSlot(int index)
    {
        List<ItemStack> list = null;

        for (NonNullList<ItemStack> nonnulllist : allInventories)
        {
            if (index < nonnulllist.size())
            {
                list = nonnulllist;
                break;
            }

            index -= nonnulllist.size();
        }

        return list == null ? ItemStack.EMPTY : list.get(index);
    }

    public String getName()
    {
        return "container.inventory";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public ITextComponent getDisplayName()
    {
        return (hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName()));
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean canHarvestBlock(IBlockState state)
    {
        if (state.getMaterial().isToolNotRequired())
        {
            return true;
        }
        else
        {
            ItemStack itemstack = getStackInSlot(currentItem);
            return !itemstack.isEmpty() && itemstack.canHarvestBlock(state);
        }
    }

    @SideOnly(Side.CLIENT)
    public ItemStack armorItemInSlot(int slotIn)
    {
        return armorInventory.get(slotIn);
    }

    public void damageArmor(float damage)
    {
        damage = damage / 4.0F;

        if (damage < 1.0F)
        {
            damage = 1.0F;
        }

        for (ItemStack itemstack : armorInventory)
        {
            if (itemstack.getItem() instanceof ItemArmor)
            {
                itemstack.damageItem((int) damage, player);
            }
        }
    }

    public void dropAllItems()
    {
        for (List<ItemStack> list : allInventories)
        {
            for (int i = 0; i < list.size(); ++i)
            {
                ItemStack itemstack = list.get(i);

                if (!itemstack.isEmpty())
                {
                    player.dropItem(itemstack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    public void markDirty()
    {
        ++timesChanged;
    }

    @SideOnly(Side.CLIENT)
    public int getTimesChanged()
    {
        return timesChanged;
    }

    public void setItemStack(ItemStack itemStackIn)
    {
        itemStack = itemStackIn;
    }

    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if (this.player.isDead)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq(this.player) <= 64.0D;
        }
    }

    public boolean hasItemStack(ItemStack itemStackIn)
    {
        label23:

        for (List<ItemStack> list : allInventories)
        {
            Iterator iterator = list.iterator();

            while (true)
            {
                if (!iterator.hasNext())
                {
                    continue label23;
                }

                ItemStack itemstack = (ItemStack) iterator.next();

                if (!itemstack.isEmpty() && itemstack.isItemEqual(itemStackIn))
                {
                    break;
                }
            }

            return true;
        }

        return false;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    public void copyInventory(net.minecraft.entity.player.InventoryPlayer playerInventory)
    {
        for (int i = 0; i < getSizeInventory(); ++i)
        {
            setInventorySlotContents(i, playerInventory.getStackInSlot(i));
        }

        currentItem = playerInventory.currentItem;
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        for (List<ItemStack> list : allInventories)
        {
            list.clear();
        }
    }

    public void fillStackedContents(RecipeItemHelper helper, boolean p_194016_2_)
    {
        for (ItemStack itemstack : mainInventory)
        {
            helper.accountStack(itemstack);
        }

        if (p_194016_2_)
        {
            helper.accountStack(offHandInventory.get(0));
        }
    }


    public static void serverStart(FMLServerStartingEvent event)
    {
        playerDataFolder = new File(MCTools.getPlayerDataDir(event.getServer()));
        //TODO load
    }

    public static void serverStop(FMLServerStoppedEvent event)
    {
        for (InventoryTiamatPlayer inventory : tiamatInventories.values())
        {
            //TODO save
        }
        tiamatInventories.clear();
        playerDataFolder = null;
    }
}