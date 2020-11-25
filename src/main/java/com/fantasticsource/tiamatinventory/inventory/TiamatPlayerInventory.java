package com.fantasticsource.tiamatinventory.inventory;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatinventory.TiamatInventory;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class TiamatPlayerInventory implements ITiamatPlayerInventory
{
    public static TiamatPlayerInventory tiamatClientInventory = null;
    public static LinkedHashMap<UUID, TiamatPlayerInventory> tiamatServerInventories = new LinkedHashMap<>();
    public static File playerDataFolder;

    public final NonNullList<ItemStack> sheathedMainhand1 = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> sheathedOffhand1 = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> sheathedMainhand2 = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> sheathedOffhand2 = NonNullList.withSize(1, ItemStack.EMPTY);

    public final NonNullList<ItemStack> armor = NonNullList.withSize(2, ItemStack.EMPTY);

    public final NonNullList<ItemStack> quickSlots = NonNullList.withSize(3, ItemStack.EMPTY);

    public final NonNullList<ItemStack> backpack = NonNullList.withSize(1, ItemStack.EMPTY);

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
    public EntityPlayer player;

    public TiamatPlayerInventory(EntityPlayer player)
    {
        allInventories = Arrays.asList(
                sheathedMainhand1, sheathedOffhand1,
                sheathedMainhand2, sheathedOffhand2,
                armor,
                quickSlots,
                backpack,
                pet,
                deck,
                classes, offensiveSkills, utilitySkills, ultimateSkill, passiveSkills,
                gatheringProfessions, craftingProfessions, craftingRecipes);

        this.player = player;
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

    public void dropAllItems()
    {
        for (int i = 0; i < getSizeInventory(); ++i)
        {
            ItemStack itemstack = getStackInSlot(i);
            if (!itemstack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemstack)) removeStackFromSlot(i);
        }

        for (List<ItemStack> list : Arrays.asList(
                sheathedMainhand1, sheathedOffhand1,
                sheathedMainhand2, sheathedOffhand2,
                armor,
                quickSlots,
                backpack
        ))
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
    }

    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if (player.isDead) return false;
        return player.getDistanceSq(this.player) <= 64.0D;
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
            File file = new File(playerDataFolder.getAbsolutePath() + File.separator + TiamatInventory.MODID + File.separator + player.getPersistentID() + File.separator + "inventory.dat");

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

            path += File.separator + TiamatInventory.MODID;
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
    public ItemStack getSheathedMainhand1()
    {
        return sheathedMainhand1.get(0);
    }

    @Override
    public void setSheathedMainhand1(ItemStack stack)
    {
        sheathedMainhand1.set(0, stack);
    }

    @Override
    public ItemStack getSheathedOffhand1()
    {
        return sheathedOffhand1.get(0);
    }

    @Override
    public void setSheathedOffhand1(ItemStack stack)
    {
        sheathedOffhand1.set(0, stack);
    }

    @Override
    public ItemStack getSheathedMainhand2()
    {
        return sheathedMainhand2.get(0);
    }

    @Override
    public void setSheathedMainhand2(ItemStack stack)
    {
        sheathedMainhand2.set(0, stack);
    }

    @Override
    public ItemStack getSheathedOffhand2()
    {
        return sheathedOffhand2.get(0);
    }

    @Override
    public void setSheathedOffhand2(ItemStack stack)
    {
        sheathedOffhand2.set(0, stack);
    }

    @Override
    public ArrayList<ItemStack> getTiamatArmor()
    {
        return new ArrayList<>(armor);
    }

    @Override
    public ItemStack getShoulders()
    {
        return armor.get(0);
    }

    @Override
    public void setShoulders(ItemStack stack)
    {
        armor.set(0, stack);
    }

    @Override
    public ItemStack getCape()
    {
        return armor.get(1);
    }

    @Override
    public void setCape(ItemStack stack)
    {
        armor.set(1, stack);
    }

    @Override
    public ArrayList<ItemStack> getQuickSlots()
    {
        return new ArrayList<>(quickSlots);
    }

    @Override
    public void setQuickSlot(int index, ItemStack stack)
    {
        quickSlots.set(index, stack);
    }

    @Override
    public ItemStack getBackpack()
    {
        return backpack.get(0);
    }

    @Override
    public void setBackpack(ItemStack stack)
    {
        backpack.set(0, stack);
    }

    @Override
    public ItemStack getPet()
    {
        return pet.get(0);
    }

    @Override
    public void setPet(ItemStack stack)
    {
        pet.set(0, stack);
    }

    @Override
    public ItemStack getDeck()
    {
        return deck.get(0);
    }

    @Override
    public void setDeck(ItemStack stack)
    {
        deck.set(0, stack);
    }

    @Override
    public ArrayList<ItemStack> getPlayerClasses()
    {
        return new ArrayList<>(classes);
    }

    @Override
    public void setPlayerClass(int index, ItemStack stack)
    {
        classes.set(index, stack);
    }

    @Override
    public ArrayList<ItemStack> getOffensiveSkills()
    {
        return new ArrayList<>(offensiveSkills);
    }

    @Override
    public void setOffensiveSkill(int index, ItemStack stack)
    {
        offensiveSkills.set(index, stack);
    }

    @Override
    public ArrayList<ItemStack> getUtilitySkills()
    {
        return new ArrayList<>(utilitySkills);
    }

    @Override
    public void setUtilitySkill(int index, ItemStack stack)
    {
        utilitySkills.set(index, stack);
    }

    @Override
    public ItemStack getUltimateSkill()
    {
        return ultimateSkill.get(0);
    }

    @Override
    public void setUltimateSkill(ItemStack stack)
    {
        ultimateSkill.set(0, stack);
    }

    @Override
    public ArrayList<ItemStack> getPassiveSkills()
    {
        return new ArrayList<>(passiveSkills);
    }

    @Override
    public void setPassiveSkill(int index, ItemStack stack)
    {
        passiveSkills.set(index, stack);
    }

    @Override
    public ArrayList<ItemStack> getGatheringProfessions()
    {
        return new ArrayList<>(gatheringProfessions);
    }

    @Override
    public void setGatheringProfession(int index, ItemStack stack)
    {
        gatheringProfessions.set(index, stack);
    }

    @Override
    public ArrayList<ItemStack> getCraftingProfessions()
    {
        return new ArrayList<>(craftingProfessions);
    }

    @Override
    public void setCraftingProfession(int index, ItemStack stack)
    {
        craftingProfessions.set(index, stack);
    }

    @Override
    public ArrayList<ItemStack> getCraftingRecipes()
    {
        return new ArrayList<>(craftingRecipes);
    }

    @Override
    public void setCraftingRecipe(int index, ItemStack stack)
    {
        craftingRecipes.set(index, stack);
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
            if (inventory == sheathedMainhand1 || inventory == sheathedOffhand1) continue;
            if (inventory == sheathedMainhand2 || inventory == sheathedOffhand2) continue;

            result.addAll(inventory);
        }
        return result;
    }


    public boolean isSheathed()
    {
        return player.getHeldItemMainhand().isEmpty() && player.getHeldItemOffhand().isEmpty();
    }

    public boolean forceEmptyHands()
    {
        //Return if sheathed or creative, or if hotbar is enabled
        if (TiamatInventory.playerHasHotbar(player) || isSheathed()) return true;


        //Sheathe normally if possible
        if (getSheathedMainhand1().isEmpty() && getSheathedOffhand1().isEmpty())
        {
            sheatheUnsheathe();
            return true;
        }
        if (getSheathedMainhand2().isEmpty() && getSheathedOffhand2().isEmpty())
        {
            sheatheUnsheathe(true);
            return true;
        }


        //Try to drop vanilla hand items if forced sheathing failed
        boolean dropFailed = false;
        for (int index : new int[]{player.inventory.currentItem, 40})
        {
            ItemStack stack = player.inventory.getStackInSlot(index);
            if (!stack.isEmpty())
            {
                if (stack.getItem().onDroppedByPlayer(stack, player))
                {
                    player.dropItem(stack, false, true);
                    player.inventory.setInventorySlotContents(index, ItemStack.EMPTY);
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, index, ItemStack.EMPTY));
                }
                else dropFailed = true;
            }
        }
        return !dropFailed;
    }

    public void sheatheUnsheathe()
    {
        if (getSheathedMainhand1().isEmpty() && getSheathedOffhand1().isEmpty()) swap();
        if (getSheathedMainhand1().isEmpty() && getSheathedOffhand1().isEmpty()) return;

        sheatheUnsheathe(false);
    }

    public void sheatheUnsheathe(boolean to2ndSet)
    {
        ItemStack swap1, swap2;
        if (!to2ndSet)
        {
            swap1 = player.getHeldItemMainhand();
            swap2 = getSheathedMainhand1();
            if (!(swap1.isEmpty() && swap2.isEmpty()))
            {
                setSheathedMainhand1(swap1);
                player.setHeldItem(EnumHand.MAIN_HAND, swap2);
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, player.inventory.currentItem, swap2));
            }

            swap1 = player.getHeldItemOffhand();
            swap2 = getSheathedOffhand1();
            if (!(swap1.isEmpty() && swap2.isEmpty()))
            {
                setSheathedOffhand1(swap1);
                player.setHeldItem(EnumHand.OFF_HAND, swap2);
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, 40, swap2));
            }
        }
        else
        {
            swap1 = player.getHeldItemMainhand();
            swap2 = getSheathedMainhand2();
            if (!(swap1.isEmpty() && swap2.isEmpty()))
            {
                setSheathedMainhand2(swap1);
                player.setHeldItem(EnumHand.MAIN_HAND, swap2);
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, player.inventory.currentItem, swap2));
            }

            swap1 = player.getHeldItemOffhand();
            swap2 = getSheathedOffhand2();
            if (!(swap1.isEmpty() && swap2.isEmpty()))
            {
                setSheathedOffhand2(swap1);
                player.setHeldItem(EnumHand.OFF_HAND, swap2);
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-2, 40, swap2));
            }
        }
    }

    public void swap()
    {
        if (isSheathed())
        {
            ItemStack swap = getSheathedMainhand1();
            setSheathedMainhand1(getSheathedMainhand2());
            setSheathedMainhand2(swap);

            swap = getSheathedOffhand1();
            setSheathedOffhand1(getSheathedOffhand2());
            setSheathedOffhand2(swap);
        }
        else
        {
            ItemStack swap = getSheathedMainhand2();
            if (swap.isEmpty() && getSheathedOffhand2().isEmpty()) return;

            setSheathedMainhand2(player.getHeldItemMainhand());
            player.setHeldItem(EnumHand.MAIN_HAND, swap);

            swap = getSheathedOffhand2();
            setSheathedOffhand2(player.getHeldItemOffhand());
            player.setHeldItem(EnumHand.OFF_HAND, swap);
        }
    }

    public void cycle(boolean right)
    {
        sheatheUnsheathe(right);

        ItemStack swap = getSheathedMainhand1();
        setSheathedMainhand1(getSheathedMainhand2());
        setSheathedMainhand2(swap);

        swap = getSheathedOffhand1();
        setSheathedOffhand1(getSheathedOffhand2());
        setSheathedOffhand2(swap);
    }
}