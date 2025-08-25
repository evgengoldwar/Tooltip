package ToolTip;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TooltipRenderer {

    private String oreDictName = "";
    private String modName = "";
    private String displayName = "";
    private boolean advancedSettings = false;
    private ItemStack itemStack = null;
    private final TooltipPositionCalculator positionCalculator = new TooltipPositionCalculator();
    private final TooltipRenderHelper renderHelper = new TooltipRenderHelper();
    private final PaginationHelper paginationHelper = new PaginationHelper();
    private static final RenderItem itemRenderer = new RenderItem();

    public void setAdditionalInfo(String oredict, String mod, String displayName, Boolean advancedSettings) {
        this.oreDictName = oredict != null ? oredict : "";
        this.modName = mod != null ? mod : "";
        this.displayName = displayName != null ? displayName : "";
        this.advancedSettings = advancedSettings;
    }

    public void renderCustomTooltip(List<String> tooltip, FontRenderer font, int x, int y, int width, int height,
        ItemStack stack, ResourceLocation resourceLocation) {
        // Variables
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenHeight = res.getScaledHeight();
        int maxAvailableHeight = screenHeight - y - 10;
        List<List<String>> pages = paginationHelper.splitTooltipByPages(tooltip, maxAvailableHeight);

        // Clear Pagination
        if (tooltip == null || stack == null) {
            paginationHelper.setTooltipActive(false);
            paginationHelper.resetPagination();
            return;
        }
        paginationHelper.setTooltipActive(true);

        // Set default textures when ModName or Tier not found
        if (resourceLocation == null) resourceLocation = TooltipConfig.BACKGROUND_TEXTURE;

        // Clear Pagination pages
        if (itemStack != stack) {
            paginationHelper.resetPagination();
            itemStack = stack;
        }

        // Pagination settings and Variables
        paginationHelper.setMaxTooltipPage(pages.size());
        List<String> currentPage = pages.isEmpty() ? new ArrayList<>() : pages.get(paginationHelper.getTooltipPage());
        if (paginationHelper.getMaxTooltipPage() > 1) {
            currentPage.add(
                EnumChatFormatting.GRAY + "Page "
                    + (paginationHelper.getTooltipPage() + 1)
                    + "/"
                    + paginationHelper.getMaxTooltipPage());
            currentPage.add(EnumChatFormatting.ITALIC + "Use Z to navigate");
        }

        int pageWidth = positionCalculator
            .calculateTooltipWidth(currentPage, font, displayName, oreDictName, modName, advancedSettings);
        int pageHeight = positionCalculator
            .calculateTooltipHeight(currentPage, font, displayName, oreDictName, modName, advancedSettings);

        int[] position = positionCalculator.calculateSafePosition(x, y, pageWidth, pageHeight);
        int finalX = position[0];
        int finalY = position[1];
        int itemX = finalX + TooltipConfig.PADDING;
        int itemY = finalY + TooltipConfig.PADDING;
        int textX = itemX + TooltipConfig.ITEM_SIZE + TooltipConfig.TEXT_MARGIN;
        int textY = itemY;
        boolean hasTooltipContent = positionCalculator.hasActualTooltipContent(currentPage);
        int headerHeight = positionCalculator
            .getHeaderHeight(font, displayName, oreDictName, modName, advancedSettings);
        int separatorY = finalY + TooltipConfig.PADDING + headerHeight + TooltipConfig.SEPARATOR_MARGIN;
        int tooltipStartY = separatorY + TooltipConfig.SEPARATOR_THICKNESS + TooltipConfig.SEPARATOR_MARGIN;

        // OpenGL render
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glTranslatef(0, 0, 500);

        renderHelper.drawRect(finalX, finalY, finalX + pageWidth, finalY + pageHeight, TooltipConfig.BACKGROUND_COLOR);

        // Check Ugly or Fancy renderer
        if (TooltipConfig.USE_TEXTURE_BORDER) {
            renderHelper.drawTexturedTooltipBorder(finalX, finalY, pageWidth, pageHeight, resourceLocation);
        } else {
            renderHelper.drawBorder(
                finalX,
                finalY,
                finalX + pageWidth,
                finalY + pageHeight,
                TooltipConfig.BORDER_COLOR,
                TooltipConfig.BORDER_THICKNESS);
        }

        // Render
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glTranslatef(0, 0, 100);
        drawItemStack(stack, itemX, itemY);
        GL11.glPopMatrix();
        renderItemInfo(font, textX, textY);

        // Render Tooltip
        if (hasTooltipContent) {
            if (TooltipConfig.USE_TEXTURE_BORDER) {
                renderHelper.drawTexturedSeparator(
                    finalX + TooltipConfig.PADDING,
                    separatorY,
                    pageWidth - TooltipConfig.PADDING * 2,
                    resourceLocation);
            } else {
                renderHelper
                    .drawSeparator(finalX + TooltipConfig.PADDING, separatorY, pageWidth - TooltipConfig.PADDING * 2);
            }
            renderTooltipContent(currentPage, font, finalX + TooltipConfig.PADDING, tooltipStartY);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    public void renderItemInfo(FontRenderer font, int x, int y) {
        int currentY = y;

        if (!displayName.isEmpty()) {
            font.drawStringWithShadow(TooltipConfig.NAME_COLOR + displayName, x, currentY, 0xFFFFFF);
            currentY += 10;
        }

        if (!oreDictName.isEmpty() && advancedSettings) {
            font.drawStringWithShadow(TooltipConfig.OREDICT_COLOR + oreDictName, x, currentY, 0xFFFFFF);
            currentY += 10;
        }

        if (!modName.isEmpty()) {
            font.drawStringWithShadow(TooltipConfig.MODNAME_COLOR + modName, x, currentY, 0xFFFFFF);
        }
    }

    public void renderTooltipContent(List<String> tooltip, FontRenderer font, int x, int y) {
        int currentY = y;
        for (String line : tooltip) {
            if (line != null && !line.trim()
                .isEmpty()) {
                font.drawStringWithShadow(TooltipConfig.TOOLTIP_COLOR + line, x, currentY, 0xFFFFFF);
                currentY += 10;
            }
        }
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
}
