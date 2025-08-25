package ToolTip;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.GuiContainerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TooltipRenderer {

    private String oredictName = "";
    private String modName = "";
    private String displayName = "";

    private boolean advancedSettings = false;
    private static final RenderItem itemRenderer = new RenderItem();

    public void setAdditionalInfo(String oredict, String mod, String displayName) {
        this.oredictName = oredict;
        this.modName = mod;
        this.displayName = displayName;
    }

    public void renderCustomTooltip(List<String> tooltip, FontRenderer font, int x, int y, int width, int height, ItemStack stack) {
        if (tooltip.isEmpty()) return;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        checkAdvancedSettings();

        if (!tooltip.isEmpty()) tooltip.remove(0);

        // Определяем, есть ли дополнительный контент тултипа
        boolean hasTooltipContent = hasActualTooltipContent(tooltip);

        // Минимальные размеры для тултипа без контента
        if (!hasTooltipContent) {
            width = Math.max(width, TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN * 3 + Math.max(font.getStringWidth(displayName), font.getStringWidth(modName)));
            height = Math.min(height, TooltipConfig.ITEM_SIZE + TooltipConfig.PADDING * 2);
        } else {
            Optional<String> longestWord = tooltip.stream()
                .max(Comparator.comparingInt(String::length));
            width = Math.max(width, TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN * 3 + Math.max(font.getStringWidth(displayName), Math.max(font.getStringWidth(modName), font.getStringWidth(String.valueOf(longestWord)))));
        }



        // Рендер фона
        drawRect(x - 3, y - 3, x + width + 3, y + height + 3, TooltipConfig.BACKGROUND_COLOR);

        // Рендер границы
        drawBorder(x - 3, y - 3, x + width + 3, y + height + 3,
            TooltipConfig.BORDER_COLOR, TooltipConfig.BORDER_THICKNESS);

        // Рендер предмета
        int itemX = x + TooltipConfig.PADDING;
        int itemY = y + TooltipConfig.PADDING;
        drawItemStack(stack, itemX, itemY);

        // Рендер информации о предмете (название, oredict, мод)
        int textX = itemX + TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN;
        int textY = itemY;
        renderItemInfo(font, textX, textY);

        // Рендер разделительной линии только если есть дополнительный контент
        if (hasTooltipContent) {
            int headerHeight = Math.max(TooltipConfig.ITEM_SIZE, getHeaderHeight(font));
            int separatorY = y + TooltipConfig.PADDING + headerHeight + TooltipConfig.SEPARATOR_MARGIN;
            drawSeparator(x + TooltipConfig.PADDING, separatorY, width - TooltipConfig.PADDING * 2);

            // Рендер основного тултипа
            int tooltipStartY = separatorY + TooltipConfig.SEPARATOR_THICKNESS + TooltipConfig.SEPARATOR_MARGIN;
            renderTooltipContent(tooltip, font, x + TooltipConfig.PADDING, tooltipStartY);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private boolean hasActualTooltipContent(List<String> tooltip) {
        if (tooltip.isEmpty()) return false;

        // Проверяем, есть ли реальный контент (не пустые строки)
        for (String line : tooltip) {
            if (line != null && !line.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void checkAdvancedSettings() {
        advancedSettings = Minecraft.getMinecraft().gameSettings.advancedItemTooltips;

    }

    private void drawRect(int left, int top, int right, int bottom, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(left, bottom, 0);
        tessellator.addVertex(right, bottom, 0);
        tessellator.addVertex(right, top, 0);
        tessellator.addVertex(left, top, 0);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawBorder(int left, int top, int right, int bottom, int color, int thickness) {
        if (thickness <= 0) return;

        drawRect(left, top, right, top + thickness, color);

        drawRect(right - thickness, top, right, bottom, color);

        drawRect(left, bottom - thickness, right, bottom, color);

        drawRect(left, top, left + thickness, bottom, color);
    }

    private void drawSeparator(int x, int y, int width) {
        drawRect(x, y, x + width, y + TooltipConfig.SEPARATOR_THICKNESS, TooltipConfig.SEPARATOR_COLOR);
    }

    private void renderItemInfo(FontRenderer font, int x, int y) {
        int currentY = y;


        font.drawStringWithShadow(TooltipConfig.NAME_COLOR + displayName, x, currentY, 0xFFFFFF);
        currentY += 10;


        if (!oredictName.isEmpty() && advancedSettings) {
            font.drawStringWithShadow(TooltipConfig.OREDICT_COLOR + oredictName, x, currentY, 0xFFFFFF);
            currentY += 10;
        }


        if (!modName.isEmpty()) {
            font.drawStringWithShadow(TooltipConfig.MODNAME_COLOR + modName, x, currentY, 0xFFFFFF);
        }
    }

    private void renderTooltipContent(List<String> tooltip, FontRenderer font, int x, int y) {
        int currentY = y;
        for (String line : tooltip) {
            if (line != null && !line.trim().isEmpty()) {
                font.drawStringWithShadow(TooltipConfig.TOOLTIP_COLOR + line, x, currentY, 0xFFFFFF);
                currentY += 10;
            }
        }
    }

    private int getHeaderHeight(FontRenderer font) {
        int height = 10;
        if (!oredictName.isEmpty()) height += 10;
        if (!modName.isEmpty()) height += 10;
        return height;
    }

    public int calculateTooltipWidth(List<String> tooltip, FontRenderer font) {
        int width = TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN * 3;

        // Ширина текста информации о предмете
        width = Math.max(width, font.getStringWidth(displayName) + TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN * 2);
        if (!oredictName.isEmpty() && advancedSettings) {
            width = Math.max(width, font.getStringWidth(oredictName) + TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN * 2);
        }
        if (!modName.isEmpty()) {
            width = Math.max(width, font.getStringWidth(modName) + TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN * 2);
        }

        // Если есть дополнительный контент тултипа, учитываем его ширину
        if (hasActualTooltipContent(tooltip)) {
            for (String line : tooltip) {
                if (line != null && !line.trim().isEmpty()) {
                    width = Math.max(width, font.getStringWidth(line) + TooltipConfig.PADDING * 2);
                }
            }
        }

        return Math.min(width + TooltipConfig.PADDING * 2, font.getStringWidth(displayName) + TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN * 4);
    }

    public int calculateTooltipHeight(List<String> tooltip, FontRenderer font) {
        int height = TooltipConfig.PADDING * 2;

        // Высота заголовка (предмет + информация)
        int headerHeight = Math.max(TooltipConfig.ITEM_SIZE, getHeaderHeight(font));
        height += headerHeight;

        // Добавляем высоту тултипа только если есть реальный контент
        boolean hasTooltipContent = hasActualTooltipContent(tooltip);
        if (hasTooltipContent) {
            height += TooltipConfig.SEPARATOR_THICKNESS + TooltipConfig.SEPARATOR_MARGIN * 2;
            height += getTooltipContentHeight(tooltip);
        }

        // Ограничиваем минимальную высоту высотой заголовка
        return Math.max(height, headerHeight + TooltipConfig.PADDING * 2);
    }

    private int getTooltipContentHeight(List<String> tooltip) {
        int height = 0;
        for (String line : tooltip) {
            if (line != null && !line.trim().isEmpty()) {
                height += 10; // Высота одной строки
            }
        }
        return height;
    }

    public void drawItemStack(ItemStack stack, int x, int y) {
        if (stack == null) return;

        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(TooltipConfig.ITEM_SCALE, TooltipConfig.ITEM_SCALE, 1.0f);
        GL11.glTranslatef(-x, -y, 0);

        boolean depthEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glTranslatef(0, 0, 300);

        itemRenderer.renderItemAndEffectIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft().getTextureManager(),
            stack,
            x,
            y
        );

        itemRenderer.renderItemOverlayIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft().getTextureManager(),
            stack,
            x,
            y
        );

        RenderHelper.disableStandardItemLighting();

        if (depthEnabled) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        GL11.glPopMatrix();
    }
}
