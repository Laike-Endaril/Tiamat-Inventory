package com.fantasticsource.tiamatrpg;

import com.fantasticsource.tiamatrpg.inventory.TiamatInventoryGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static com.fantasticsource.tiamatrpg.TiamatRPG.MODID;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding
            TIAMAT_INVENTORY_KEY = new KeyBinding(MODID + ".key.inventory", KeyConflictContext.UNIVERSAL, Keyboard.KEY_R, MODID + ".keyCategory"),
            SWAP_WEAPONSETS_KEY = new KeyBinding(MODID + ".key.swapWeaponsets", KeyConflictContext.UNIVERSAL, Keyboard.KEY_F, MODID + ".keyCategory"),
            SHEATHE_UNSHEATHE = new KeyBinding(MODID + ".key.sheatheUnsheathe", KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL, Keyboard.KEY_F, MODID + ".keyCategory"),
            SKILLSET_1 = new KeyBinding(MODID + ".key.skillset1", KeyConflictContext.UNIVERSAL, Keyboard.KEY_Q, MODID + ".keyCategory"),
            SKILLSET_2 = new KeyBinding(MODID + ".key.skillset2", KeyConflictContext.UNIVERSAL, Keyboard.KEY_E, MODID + ".keyCategory"),
            DODGE = new KeyBinding(MODID + ".key.dodge", KeyConflictContext.UNIVERSAL, Keyboard.KEY_V, MODID + ".keyCategory");


    public static boolean
            skillset1Pressed = false,
            skillset2Pressed = false,
            skillset1Locked = false,
            skillset2Locked = false;

    public static int controlModifier = 0;


    public static void init(FMLPreInitializationEvent event)
    {
        for (KeyBinding keyBinding : new KeyBinding[]{TIAMAT_INVENTORY_KEY, SWAP_WEAPONSETS_KEY, SHEATHE_UNSHEATHE, SKILLSET_1, SKILLSET_2, DODGE})
        {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @SubscribeEvent
    public static void keyPress(InputEvent event)
    {
        if (isPressed(DODGE))
        {
            //TODO
        }

        if (isPressed(SKILLSET_1))
        {
            if (!skillset1Locked)
            {
                if (!skillset1Pressed)
                {
                    skillset2Pressed = false;
                    controlModifier = 1;
                    Network.WRAPPER.sendToServer(new Network.ControlAndActionPacket(1, -1));
                }
                skillset1Pressed = true;
            }
        }
        else
        {
            if (skillset1Pressed)
            {
                if (controlModifier == 1)
                {
                    controlModifier = 0;
                    Network.WRAPPER.sendToServer(new Network.ControlAndActionPacket(0, 0));
                }
            }
            skillset1Pressed = false;
            skillset1Locked = false;
        }

        if (isPressed(SKILLSET_2))
        {
            if (!skillset2Locked)
            {
                if (!skillset2Pressed)
                {
                    skillset1Pressed = false;
                    controlModifier = 2;
                    Network.WRAPPER.sendToServer(new Network.ControlAndActionPacket(2, -1));
                }
                skillset2Pressed = true;
            }
        }
        else
        {
            if (skillset2Pressed)
            {
                if (controlModifier == 2)
                {
                    controlModifier = 0;
                    Network.WRAPPER.sendToServer(new Network.ControlAndActionPacket(0, 3));
                }
            }
            skillset2Pressed = false;
            skillset2Locked = false;
        }

        if (isPressed(SHEATHE_UNSHEATHE))
        {
            //TODO
        }

        if (isPressed(SWAP_WEAPONSETS_KEY))
        {
            Network.WRAPPER.sendToServer(new Network.SwapWeaponsetPacket());
        }

        if (isPressed(TIAMAT_INVENTORY_KEY))
        {
            Minecraft.getMinecraft().displayGuiScreen(new TiamatInventoryGUI());
            Network.WRAPPER.sendToServer(new Network.OpenTiamatInventoryPacket());
        }
    }

    public static boolean isPressed(KeyBinding keyBinding)
    {
        return keyBinding.isPressed() && keyBinding.getKeyConflictContext().isActive();
    }

    @SubscribeEvent
    public static void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        skillset1Pressed = false;
        skillset2Pressed = false;
        controlModifier = 0;
    }
}
