package ToolTip;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class InputHandlerEvent {

    private boolean wasZPressed = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        boolean isPressed = Keyboard.isKeyDown(Keyboard.KEY_Z);

        if (PaginationHelper.isTooltipActive()) {
            if (isPressed && !wasZPressed) {
                PaginationHelper.nextPage();
            }
            wasZPressed = isPressed;
        }
    }
}
