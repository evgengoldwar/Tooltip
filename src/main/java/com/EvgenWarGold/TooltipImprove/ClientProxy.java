package com.EvgenWarGold.TooltipImprove;

import net.minecraftforge.common.MinecraftForge;

import ToolTip.TooltipHandlerEvent;
import ToolTip.TooltipInputHandlerEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        FMLCommonHandler.instance()
            .bus()
            .register(new TooltipInputHandlerEvent());
        FMLCommonHandler.instance()
            .bus()
            .register(new TooltipHandlerEvent());
        MinecraftForge.EVENT_BUS.register(new TooltipInputHandlerEvent());
        MinecraftForge.EVENT_BUS.register(new TooltipHandlerEvent());
    }
}
