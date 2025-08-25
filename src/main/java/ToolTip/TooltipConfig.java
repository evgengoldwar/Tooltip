package ToolTip;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class TooltipConfig {
//    0xFF2D2D2D; // Темно-серый

    public static int BACKGROUND_COLOR = 0xFF2D2D2D; // Темно-серый
    public static ResourceLocation BACKGROUND_TEXTURE = null;

    public static int BORDER_TOP_COLOR = 0xFFFFFFFF;    // Красный
    public static int BORDER_RIGHT_COLOR = 0xFFFFFFFF;  // Зеленый
    public static int BORDER_BOTTOM_COLOR = 0xFFFFFFFF; // Синий
    public static int BORDER_LEFT_COLOR = 0xFFFFFFFF;   // Желтый

    public static int BORDER_TOP_THICKNESS = 2;
    public static int BORDER_RIGHT_THICKNESS = 2;
    public static int BORDER_BOTTOM_THICKNESS = 2;
    public static int BORDER_LEFT_THICKNESS = 2;

    public static int SEPARATOR_COLOR = 0x50FFFFFF;     // Полупрозрачный белый
    public static int SEPARATOR_THICKNESS = 5;          // Тонкая линия

    public static String NAME_COLOR = EnumChatFormatting.GOLD.toString();
    public static String TEXT_COLOR = EnumChatFormatting.WHITE.toString();

    public static void setBackgroundColor(int color) {
        BACKGROUND_COLOR = color;
    }

    public static void setBackgroundTexture(ResourceLocation texture) {
        BACKGROUND_TEXTURE = texture;
    }

    public static void setBorderColors(int top, int right, int bottom, int left) {
        BORDER_TOP_COLOR = top;
        BORDER_RIGHT_COLOR = right;
        BORDER_BOTTOM_COLOR = bottom;
        BORDER_LEFT_COLOR = left;
    }

    public static void setBorderThickness(int top, int right, int bottom, int left) {
        BORDER_TOP_THICKNESS = top;
        BORDER_RIGHT_THICKNESS = right;
        BORDER_BOTTOM_THICKNESS = bottom;
        BORDER_LEFT_THICKNESS = left;
    }

    public static void setSeparator(int color, int thickness) {
        SEPARATOR_COLOR = color;
        SEPARATOR_THICKNESS = thickness;
    }

    public static void setTextColors(String nameColor, String textColor) {
        NAME_COLOR = nameColor;
        TEXT_COLOR = textColor;
    }
}
