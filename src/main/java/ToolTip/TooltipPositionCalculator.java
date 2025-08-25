package ToolTip;

import net.minecraft.client.Minecraft;

public class TooltipPositionCalculator {

    private static final int TOOLTIP_OFFSET = 15;
    private static final int SCREEN_PADDING = 8;

    public int[] calculateSafePosition(int mouseX, int mouseY, int width, int height) {
        int screenWidth = Minecraft.getMinecraft().displayWidth;
        int screenHeight = Minecraft.getMinecraft().displayHeight;

        int tooltipX = calculateXPosition(mouseX, width, screenWidth);
        int tooltipY = calculateYPosition(mouseY, height, screenHeight);

        tooltipX = Math.max(SCREEN_PADDING, Math.min(tooltipX, screenWidth - width - SCREEN_PADDING));
        tooltipY = Math.max(SCREEN_PADDING, Math.min(tooltipY, screenHeight - height - SCREEN_PADDING));

        return new int[]{tooltipX, tooltipY};
    }

    private int calculateXPosition(int mouseX, int width, int screenWidth) {
        if (mouseX + width + TOOLTIP_OFFSET + SCREEN_PADDING <= screenWidth) {
            return mouseX + TOOLTIP_OFFSET;
        } else if (mouseX - width - TOOLTIP_OFFSET - SCREEN_PADDING >= 0) {
            return mouseX - width - TOOLTIP_OFFSET;
        } else {
            return mouseX > screenWidth / 2 ? screenWidth - width - SCREEN_PADDING : SCREEN_PADDING;
        }
    }

    private int calculateYPosition(int mouseY, int height, int screenHeight) {
        if (mouseY + height + SCREEN_PADDING <= screenHeight) {
            return mouseY + TOOLTIP_OFFSET;
        } else if (mouseY - height - SCREEN_PADDING >= 0) {
            return mouseY - height - TOOLTIP_OFFSET;
        } else {
            return mouseY > screenHeight / 2 ? screenHeight - height - SCREEN_PADDING : SCREEN_PADDING;
        }
    }
}
