package com.fantasticsource.tiamatinventory;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fantasticsource.tiamatinventory.TiamatInventory.MODID;
import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.WHITE;

public class Commands extends CommandBase
{
    private static ArrayList<String> subcommands = new ArrayList<>(Arrays.asList("setSlot"));

    private static ArrayList<String> slots = new ArrayList<>(Arrays.asList(
            "mainhand1",
            "offhand1",
            "mainhand2",
            "offhand2",
            "shoulders",
            "cape",
            "quickslot1",
            "quickslot2",
            "quickslot3",
            "backpack",
            "pet",
            "deck",
            "class1",
            "class2",
            "offensive1",
            "offensive2",
            "utility1",
            "utility2",
            "ultimate",
            "passive1",
            "passive2",
            "gathering1",
            "gathering2",
            "crafting1",
            "crafting2",
            "recipe1",
            "recipe2",
            "recipe3",
            "recipe4",
            "recipe5",
            "recipe6",
            "recipe7",
            "recipe8",
            "recipe9",
            "recipe10",
            "recipe11",
            "recipe12",
            "recipe13",
            "recipe14",
            "recipe15"
    ));


    @Override
    public String getName()
    {
        return MODID;
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return subUsage("");
    }

    public String subUsage(String subcommand)
    {
        if (!subcommands.contains(subcommand))
        {
            StringBuilder s = new StringBuilder(AQUA + "/" + getName() + " <" + subcommands.get(0));
            for (int i = 1; i < subcommands.size(); i++) s.append(" | ").append(subcommands.get(i));
            s.append(">");
            return s.toString();
        }

        switch (subcommand)
        {
            default:
                return AQUA + "/" + getName() + " " + subcommand + WHITE + " - " + I18n.translateToLocalFormatted(MODID + ".cmd." + subcommand + ".comment");
        }
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0) sender.getCommandSenderEntity().sendMessage(new TextComponentString(subUsage("")));
        else subCommand(sender, args);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        ArrayList<String> result = new ArrayList<>();

        String partial = args[args.length - 1];
        switch (args.length)
        {
            case 1:
                result.addAll(subcommands);
                break;

            case 2:
                switch (args[0])
                {
                    case "setSlot":
                        result.addAll(slots);
                        break;
                }
                break;
        }

