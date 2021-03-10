package com.fantasticsource.tiamatinventory;

import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.tiamatinventory.config.TiamatConfig;
import com.fantasticsource.tiamatinventory.inventory.ClientInventoryData;
import com.fantasticsource.tiamatinventory.inventory.InterfaceTiamatInventory;
import com.fantasticsource.tiamatinventory.inventory.TiamatInventoryContainer;
import com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.inventory.inventoryhacks.ClientInventoryHacks;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(TiamatInventory.MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(OpenTiamatInventoryPacketHandler.class, OpenTiamatInventoryPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(InventoryDataPacketHandler.class, InventoryDataPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(PickupSoundPacketHandler.class, PickupSoundPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(TiamatItemSyncPacketHandler.class, TiamatItemSyncPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(SheatheUnsheathePacketHandler.class, SheatheUnsheathePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(SwapWeaponsetsPacketHandler.class, SwapWeaponsetsPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(ToggleRenderModePacketHandler.class, ToggleRenderModePacket.class, discriminator++, Side.SERVER);
    }


    public static class PickupSoundPacket implements IMessage
    {
        double x, y, z;

        public PickupSoundPacket()
        {
            //Required
        }

        public PickupSoundPacket(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeDouble(x);
            buf.writeDouble(y);
            buf.writeDouble(z);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            x = buf.readDouble();
            y = buf.readDouble();
            z = buf.readDouble();
        }
    }

    public static class PickupSoundPacketHandler implements IMessageHandler<PickupSoundPacket, IMessage>
    {
        @Override
        public IMessage onMessage(PickupSoundPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().world.playSound(packet.x, packet.y, packet.z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (float) ((Math.random() - Math.random()) * 1.4 + 2), false));
            }

            return null;
        }
    }


    public static class InventoryDataPacket implements IMessage
    {
        public int inventorySize, craftW, craftH;
        public boolean allowHotbar;
        public String[] syncedAttributes;

        public InventoryDataPacket()
        {
            //Required
        }

        public InventoryDataPacket(int inventorySize, int craftW, int craftH, boolean allowHotbar)
        {
            this.inventorySize = inventorySize;
            this.craftW = craftW;
            this.craftH = craftH;
            this.allowHotbar = allowHotbar;
            this.syncedAttributes = TiamatConfig.serverSettings.attributesToSync;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(inventorySize);
            buf.writeInt(craftW);
            buf.writeInt(craftH);
            buf.writeBoolean(allowHotbar);
            buf.writeInt(syncedAttributes.length);
            for (String attribute : syncedAttributes) ByteBufUtils.writeUTF8String(buf, attribute);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            inventorySize = buf.readInt();
            craftW = buf.readInt();
            craftH = buf.readInt();
            allowHotbar = buf.readBoolean();
            syncedAttributes = new String[buf.readInt()];
            for (int i = 0; i < syncedAttributes.length; i++) syncedAttributes[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class InventoryDataPacketHandler implements IMessageHandler<InventoryDataPacket, IMessage>
    {
        @Override
        public IMessage onMessage(InventoryDataPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    ClientInventoryData.inventorySize = packet.inventorySize;
                    ClientInventoryData.craftW = packet.craftW;
                    ClientInventoryData.craftH = packet.craftH;
                    ClientInventoryData.allowHotbar = packet.allowHotbar;
                    ClientInventoryData.additionalSyncedAttributes = packet.syncedAttributes;

                    ClientInventoryHacks.update();
                });
            }

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
                EntityPlayerMP player = ctx.getServerHandler().player;
                TiamatPlayerInventory inventory = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());
                if (inventory != null && !inventory.forceEmptyHands()) return; //Don't open if we failed to force sheathe

                InterfaceTiamatInventory iface = new InterfaceTiamatInventory();

                player.getNextWindowId();
                player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, iface.getGuiID(), iface.getDisplayName()));

                player.openContainer = iface.createContainer(player.inventory, player);
                player.openContainer.windowId = player.currentWindowId;
                player.openContainer.addListener(player);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, new TiamatInventoryContainer(player)));
            });
            return null;
        }
    }


    public static class TiamatItemSyncPacket implements IMessage
    {
        public ItemStack[] newTiamatItems;

        public TiamatItemSyncPacket()
        {
            //Required
        }

        public TiamatItemSyncPacket(ItemStack[] newTiamatItems)
        {
            this.newTiamatItems = newTiamatItems;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            CItemStack cstack = new CItemStack();
            buf.writeInt(newTiamatItems.length);
            for (ItemStack stack : newTiamatItems)
            {
                cstack.set(stack).write(buf);
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            CItemStack cstack = new CItemStack();
            newTiamatItems = new ItemStack[buf.readInt()];
            for (int i = 0; i < newTiamatItems.length; i++)
            {
                newTiamatItems[i] = cstack.read(buf).value;
            }
        }
    }

    public static class TiamatItemSyncPacketHandler implements IMessageHandler<TiamatItemSyncPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(TiamatItemSyncPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() ->
                {
                    TiamatPlayerInventory inventory = TiamatPlayerInventory.tiamatClientInventory;
                    if (inventory == null)
                    {
                        inventory = new TiamatPlayerInventory(mc.player);
                        TiamatPlayerInventory.tiamatClientInventory = inventory;
                    }

                    int i = 0;
                    for (ItemStack stack : packet.newTiamatItems)
                    {
                        inventory.setInventorySlotContents(i++, stack);
                    }
                });
            }

            return null;
        }
    }


    public static class SheatheUnsheathePacket implements IMessage
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

    public static class SheatheUnsheathePacketHandler implements IMessageHandler<SheatheUnsheathePacket, IMessage>
    {
        @Override
        public IMessage onMessage(SheatheUnsheathePacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                TiamatPlayerInventory inventory = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());
                if (inventory != null) inventory.sheatheUnsheatheKeyPressed();
            });
            return null;
        }
    }


    public static class SwapWeaponsetsPacket implements IMessage
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

    public static class SwapWeaponsetsPacketHandler implements IMessageHandler<SwapWeaponsetsPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SwapWeaponsetsPacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                TiamatPlayerInventory inventory = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());
                if (inventory != null) inventory.swapKeyPressed();
            });
            return null;
        }
    }


    public static class ToggleRenderModePacket implements IMessage
    {
        String rendermode;

        public ToggleRenderModePacket()
        {
        }

        public ToggleRenderModePacket(String rendermode)
        {
            this.rendermode = rendermode;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, rendermode);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            rendermode = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class ToggleRenderModePacketHandler implements IMessageHandler<ToggleRenderModePacket, IMessage>
    {
        @Override
        public IMessage onMessage(ToggleRenderModePacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                String rendermode = packet.rendermode;
                switch (rendermode)
                {
                    case "HeadControl":
                    case "CapeInvControl":
                        if ("On".equals(RenderModes.getRenderMode(player, rendermode))) RenderModes.setRenderMode(player, rendermode, "Off");
                        else RenderModes.setRenderMode(player, rendermode, "On");
                        break;

                    case "Shoulders":
                        if ("On".equals(RenderModes.getRenderMode(player, "ShoulderLControl")))
                        {
                            if ("On".equals(RenderModes.getRenderMode(player, "ShoulderRControl")))
                            {
                                RenderModes.setRenderMode(player, "ShoulderLControl", "Off");
                                RenderModes.setRenderMode(player, "ShoulderRControl", "Off");
                            }
                            else
                            {
                                RenderModes.setRenderMode(player, "ShoulderLControl", "Off");
                                RenderModes.setRenderMode(player, "ShoulderRControl", "On");
                            }
                        }
                        else
                        {
                            if ("On".equals(RenderModes.getRenderMode(player, "ShoulderRControl")))
                            {
                                RenderModes.setRenderMode(player, "ShoulderLControl", "On");
                                RenderModes.setRenderMode(player, "ShoulderRControl", "On");
                            }
                            else
                            {
                                RenderModes.setRenderMode(player, "ShoulderLControl", "On");
                                RenderModes.setRenderMode(player, "ShoulderRControl", "Off");
                            }
                        }
                }
            });
            return null;
        }
    }
}
