package ToolTip;

import com.gtnewhorizon.gtnhlib.client.event.RenderTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class TooltipHandlerEvent {

    private final TooltipRenderer renderer = new TooltipRenderer();
    private final TooltipPositionCalculator positionCalculator = new TooltipPositionCalculator();

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent event) {
        event.alternativeRenderer = tooltip -> {
            if (tooltip.isEmpty()) return;

            int width = renderer.calculateTooltipWidth(tooltip, event.font);
            int height = renderer.calculateTooltipHeight(tooltip);

            int[] position = positionCalculator.calculateSafePosition(event.x, event.y, width, height);

            renderer.renderCustomTooltip(tooltip, event.font, position[0], position[1], width, height, event.itemStack);
        };
    }
}




