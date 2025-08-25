package ToolTip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.util.List;

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

    public int getHeaderHeight(FontRenderer font, String displayName, String oredictName, String modName, boolean advancedSettings) {
        int height = 0;
        if (!displayName.isEmpty()) height += 10;
        if (!oredictName.isEmpty() && advancedSettings) height += 10;
        if (!modName.isEmpty()) height += 10;
        return Math.max(height, TooltipConfig.ITEM_SIZE);
    }

    public int calculateTooltipWidth(List<String> tooltip, FontRenderer font, String displayName, String oredictName, String modName, boolean advancedSettings) {
        int maxWidth = 0;
        int headerWidth = TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN;

        if (!displayName.isEmpty()) {
            headerWidth = Math.max(
                headerWidth,
                TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN + font.getStringWidth(displayName));
        }
        if (!oredictName.isEmpty() && advancedSettings) {
            headerWidth = Math.max(
                headerWidth,
                TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN + font.getStringWidth(oredictName));
        }
        if (!modName.isEmpty()) {
            headerWidth = Math
                .max(headerWidth, TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN + font.getStringWidth(modName));
        }

        maxWidth = headerWidth;

        if (hasActualTooltipContent(tooltip)) {
            for (String line : tooltip) {
                if (line != null && !line.trim().isEmpty()) {
                    maxWidth = Math.max(maxWidth, font.getStringWidth(line));
                }
            }
        }

        return maxWidth + TooltipConfig.PADDING * 2;
    }

    public int calculateTooltipHeight(List<String> tooltip, FontRenderer font, String displayName, String oredictName, String modName, boolean advancedSettings) {
        int height = TooltipConfig.PADDING * 2;
        int headerHeight = getHeaderHeight(font, displayName, oredictName, modName, advancedSettings);
        height += headerHeight;

        boolean hasTooltipContent = hasActualTooltipContent(tooltip);
        if (hasTooltipContent) {
            height += TooltipConfig.SEPARATOR_THICKNESS + TooltipConfig.SEPARATOR_MARGIN * 2;
            height += getTooltipContentHeight(tooltip);
        }

        return height;
    }

    public int getTooltipContentHeight(List<String> tooltip) {
        int height = 0;
        if (tooltip != null) {
            for (String line : tooltip) {
                if (line != null && !line.trim().isEmpty()) {
                    height += 10;
                }
            }
        }
        return height;
    }

    public boolean hasActualTooltipContent(List<String> tooltip) {
        if (tooltip == null || tooltip.isEmpty()) return false;
        for (String line : tooltip) {
            if (line != null && !line.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
