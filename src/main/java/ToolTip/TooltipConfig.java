package ToolTip;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class TooltipConfig {
    // 0xFF2D2D2D; // Темно-серый

    public static int BACKGROUND_COLOR = 0xFF2D2D2D;
    public static boolean USE_TEXTURE_BORDER = true;
    public static int BORDER_COLOR = 0xFFFFFFFF;
    public static int BORDER_THICKNESS = 1;

    public static int SEPARATOR_COLOR = 0x50FFFFFF;
    public static int SEPARATOR_THICKNESS = 1;
    public static int SEPARATOR_MARGIN = 2;
    public static int PADDING = 3;
    public static int TEXT_MARGIN = 2;

    // Text settings
    public static String NAME_COLOR = EnumChatFormatting.GOLD.toString();
    public static String OREDICT_COLOR = EnumChatFormatting.GRAY.toString();
    public static String MODNAME_COLOR = EnumChatFormatting.BLUE.toString();
    public static String TOOLTIP_COLOR = EnumChatFormatting.WHITE.toString();

    // Item settings
    public static float ITEM_SCALE = 2.0f;
    public static int ITEM_SIZE = 32;
    public static boolean USE_ITEM_RENDER = true;


    // Default texture
    public static ResourceLocation BORDER_TEXTURE = new ResourceLocation("tooltipimprove", "textures/gui/default/border.png");
    public static ResourceLocation SEPARATOR_TEXTURE = new ResourceLocation("tooltipimprove", "textures/gui/default/separator.png");
    public static ResourceLocation TOP_ICON_TEXTURE = new ResourceLocation("tooltipimprove", "textures/gui/default/icon.png");


}
