package com.fantasticsource.tiamatrpg.inventory;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatrpg.api.ITiamatPlayerInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class TiamatPlayerInventory implements ITiamatPlayerInventory
{
    public static LinkedHashMap<UUID, TiamatPlayerInventory> tiamatServerInventories = new LinkedHashMap<>();
    public static File playerDataFolder;

    public final NonNullList<ItemStack> activeMainhand = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> activeOffhand = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> inactiveMainhand = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> inactiveOffhand = NonNullList.withSize(1, ItemStack.EMPTY);

    public final NonNullList<ItemStack> armor = NonNullList.withSize(2, ItemStack.EMPTY);

    public final NonNullList<ItemStack> quickSlots = NonNullList.withSize(1, ItemStack.EMPTY);

    public final NonNullList<ItemStack> pet = NonNullList.withSize(1, ItemStack.EMPTY);

    public final NonNullList<ItemStack> deck = NonNullList.withSize(1, ItemStack.EMPTY);

    public final NonNullList<ItemStack> classes = NonNullList.withSize(2, ItemStack.EMPTY);
    public final NonNullList<ItemStack> offensiveSkills = NonNullList.withSize(2, ItemStack.EMPTY);
    public final NonNullList<ItemStack> utilitySkills = NonNullList.withSize(2, ItemStack.EMPTY);
    public final NonNullList<ItemStack> ultimateSkill = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> passiveSkills = NonNullList.withSize(2, ItemStack.EMPTY);

    public final NonNullList<ItemStack> gatheringProfessions = NonNullList.withSize(2, ItemStack.EMPTY);
    public final NonNullList<ItemStack> craftingProfessions = NonNullList.withSize(2, ItemStack.EMPTY);
    public final NonNullList<ItemStack> craftingRecipes = NonNullList.withSize(15, ItemStack.EMPTY);


    private final List<NonNullList<ItemStack>> allInventories;
    public int currentItem;
    public EntityPlayer player;
    private ItemStack itemStack;
    private int timesChanged;
    private boolean unsheathed = false;

    public TiamatPlayerInventory(EntityPlayer playerIn)
    {
        allInventories = Arrays.asList(activeMainhand, activeOffhand, inactiveMainhand, inactiveOffhand,
                armor,
                quickSlots,
                pet,
                deck,
                classes, offensiveSkills, utilitySkills, ultimateSkill, passiveSkills,
                gatheringProfessions, craftingProfessions, craftingRecipes);


        itemStack = ItemStack.EMPTY;
        player = playerIn;
    }

    public static void init(FMLServerStartingEvent event)
    {
        playerDataFolder = new File(MCTools.getPlayerDataDir(event.getServer()));
    }

    public static void load(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        TiamatPlayerInventory inventory = new TiamatPlayerInventory(player);
        tiamatServerInventories.put(player.getUniqueID(), inventory);

        inventory.load();
    }

    public static void saveUnloadAll(FMLServerStoppedEvent event)
    {
        for (TiamatPlayerInventory inventory : tiamatServerInventories.values())
        {
            inventory.save();
        }
        tiamatServerInventories.clear();
        playerDataFolder = null;
    }

    public static void saveUnload(PlayerEvent.PlayerLoggedOutEvent event)
    {
        EntityPlayer player = event.player;
        TiamatPlayerInventory inventory = tiamatServerInventories.remove(player.getUniqueID());
        if (inventory == null) return;

        inventory.save();
    }

    private boolean canMergeStacks(ItemStack stack1, ItemStack stack2)
    {
        return !stack1.isEmpty() && stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < getInventoryStackLimit();
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
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
        for (ItemStack is : armor) // FORGE: Tick armor on animation ticks
        {
            if (!is.isEmpty())
            {
                is.getItem().onArmorTick(player.world, player, is);
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

    public NBTTagList writeToNBT(NBTTagList nbtTagListIn)
    {
        for (int i = 0; i < getSizeInventory(); i++)
        {
            nbtTagListIn.appendTag(getStackInSlot(i).serializeNBT());
        }

        return nbtTagListIn;
    }

    public void readFromNBT(NBTTagList nbtTagListIn)
    {
        clear();

        for (int i = 0; i < getSizeInventory(); i++)
        {
            NBTTagCompound compound = nbtTagListIn.getCompoundTagAt(i);
            if (compound.hasNoTags()) return;

            setInventorySlotContents(i, new ItemStack(compound));
        }
    }

    public int getSizeInventory()
    {
        int i = 0;
        for (List list : allInventories) i += list.size();
        return i;
    }

    public boolean isEmpty()
    {
        for (List<ItemStack> list : allInventories)
        {
            for (ItemStack stack : list)
            {
                if (!stack.isEmpty())
                {
                    return false;
                }
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

    public void damageArmor(float damage)
    {
        damage = damage / 4.0F;

        if (damage < 1.0F)
        {
            damage = 1.0F;
        }

        for (ItemStack itemstack : armor)
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

    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStackIn)
    {
        itemStack = itemStackIn;
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

    public boolean hasItemStack(ItemStack itemStack)
    {
        for (List<ItemStack> list : allInventories)
        {
            for (ItemStack stack : list)
            {
                if (!stack.isEmpty() && stack.isItemEqual(itemStack)) return true;
            }
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

    public void copyInventory(TiamatPlayerInventory tiamatPlayerInventory)
    {
        for (int i = 0; i < getSizeInventory(); ++i)
        {
            setInventorySlotContents(i, tiamatPlayerInventory.getStackInSlot(i));
        }

        currentItem = tiamatPlayerInventory.currentItem;
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
            for (int i = 0; i < list.size(); i++)
            {
                list.set(i, ItemStack.EMPTY);
            }
        }
    }

    private void load()
    {
        NBTTagCompound compound = null;
        try
        {
            File file = new File(playerDataFolder.getAbsolutePath() + File.separator + MODID + File.separator + player.getPersistentID() + File.separator + "inventory.dat");

            if (file.exists() && file.isFile())
            {
                compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
            }
        }
        catch (Exception var4)
        {
            System.err.println("Failed to load player data for" + player.getName());
        }

        if (compound != null)
        {
            readFromNBT((NBTTagList) compound.getTag("inventory"));
        }
    }

    private void save()
    {
        try
        {
            NBTTagList list = new NBTTagList();
            writeToNBT(list);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("inventory", list);

            String path = playerDataFolder.getAbsolutePath();
            File file = new File(path);
            if (!file.exists()) file.mkdir();

            path += File.separator + MODID;
            file = new File(path);
            if (!file.exists()) file.mkdir();

            path += File.separator + player.getPersistentID();
            file = new File(path);
            if (!file.exists()) file.mkdir();

            File file1 = new File(path + File.separator + "inventory.dat.tmp");
            if (!file1.exists()) file1.createNewFile();
            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(file1));

            File file2 = new File(path + File.separator + "inventory.dat");
            if (file2.exists()) file2.delete();
            file1.renameTo(file2);
        }
        catch (Exception e)
        {
            System.err.println("Failed to save player data for " + player.getName());
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack getActiveMainhand()
    {
        return activeMainhand.get(0);
    }

    @Override
    public ItemStack getActiveOffhand()
    {
        return activeOffhand.get(0);
    }

    @Override
    public ItemStack getInactiveMainhand()
    {
        return inactiveMainhand.get(0);
    }

    @Override
    public ItemStack getInactiveOffhand()
    {
        return inactiveOffhand.get(0);
    }

    @Override
    public ArrayList<ItemStack> getTiamatArmor()
    {
        return new ArrayList<>(armor);
    }

    @Override
    public ArrayList<ItemStack> getQuickSlots()
    {
        return new ArrayList<>(quickSlots);
    }

    @Override
    public ItemStack getPet()
    {
        return pet.get(0);
    }

    @Override
    public ItemStack getDeck()
    {
        return deck.get(0);
    }

    @Override
    public ArrayList<ItemStack> getPlayerClasses()
    {
        return new ArrayList<>(classes);
    }

    @Override
    public ArrayList<ItemStack> getOffensiveSkills()
    {
        return new ArrayList<>(offensiveSkills);
    }

    @Override
    public ArrayList<ItemStack> getUtilitySkills()
    {
        return new ArrayList<>(utilitySkills);
    }

    @Override
    public ItemStack getUltimateSkill()
    {
        return ultimateSkill.get(0);
    }

    @Override
    public ArrayList<ItemStack> getPassiveSkills()
    {
        return new ArrayList<>(passiveSkills);
    }

    @Override
    public ArrayList<ItemStack> getGatheringProfessions()
    {
        return new ArrayList<>(gatheringProfessions);
    }

    @Override
    public ArrayList<ItemStack> getCraftingProfessions()
    {
        return new ArrayList<>(craftingProfessions);
    }

    @Override
    public ArrayList<ItemStack> getCraftingRecipes()
    {
        return new ArrayList<>(craftingRecipes);
    }

    @Override
    public ArrayList<ItemStack> getAllItems()
    {
        ArrayList<ItemStack> result = new ArrayList<>();
        for (NonNullList<ItemStack> inventory : allInventories) result.addAll(inventory);
        return result;
    }

    @Override
    public ArrayList<ItemStack> getAllEquippedItems()
    {
        ArrayList<ItemStack> result = new ArrayList<>();
        for (NonNullList<ItemStack> inventory : allInventories)
        {
            if (inventory == inactiveMainhand || inventory == inactiveOffhand) continue;
            if (!unsheathed)
            {
                if (inventory == activeMainhand || inventory == activeOffhand) continue;
            }

            result.addAll(inventory);
        }
        return result;
    }

    @Override
    public boolean unsheathed()
    {
        return unsheathed;
    }

    public void setUnsheathed(boolean unsheathed)
    {
        this.unsheathed = unsheathed;
    }
}