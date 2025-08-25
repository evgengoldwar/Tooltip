package com.EvgenWarGold.TooltipImprove;

import net.minecraftforge.common.MinecraftForge;

import ToolTip.InputHandlerEvent;
import ToolTip.TooltipHandlerEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        FMLCommonHandler.instance()
            .bus()
            .register(new InputHandlerEvent());
        FMLCommonHandler.instance()
            .bus()
            .register(new TooltipHandlerEvent());
        MinecraftForge.EVENT_BUS.register(new InputHandlerEvent());
        MinecraftForge.EVENT_BUS.register(new TooltipHandlerEvent());
    }
}
