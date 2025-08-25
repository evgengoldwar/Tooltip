package ToolTip;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.GuiContainerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public class TooltipRenderer {

    public void renderCustomTooltip(List<String> tooltip, FontRenderer font, int x, int y, int width, int height, ItemStack stack) {
        if (tooltip.isEmpty()) return;

        GL11.glPushMatrix();
        setupGL();

        // Рендер фона
        drawRect(x - 3, y - 3, x + width + 3, y + height + 3, TooltipConfig.BACKGROUND_COLOR);

        // Рендер границы
        drawBorder(x - 4, y - 4, x + width + 4, y + height + 4,
            TooltipConfig.BORDER_TOP_COLOR, TooltipConfig.BORDER_RIGHT_COLOR,
            TooltipConfig.BORDER_BOTTOM_COLOR, TooltipConfig.BORDER_LEFT_COLOR,
            TooltipConfig.BORDER_TOP_THICKNESS, TooltipConfig.BORDER_RIGHT_THICKNESS,
            TooltipConfig.BORDER_BOTTOM_THICKNESS, TooltipConfig.BORDER_LEFT_THICKNESS);

        // Рендер разделительной линии (простая линия от начала до конца)
        if (tooltip.size() > 1) {
            drawRect(x + 2 , y + 10, x + width - 2, y + 10 + TooltipConfig.BORDER_BOTTOM_THICKNESS, TooltipConfig.BORDER_BOTTOM_COLOR);
        }

        drawItemStack(stack, x, y);

        // Рендер текста
        renderTooltipText(tooltip, font, x, y);

        restoreGL();
        GL11.glPopMatrix();
    }

    private void setupGL() {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void restoreGL() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
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

    private void drawBorder(int left, int top, int right, int bottom,
                            int topColor, int rightColor, int bottomColor, int leftColor,
                            int topThickness, int rightThickness, int bottomThickness, int leftThickness) {

        // Верхняя граница
        if (topThickness > 0) {
            drawRect(left, top, right, top + topThickness, topColor);
        }

        // Правая граница
        if (rightThickness > 0) {
            drawRect(right - rightThickness, top, right, bottom, rightColor);
        }

        // Нижняя граница
        if (bottomThickness > 0) {
            drawRect(left, bottom - bottomThickness, right, bottom, bottomColor);
        }

        // Левая граница
        if (leftThickness > 0) {
            drawRect(left, top, left + leftThickness, bottom, leftColor);
        }
    }

    private void drawSimpleLine(int startX, int y, int endX, int endY, int thickness, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.instance;
        int halfThickness = thickness / 2;

        // Простая прямоугольная линия от начала до конца
        tessellator.startDrawingQuads();
        tessellator.addVertex(startX, y - halfThickness, 0);
        tessellator.addVertex(endX, y - halfThickness, 0);
        tessellator.addVertex(endX, y + halfThickness, 0);
        tessellator.addVertex(startX, y + halfThickness, 0);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderTooltipText(List<String> tooltip, FontRenderer font, int x, int y) {
        if (!tooltip.isEmpty()) {
            // Рендер названия предмета
            String itemName = tooltip.get(0);
            font.drawStringWithShadow(TooltipConfig.NAME_COLOR + itemName, x, y, 0xFFFFFF);

            // Рендер остального текста (с отступом от линии)
            for (int i = 1; i < tooltip.size(); i++) {
                String line = tooltip.get(i);
                font.drawStringWithShadow(TooltipConfig.TEXT_COLOR + line, x, y + 15 + (i - 1) * 10, 0xFFFFFF);
            }
        }
    }

    public int calculateTooltipWidth(List<String> tooltip, FontRenderer font) {
        int width = 0;
        for (String line : tooltip) {
            width = Math.max(width, font.getStringWidth(line));
        }
        return Math.max(width + 8, 100);
    }

    public int calculateTooltipHeight(List<String> tooltip) {
        return Math.max(tooltip.size() * 10 + 15, 35);
    }

    public void drawItemStack(ItemStack stack, int x, int y) {

        NEIClientUtils.gl2DRenderContext(() -> {
            GuiDraw.drawRect(x, y, 34, 34, 0x66555555);

            GuiDraw.drawString(stack.getDisplayName(), x + 36, y, 0xffffffff);

//            GuiDraw.drawString(this.identifier, x + 36, y + 9 * 1, 0xee555555);

            if (stack.stackSize != Integer.MAX_VALUE) {
//                GuiDraw.drawString(stack.itemCount, x + 36, y + 9 * 2, 0xee555555);
            }

//            GuiDraw.drawString(this.modName, x + 36, y + 9 * 3, 0xff5555ff);
        });

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        GL11.glScaled(2, 2, 3);
        GL11.glTranslatef((x + 1) / 2f, (y + 1) / 2f, 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GuiContainerManager.drawItem(0, 0, stack, true, "");

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
