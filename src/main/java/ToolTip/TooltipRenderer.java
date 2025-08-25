package ToolTip;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import codechicken.lib.gui.GuiDraw;

public class TooltipRenderer {

    private String oredictName = "";
    private String modName = "";
    private String displayName = "";
    private boolean advancedSettings = false;
    private static final RenderItem itemRenderer = new RenderItem();
    private final TooltipPositionCalculator positionCalculator = new TooltipPositionCalculator();
    private static int tooltipPage = 0;
    private static int maxTooltipPage = 1;
    private static boolean isTooltipActive = false;
    private static ItemStack lastItemStack = null;

    public void setAdditionalInfo(String oredict, String mod, String displayName) {
        this.oredictName = oredict != null ? oredict : "";
        this.modName = mod != null ? mod : "";
        this.displayName = displayName != null ? displayName : "";
    }

    public void renderCustomTooltip(List<String> tooltip, FontRenderer font, int x, int y, int width, int height,
        ItemStack stack, ResourceLocation resourceLocation) {
        if (tooltip == null || stack == null) {
            setTooltipActive(false);
            resetPagination();
            return;
        }
        setTooltipActive(true);

        if (resourceLocation == null) resourceLocation = TooltipConfig.BACKGROUND_TEXTURE;

        if (lastItemStack != stack) {
            resetPagination();
            lastItemStack = stack;
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glTranslatef(0, 0, 500);

        advancedSettings = Minecraft.getMinecraft().gameSettings.advancedItemTooltips;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenHeight = res.getScaledHeight();
        int maxAvailableHeight = screenHeight - y - 10;

        List<List<String>> pages = splitTooltipByPages(tooltip, maxAvailableHeight);
        setMaxTooltipPage(pages.size());

        List<String> currentPage = pages.isEmpty() ? new ArrayList<String>() : pages.get(tooltipPage);

        if (maxTooltipPage > 1) {
            currentPage.add(EnumChatFormatting.GRAY + "Page " + (tooltipPage + 1) + "/" + maxTooltipPage);
            currentPage.add(EnumChatFormatting.ITALIC + "Use Z to navigate");
        }

        int pageWidth = calculateTooltipWidth(currentPage, font);
        int pageHeight = calculateTooltipHeight(currentPage, font);

        int[] position = positionCalculator.calculateSafePosition(x, y, pageWidth, pageHeight);
        int finalX = position[0];
        int finalY = position[1];

        drawRect(finalX, finalY, finalX + pageWidth, finalY + pageHeight, TooltipConfig.BACKGROUND_COLOR);

        if (TooltipConfig.USE_TEXTURE_BORDER) {
            drawTexturedTooltipBorder(finalX, finalY, pageWidth, pageHeight, resourceLocation);
        } else {
            drawBorder(
                finalX,
                finalY,
                finalX + pageWidth,
                finalY + pageHeight,
                TooltipConfig.BORDER_COLOR,
                TooltipConfig.BORDER_THICKNESS);
        }

        int itemX = finalX + TooltipConfig.PADDING;
        int itemY = finalY + TooltipConfig.PADDING;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glTranslatef(0, 0, 100);
        drawItemStack(stack, itemX, itemY);
        GL11.glPopMatrix();

        int textX = itemX + TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN;
        int textY = itemY;
        renderItemInfo(font, textX, textY);

        boolean hasTooltipContent = hasActualTooltipContent(currentPage);
        if (hasTooltipContent) {
            int headerHeight = getHeaderHeight(font);
            int separatorY = finalY + TooltipConfig.PADDING + headerHeight + TooltipConfig.SEPARATOR_MARGIN;

            if (TooltipConfig.USE_TEXTURE_BORDER) {
                drawTexturedSeparator(
                    finalX + TooltipConfig.PADDING,
                    separatorY,
                    pageWidth - TooltipConfig.PADDING * 2,
                    resourceLocation);
            } else {
                drawSeparator(finalX + TooltipConfig.PADDING, separatorY, pageWidth - TooltipConfig.PADDING * 2);
            }

            int tooltipStartY = separatorY + TooltipConfig.SEPARATOR_THICKNESS + TooltipConfig.SEPARATOR_MARGIN;
            renderTooltipContent(currentPage, font, finalX + TooltipConfig.PADDING, tooltipStartY);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private List<List<String>> splitTooltipByPages(List<String> tooltip, int maxHeight) {
        List<List<String>> pages = new ArrayList<>();
        if (tooltip == null || tooltip.isEmpty()) return pages;

        List<String> currentPage = new ArrayList<>();
        int currentHeight = 0;
        int headerHeight = 40;
        int lineHeight = 10;

        for (String line : tooltip) {
            if (line != null && !line.trim()
                .isEmpty()) {
                if (currentHeight + lineHeight > maxHeight - headerHeight && !currentPage.isEmpty()) {
                    pages.add(currentPage);
                    currentPage = new ArrayList<>();
                    currentHeight = 0;
                }
                currentPage.add(line);
                currentHeight += lineHeight;
            }
        }

        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }

        return pages;
    }

    private boolean hasActualTooltipContent(List<String> tooltip) {
        if (tooltip == null || tooltip.isEmpty()) return false;
        for (String line : tooltip) {
            if (line != null && !line.trim()
                .isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void drawRect(int left, int top, int right, int bottom, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

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

        if (!displayName.isEmpty()) {
            font.drawStringWithShadow(TooltipConfig.NAME_COLOR + displayName, x, currentY, 0xFFFFFF);
            currentY += 10;
        }

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
            if (line != null && !line.trim()
                .isEmpty()) {
                font.drawStringWithShadow(TooltipConfig.TOOLTIP_COLOR + line, x, currentY, 0xFFFFFF);
                currentY += 10;
            }
        }
    }

    private int getHeaderHeight(FontRenderer font) {
        int height = 0;
        if (!displayName.isEmpty()) height += 10;
        if (!oredictName.isEmpty() && advancedSettings) height += 10;
        if (!modName.isEmpty()) height += 10;
        return Math.max(height, TooltipConfig.ITEM_SIZE);
    }

    public int calculateTooltipWidth(List<String> tooltip, FontRenderer font) {
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
                if (line != null && !line.trim()
                    .isEmpty()) {
                    maxWidth = Math.max(maxWidth, font.getStringWidth(line));
                }
            }
        }

        return maxWidth + TooltipConfig.PADDING * 2;
    }

    public int calculateTooltipHeight(List<String> tooltip, FontRenderer font) {
        int height = TooltipConfig.PADDING * 2;
        int headerHeight = getHeaderHeight(font);
        height += headerHeight;

        boolean hasTooltipContent = hasActualTooltipContent(tooltip);
        if (hasTooltipContent) {
            height += TooltipConfig.SEPARATOR_THICKNESS + TooltipConfig.SEPARATOR_MARGIN * 2;
            height += getTooltipContentHeight(tooltip);
        }

        return height;
    }

    private int getTooltipContentHeight(List<String> tooltip) {
        int height = 0;
        if (tooltip != null) {
            for (String line : tooltip) {
                if (line != null && !line.trim()
                    .isEmpty()) {
                    height += 10;
                }
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

        Minecraft mc = Minecraft.getMinecraft();
        itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);

        RenderHelper.disableStandardItemLighting();

        if (depthEnabled) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }


        GL11.glPopMatrix();
    }

    private void drawTexturedTooltipBorder(int x, int y, int width, int height, ResourceLocation resourceLocation) {
        if (TooltipConfig.BACKGROUND_TEXTURE == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager()
            .bindTexture(resourceLocation);

        float texWidth = 64.0f;
        float texHeight = 64.0f;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.instance;

        renderTexturedQuad(tessellator, x - 1, y - 1, 3, 3, 16, 16, 19, 19, texWidth, texHeight);
        renderTexturedQuad(tessellator, x + width - 2, y - 1, 3, 3, 43, 16, 46, 19, texWidth, texHeight);
        renderTexturedQuad(tessellator, x - 1, y + height - 2, 3, 3, 16, 43, 19, 46, texWidth, texHeight);
        renderTexturedQuad(tessellator, x + width - 2, y + height - 2, 3, 3, 43, 43, 46, 46, texWidth, texHeight);

        renderTexturedQuad(tessellator, x - 1, y + 2, 1, height - 3, 16, 19, 17, 43, texWidth, texHeight);
        renderTexturedQuad(tessellator, x + width, y + 2, 1, height - 3, 45, 19, 46, 43, texWidth, texHeight);

        renderTexturedQuad(tessellator, x + 2, y - 1, (width / 2) - 8.5, 1, 19, 16, 26, 17, texWidth, texHeight);
        renderTexturedQuad(
            tessellator,
            x + (width / 2) + 8.5,
            y - 1,
            (width / 2) - 10.5,
            1,
            36,
            16,
            43,
            17,
            texWidth,
            texHeight);

        renderTexturedQuad(tessellator, x + 2, y + height, width - 3, 1, 18, 45, 43, 46, texWidth, texHeight);

        renderTexturedQuad(tessellator, x + (width / 2) - 6.5, y, 2, 1, 26, 17, 28, 18, texWidth, texHeight);
        renderTexturedQuad(tessellator, x + (width / 2) + 6.5, y, 2, 1, 34, 17, 36, 18, texWidth, texHeight);
        renderTexturedQuad(tessellator, x + (width / 2) - 6.5, y - 11, 15, 13, 1, 1, 16, 14, texWidth, texHeight);

        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderTexturedQuad(Tessellator tessellator, double x, double y, double width, double height,
        double uStart, double vStart, double uEnd, double vEnd, double texWidth, double texHeight) {
        double uMin = uStart / texWidth;
        double vMin = vStart / texHeight;
        double uMax = uEnd / texWidth;
        double vMax = vEnd / texHeight;

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y, 0, uMin, vMin);
        tessellator.addVertexWithUV(x, y + height, 0, uMin, vMax);
        tessellator.addVertexWithUV(x + width, y + height, 0, uMax, vMax);
        tessellator.addVertexWithUV(x + width, y, 0, uMax, vMin);
        tessellator.draw();
    }

    private void drawTexturedSeparator(int x, int y, int width, ResourceLocation resourceLocation) {
        if (TooltipConfig.BACKGROUND_TEXTURE == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager()
            .bindTexture(resourceLocation);

        float texWidth = 64.0f;
        float texHeight = 64.0f;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.instance;
        renderTexturedQuad(tessellator, x, y, width, 1, 18, 7, 44, 8, texWidth, texHeight);

        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void nextPage() {
        if (isTooltipActive && maxTooltipPage > 1) {
            tooltipPage = (tooltipPage + 1) % maxTooltipPage;
        }
    }

    public static void previousPage() {
        if (isTooltipActive && maxTooltipPage > 1) {
            tooltipPage = (tooltipPage - 1 + maxTooltipPage) % maxTooltipPage;
        }
    }

    public static void setMaxTooltipPage(int maxPages) {
        maxTooltipPage = Math.max(1, maxPages);
        if (tooltipPage >= maxTooltipPage) {
            tooltipPage = 0;
        }
    }

    public static void setTooltipActive(boolean active) {
        isTooltipActive = active;
        if (!active) {
            resetPagination();
        }
    }

    public static boolean isTooltipActive() {
        return isTooltipActive;
    }

    public static void resetPagination() {
        tooltipPage = 0;
        maxTooltipPage = 1;
        isTooltipActive = false;
        lastItemStack = null;
    }
}
