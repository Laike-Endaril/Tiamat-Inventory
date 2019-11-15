package com.fantasticsource.tiamatrpgmain;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static com.fantasticsource.tiamatrpgmain.TiamatRPGMain.MODID;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding TIAMAT_INVENTORY_KEY = new KeyBinding(MODID + ".key.inventory", KeyConflictContext.UNIVERSAL, Keyboard.KEY_E, MODID + ".keyCategory");

    public static void init(FMLPreInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(TIAMAT_INVENTORY_KEY);
    }
}
