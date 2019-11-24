package com.fantasticsource.tiamatrpg;

import com.fantasticsource.tiamatrpg.inventory.InterfaceTiamatInventory;
import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryContainer;
import com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(LeftClickEmptyPacketHandler.class, LeftClickEmptyPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(OpenTiamatInventoryPacketHandler.class, OpenTiamatInventoryPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(SwapWeaponsetPacketHandler.class, SwapWeaponsetPacket.class, discriminator++, Side.SERVER);
    }


    public static class LeftClickEmptyPacket implements IMessage
    {
        @Override
        public void toBytes(ByteBuf buf)
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }
    }

    public static class LeftClickEmptyPacketHandler implements IMessageHandler<LeftClickEmptyPacket, IMessage>
    {
        @Override
        public IMessage onMessage(LeftClickEmptyPacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                try
                {
                    EntityPlayerMP player = ctx.getServerHandler().player;
                    if (player.inventory.currentItem == 0) Attacks.tryAttack(player, EntityLivingBase.class);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            });
            return null;
        }
    }


    public static class OpenTiamatInventoryPacket implements IMessage
    {
        @Override
        public void toBytes(ByteBuf buf)
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }
    }

    public static class OpenTiamatInventoryPacketHandler implements IMessageHandler<OpenTiamatInventoryPacket, IMessage>
    {
        @Override
        public IMessage onMessage(OpenTiamatInventoryPacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                InterfaceTiamatInventory iface = new InterfaceTiamatInventory();

                EntityPlayerMP player = ctx.getServerHandler().player;
                player.getNextWindowId();
                player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, iface.getGuiID(), iface.getDisplayName()));

                player.openContainer = iface.createContainer(player.inventory, player);
                player.openContainer.windowId = player.currentWindowId;
                player.openContainer.addListener(player);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, new TiamatInventoryContainer(player)));

                player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
            });
            return null;
        }
    }


    public static class SwapWeaponsetPacket implements IMessage
    {
        @Override
        public void toBytes(ByteBuf buf)
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }
    }

    public static class SwapWeaponsetPacketHandler implements IMessageHandler<SwapWeaponsetPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SwapWeaponsetPacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                InventoryPlayer inventory = player.inventory;
                TiamatPlayerInventory tiamatInventory = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());
                if (inventory == null || tiamatInventory == null) return;


                ItemStack mainhand = tiamatInventory.mainhand.get(0);
                ItemStack offhand = tiamatInventory.offhand.get(0);
                tiamatInventory.mainhand.set(0, inventory.mainInventory.get(0));
                tiamatInventory.offhand.set(0, inventory.offHandInventory.get(0));
                inventory.mainInventory.set(0, mainhand);
                inventory.offHandInventory.set(0, offhand);
            });
            return null;
        }
    }
}