        if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
        return result;
    }

    private void subCommand(ICommandSender sender, String[] args)
    {
        String cmd = args[0];
        switch (cmd)
        {
            case "setSlot":
                if (!(sender instanceof EntityPlayerMP))
                {
                    notifyCommandListener(sender, this, MODID + ".error.notPlayer", cmd);
                }
                else if (args.length < 2) notifyCommandListener(sender, this, subUsage(cmd));
                else
                {
                    EntityPlayerMP player = (EntityPlayerMP) sender;
                    TiamatPlayerInventory inventory = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());

                    ItemStack stack;
                    switch (args[1])
                    {
                        case "mainhand1":
                            stack = inventory.getSheathedMainhand1();
                            inventory.setSheathedMainhand1(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "offhand1":
                            stack = inventory.getSheathedOffhand1();
                            inventory.setSheathedOffhand1(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "mainhand2":
                            stack = inventory.getSheathedMainhand2();
                            inventory.setSheathedMainhand2(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "offhand2":
                            stack = inventory.getSheathedOffhand2();
                            inventory.setSheathedOffhand2(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "shoulders":
                            stack = inventory.getShoulders();
                            inventory.setShoulders(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "cape":
                            stack = inventory.getCape();
                            inventory.setCape(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "quickslot1":
                            stack = inventory.getQuickSlots().get(0);
                            inventory.setQuickSlot(0, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "quickslot2":
                            stack = inventory.getQuickSlots().get(1);
                            inventory.setQuickSlot(1, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "quickslot3":
                            stack = inventory.getQuickSlots().get(2);
                            inventory.setQuickSlot(2, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "backpack":
                            stack = inventory.getBackpack();
                            inventory.setBackpack(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "pet":
                            stack = inventory.getPet();
                            inventory.setPet(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "deck":
                            stack = inventory.getDeck();
                            inventory.setDeck(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "class1":
                            stack = inventory.getPlayerClasses().get(0);
                            inventory.setPlayerClass(0, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "class2":
                            stack = inventory.getPlayerClasses().get(1);
                            inventory.setPlayerClass(1, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "offensive1":
                            stack = inventory.getOffensiveSkills().get(0);
                            inventory.setOffensiveSkill(0, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "offensive2":
                            stack = inventory.getOffensiveSkills().get(1);
                            inventory.setOffensiveSkill(1, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "utility1":
                            stack = inventory.getUtilitySkills().get(0);
                            inventory.setUtilitySkill(0, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "utility2":
                            stack = inventory.getUtilitySkills().get(1);
                            inventory.setUtilitySkill(1, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "ultimate":
                            stack = inventory.getUltimateSkill();
                            inventory.setUltimateSkill(MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "passive1":
                            stack = inventory.getPassiveSkills().get(0);
                            inventory.setPassiveSkill(0, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "passive2":
                            stack = inventory.getPassiveSkills().get(1);
                            inventory.setPassiveSkill(1, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "gathering1":
                            stack = inventory.getGatheringProfessions().get(0);
                            inventory.setGatheringProfession(0, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "gathering2":
                            stack = inventory.getGatheringProfessions().get(1);
                            inventory.setGatheringProfession(1, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "crafting1":
                            stack = inventory.getCraftingProfessions().get(0);
                            inventory.setCraftingProfession(0, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "crafting2":
                            stack = inventory.getCraftingProfessions().get(1);
                            inventory.setCraftingProfession(1, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe1":
                            stack = inventory.getCraftingRecipes().get(0);
                            inventory.setCraftingRecipe(0, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe2":
                            stack = inventory.getCraftingRecipes().get(1);
                            inventory.setCraftingRecipe(1, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe3":
                            stack = inventory.getCraftingRecipes().get(2);
                            inventory.setCraftingRecipe(2, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe4":
                            stack = inventory.getCraftingRecipes().get(3);
                            inventory.setCraftingRecipe(3, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe5":
                            stack = inventory.getCraftingRecipes().get(4);
                            inventory.setCraftingRecipe(4, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe6":
                            stack = inventory.getCraftingRecipes().get(5);
                            inventory.setCraftingRecipe(5, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe7":
                            stack = inventory.getCraftingRecipes().get(6);
                            inventory.setCraftingRecipe(6, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe8":
                            stack = inventory.getCraftingRecipes().get(7);
                            inventory.setCraftingRecipe(7, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe9":
                            stack = inventory.getCraftingRecipes().get(8);
                            inventory.setCraftingRecipe(8, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe10":
                            stack = inventory.getCraftingRecipes().get(9);
                            inventory.setCraftingRecipe(9, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe11":
                            stack = inventory.getCraftingRecipes().get(10);
                            inventory.setCraftingRecipe(10, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe12":
                            stack = inventory.getCraftingRecipes().get(11);
                            inventory.setCraftingRecipe(11, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe13":
                            stack = inventory.getCraftingRecipes().get(12);
                            inventory.setCraftingRecipe(12, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe14":
                            stack = inventory.getCraftingRecipes().get(13);
                            inventory.setCraftingRecipe(13, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        case "recipe15":
                            stack = inventory.getCraftingRecipes().get(14);
                            inventory.setCraftingRecipe(14, MCTools.cloneItemStack(player.getHeldItemMainhand()));
                            break;

                        default:
                            notifyCommandListener(sender, this, subUsage(cmd));
                            return;
                    }

                    player.setHeldItem(EnumHand.MAIN_HAND, stack);
                }
                break;

            default:
                notifyCommandListener(sender, this, subUsage(cmd));
        }
    }
}
