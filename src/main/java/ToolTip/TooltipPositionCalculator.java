package ToolTip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class TooltipPositionCalculator {

    public int[] calculateSafePosition(int mouseX, int mouseY, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        int tooltipX = mouseX + 12;
        int tooltipY = mouseY - 12;

        if (tooltipX + width > screenWidth) {
            tooltipX = Math.max(4, mouseX - 16 - width);
        }

        if (tooltipY + height > screenHeight) {
            tooltipY = Math.max(4, screenHeight - height - 4);
        }

        tooltipX = Math.max(4, tooltipX);
        tooltipY = Math.max(4, tooltipY);

        return new int[] { tooltipX, tooltipY };
    }
}
