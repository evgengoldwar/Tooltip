package ToolTip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TooltipRenderHelper {

    private static void bind(ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    }

    public void drawRect(int left, int top, int right, int bottom, int color) {
        int t;
        if (left < right) { t = left; left = right; right = t; }
        if (top < bottom) { t = top; top = bottom; bottom = t; }

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;


        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(left, bottom, 0.0D);
        tessellator.addVertex(right, bottom, 0.0D);
        tessellator.addVertex(right, top, 0.0D);
        tessellator.addVertex(left, top, 0.0D);
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawBorder(int left, int top, int right, int bottom, int color, int thickness) {
        drawRect(left, top, right, top + thickness, color);
        drawRect(left, bottom - thickness, right, bottom, color);
        drawRect(left, top, left + thickness, bottom, color);
        drawRect(right - thickness, top, right, bottom, color);
    }

    public void drawTexturedBorder(int x, int y, int width, int height, ResourceLocation texture) {

        Tessellator tessellator = Tessellator.instance;
        int texWidth = 64;
        int texHeight = 64;
        if (texture == null) {
            texture = TooltipConfig.BORDER_TEXTURE;
        }
        bind(texture);



        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


        // Top Left Angle
        renderQuadTexture(tessellator, x - 1, y - 1, 5, 5, 17, 17, 22, 22, texWidth, texHeight);
        // Top Right Angle
        renderQuadTexture(tessellator, x + width - (5 - 1), y - 1, 5, 5, 42, 17, 47, 22, texWidth, texHeight);
        // Bottom Left Angle
        renderQuadTexture(tessellator, x - 1, y + height - (5 - 1), 5, 5, 17, 42, 22, 47, texWidth, texHeight);
        // Bottom Right Angle
        renderQuadTexture(tessellator, x + width - (5 - 1), y + height - (5 - 1), 5, 5, 42, 42, 47, 47, texWidth, texHeight);


        // Left Border
        renderQuadTexture(tessellator, x - 1, y - 1 + 5, 1, height - 2 * (5 - 1), 17, 22, 18, 42, texWidth, texHeight);
        // Right Border
        renderQuadTexture(tessellator, x + width, y - 1 + 5, 1, height - 2 * (5 - 1), 46, 22, 47, 42, texWidth, texHeight);


        // Top Left Line
        renderQuadTexture(tessellator, x + (5 - 1), y - 1, (x - 1 + (width / 2) - (3 * 32) / 8) - (x + (5 - 1) + 1), 1, 22, 17, 27, 18, texWidth, texHeight);
        // Top Right Line
        renderQuadTexture(
            tessellator,
            x + (5 - 1) + (width / 2) + (3 * 32) / 8,
            y - 1,
            (x - 1 + (width / 2) - (3 * 32) / 8) - (x + (5 - 1) + 1),
            1,
            37,
            17,
            42,
            18,
            texWidth,
            texHeight);

        // Top Middle Left Angle
        renderQuadTexture(tessellator, x - 2 + (width / 2) - (3 * 32) / 8, y, 2, 1, 27, 18, 29, 19, texWidth, texHeight);
        // Top Middle Right Angle
        renderQuadTexture(tessellator, x + 2 + (width / 2) + (3 * 32) / 8, y, 2, 1, 35, 18, 37, 19, texWidth, texHeight);


        // Bottom Left Line
        renderQuadTexture(tessellator, x + (5 - 1), y + height, ((width - (5 - 1) - 14) / 2) - 2, 1, 22, 46, 25, 47, texWidth, texHeight);
        // Bottom Right Line
        renderQuadTexture(tessellator, x + (double) (width + 14) / 2 - 1, y + height, ((width - (5 - 1) - (double) 14) / 2), 1, 39, 46, 42, 47, texWidth, texHeight);
        // Bottom ornament
        renderQuadTexture(tessellator, x + (double) (width - 14) / 2 - 1, y + height - 2, 14, 6, 25, 44, 39, 50, texWidth, texHeight);


//        GL11.glDisable(GL11.GL_BLEND);
    }


    public void drawTexturedSeparator(int x, int y, int width, ResourceLocation texture) {
        if (texture == null) texture = TooltipConfig.SEPARATOR_TEXTURE;

        Tessellator tessellator = Tessellator.instance;

        bind(texture);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        int h = 7;
        int sliceSide = 3;
        int centerUW = 26;
        int texW = 32;
        int texH = 16;
        int v = 4;
        int innerW = Math.max(0, width - 2 * sliceSide);
        y -= 4;

        renderQuad(tessellator, x, y, sliceSide, h, 0, v, sliceSide, v + h, texW, texH);

        renderQuad(tessellator, x + sliceSide, y, innerW, h, sliceSide, v, sliceSide + centerUW, v + h, texW, texH);

        renderQuad(tessellator, x + width - sliceSide, y, sliceSide, h, texW - sliceSide, v, texW, v + h, texW, texH);
    }

    public void drawSeparator(int x, int y, int width) {
        drawRect(x, y, x + width, y + TooltipConfig.SEPARATOR_THICKNESS, TooltipConfig.SEPARATOR_COLOR);
    }

    public void drawTopIconCentered(int x, int y, int width, ResourceLocation texture) {
        if (texture == null) texture = TooltipConfig.TOP_ICON_TEXTURE;

        Tessellator tessellator = Tessellator.instance;

        bind(texture);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        int textureWidth = 32;
        int textureHeight = 16;
        int paddingWidth = 2;
        int paddingHeight = 1;
        int ix = x + (width - textureWidth) / 2 + paddingWidth;
        int iy = y - textureHeight + paddingHeight;
        renderQuad(tessellator, ix, iy, textureWidth, textureHeight, 0, 0, 32, 16, textureWidth, textureHeight);
    }

    private static void renderQuad(Tessellator tessellator, double x, double y, double width, double height,
                                   double uStart, double vStart, double uEnd, double vEnd, double texWidth, double texHeight) {
        double uMin = uStart / texWidth;
        double vMin = vStart / texHeight;
        double uMax = uEnd / texWidth;
        double vMax = vEnd / texHeight;

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0, uMin, vMax);
        tessellator.addVertexWithUV(x + width, y + height, 0, uMax, vMax);
        tessellator.addVertexWithUV(x + width, y, 0, uMax, vMin);
        tessellator.addVertexWithUV(x, y, 0, uMin, vMin);
        tessellator.draw();
    }

    private static void renderQuadTexture(Tessellator tessellator, double x, double y, double width, double height,
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


}
