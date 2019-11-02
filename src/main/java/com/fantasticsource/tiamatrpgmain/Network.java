package com.fantasticsource.tiamatrpgmain;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(LeftClickEmptyPacketHandler.class, LeftClickEmptyPacket.class, discriminator++, Side.SERVER);
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
                    Attacks.tryAttack(ctx.getServerHandler().player, EntityLivingBase.class);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            });
            return null;
        }
    }
}
