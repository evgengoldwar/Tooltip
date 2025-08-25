package ToolTip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TooltipRenderHelper {

    public void drawRect(int left, int top, int right, int bottom, int color) {
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

    public void drawBorder(int left, int top, int right, int bottom, int color, int thickness) {
        if (thickness <= 0) return;

        drawRect(left, top, right, top + thickness, color);
        drawRect(right - thickness, top, right, bottom, color);
        drawRect(left, bottom - thickness, right, bottom, color);
        drawRect(left, top, left + thickness, bottom, color);
    }

    public void drawSeparator(int x, int y, int width) {
        drawRect(x, y, x + width, y + TooltipConfig.SEPARATOR_THICKNESS, TooltipConfig.SEPARATOR_COLOR);
    }

    public void drawTexturedTooltipBorder(int x, int y, int width, int height, ResourceLocation resourceLocation) {
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

    public void drawTexturedSeparator(int x, int y, int width, ResourceLocation resourceLocation) {
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
}
