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

        // Если тултип выходит за правую границу экрана
        if (tooltipX + width > screenWidth) {
            tooltipX = Math.max(4, mouseX - 16 - width);
        }

        // Если тултип выходит за нижнюю границу экрана
        if (tooltipY + height > screenHeight) {
            tooltipY = Math.max(4, screenHeight - height - 4);
        }

        // Гарантируем, что тултип не выйдет за левую границу
        if (tooltipX < 4) {
            tooltipX = 4;
        }

        // Гарантируем, что тултип не выйдет за верхнюю границу
        if (tooltipY < 4) {
            tooltipY = 4;
        }

        return new int[]{tooltipX, tooltipY};
    }
}
