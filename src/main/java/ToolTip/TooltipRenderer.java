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

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TooltipRenderer {

    private String oredictName = "";
    private String modName = "";
    private String displayName = "";

    private boolean advancedSettings = false;
    private static final RenderItem itemRenderer = new RenderItem();
    private final TooltipPositionCalculator positionCalculator = new TooltipPositionCalculator();

    // Пагинация
    private static int tooltipPage = 0;
    private static int maxTooltipPage = 1;
    private static boolean isTooltipActive = false;

    private static ItemStack lastItemStack = null;

    public void setAdditionalInfo(String oredict, String mod, String displayName) {
        this.oredictName = oredict;
        this.modName = mod;
        this.displayName = displayName;
    }

    public void renderCustomTooltip(List<String> tooltip, FontRenderer font, int x, int y, int width, int height,
        ItemStack stack) {
        if (tooltip == null) {
            setTooltipActive(false);
            resetPagination();
            return;
        }
        setTooltipActive(true);

        if (lastItemStack != stack) {
            resetPagination();
            lastItemStack = stack;
        }
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        checkAdvancedSettings();

        // Рассчитываем максимальную доступную высоту
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenHeight = res.getScaledHeight();
        int maxAvailableHeight = screenHeight - y - 10;

        // Разделяем тултип на страницы
        List<List<String>> pages = splitTooltipByPages(tooltip, maxAvailableHeight);
        setMaxTooltipPage(pages.size());

        List<String> currentPage = pages.isEmpty() ? new ArrayList<String>() : pages.get(tooltipPage);

        // Добавляем информацию о странице если нужно
        if (maxTooltipPage > 1) {
            currentPage.add(EnumChatFormatting.GRAY + "Page " + (tooltipPage + 1) + "/" + maxTooltipPage);
            currentPage.add(EnumChatFormatting.ITALIC + "Use arrows to navigate");
        }

        // Пересчитываем размеры для текущей страницы
        int pageWidth = calculateTooltipWidth(currentPage, font);
        int pageHeight = calculateTooltipHeight(currentPage, font);

        // Обновляем позицию
        int[] position = positionCalculator.calculateSafePosition(x, y, pageWidth, pageHeight);
        int finalX = position[0];
        int finalY = position[1];

        // Рендер фона
        drawRect(finalX, finalY, finalX + pageWidth, finalY + pageHeight, TooltipConfig.BACKGROUND_COLOR);

        // Рендер границ
        drawBorder(
            finalX,
            finalY,
            finalX + pageWidth,
            finalY + pageHeight,
            TooltipConfig.BORDER_COLOR,
            TooltipConfig.BORDER_THICKNESS);

        // Рендер предмета
        int itemX = finalX + TooltipConfig.PADDING;
        int itemY = finalY + TooltipConfig.PADDING;
        drawItemStack(stack, itemX, itemY);

        // Рендер информации о предмете
        int textX = itemX + TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN;
        int textY = itemY;
        renderItemInfo(font, textX, textY);

        // Рендер контента тултипа
        boolean hasTooltipContent = hasActualTooltipContent(currentPage);
        if (hasTooltipContent) {
            int headerHeight = getHeaderHeight(font);
            int separatorY = finalY + TooltipConfig.PADDING + headerHeight + TooltipConfig.SEPARATOR_MARGIN;
            drawSeparator(finalX + TooltipConfig.PADDING, separatorY, pageWidth - TooltipConfig.PADDING * 2);

            int tooltipStartY = separatorY + TooltipConfig.SEPARATOR_THICKNESS + TooltipConfig.SEPARATOR_MARGIN;
            renderTooltipContent(currentPage, font, finalX + TooltipConfig.PADDING, tooltipStartY);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private static boolean lastN = false;
    private static boolean lastP = false;

    private void checkInputInRenderer() {
        try {
            boolean nNow = Keyboard.isKeyDown(Keyboard.KEY_N);
            boolean pNow = Keyboard.isKeyDown(Keyboard.KEY_P);

            if (nNow && !lastN) {
                System.out.println("[RENDER] N pressed in renderer!");
                nextPage();
            }
            lastN = nNow;

            if (pNow && !lastP) {
                System.out.println("[RENDER] P pressed in renderer!");
                previousPage();
            }
            lastP = pNow;

        } catch (Exception e) {
            System.out.println("[RENDER] Input check error: " + e.getMessage());
        }
    }

    private List<List<String>> splitTooltipByPages(List<String> tooltip, int maxHeight) {
        List<List<String>> pages = new ArrayList<>();
        if (tooltip == null || tooltip.isEmpty()) return pages;

        List<String> currentPage = new ArrayList<>();
        int currentHeight = 0;
        int headerHeight = 40; // Примерная высота заголовка

        for (String line : tooltip) {
            if (line != null && !line.trim()
                .isEmpty()) {
                int lineHeight = 10;

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

    private void checkAdvancedSettings() {
        advancedSettings = Minecraft.getMinecraft().gameSettings.advancedItemTooltips;
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
            if (line != null && !line.trim()
                .isEmpty()) {
                font.drawStringWithShadow(TooltipConfig.TOOLTIP_COLOR + line, x, currentY, 0xFFFFFF);
                currentY += 10;
            }
        }
    }

    private int getHeaderHeight(FontRenderer font) {
        int height = 10; // displayName
        if (!oredictName.isEmpty() && advancedSettings) height += 10;
        if (!modName.isEmpty()) height += 10;
        return Math.max(height, TooltipConfig.ITEM_SIZE);
    }

    public int calculateTooltipWidth(List<String> tooltip, FontRenderer font) {
        int maxWidth = 0;

        // Ширина заголовка
        int headerWidth = TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN;
        headerWidth += font.getStringWidth(displayName);

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

        // Ширина контента тултипа
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

        // Высота заголовка
        int headerHeight = getHeaderHeight(font);
        height += headerHeight;

        // Высота контента тултипа
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
        GL11.glTranslatef(0, 0, 300);

        itemRenderer.renderItemAndEffectIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            stack,
            x,
            y);

        itemRenderer.renderItemOverlayIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            stack,
            x,
            y);

        RenderHelper.disableStandardItemLighting();

        if (depthEnabled) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        GL11.glPopMatrix();
    }

    // Методы пагинации
    public static void nextPage() {
        if (isTooltipActive && maxTooltipPage > 1) {
            tooltipPage = (tooltipPage + 1) % maxTooltipPage;
            System.out.println("Next page: " + (tooltipPage + 1) + "/" + maxTooltipPage);
            Minecraft.getMinecraft().currentScreen = Minecraft.getMinecraft().currentScreen;
        }
    }

    public static void previousPage() {
        if (isTooltipActive && maxTooltipPage > 1) {
            tooltipPage = (tooltipPage - 1 + maxTooltipPage) % maxTooltipPage;
            System.out.println("Previous page: " + (tooltipPage + 1) + "/" + maxTooltipPage);
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
    }
}
