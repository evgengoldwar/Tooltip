package ToolTip;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class InputHandlerEvent {

    private boolean wasZPressed = false;
    private final boolean isZPressed = Keyboard.isKeyDown(Keyboard.KEY_Z);

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (PaginationHelper.isTooltipActive()) {
            if (isZPressed && !wasZPressed) {
                PaginationHelper.nextPage();
            }
            wasZPressed = isZPressed;
        }
    }
}
