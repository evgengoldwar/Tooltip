package ToolTip;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TooltipInputHandler {

    private boolean wasZPressed = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        boolean isZPressed = Keyboard.isKeyDown(Keyboard.KEY_Z);

        if (TooltipRenderer.isTooltipActive()) {
            if (isZPressed && !wasZPressed) {
                TooltipRenderer.nextPage();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
                TooltipRenderer.previousPage();
            }
            wasZPressed = isZPressed;
        }
    }
}
