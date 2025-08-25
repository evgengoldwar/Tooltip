package ToolTip;

import net.minecraft.util.ResourceLocation;

public enum ModTextures {

    GT_MAX_TIER("2,147,483,640", "textures/gui/tooltip_max_tier.png"),
    GT_UXV_TIER("32_127", "textures/gui/tooltip_uxv_tier.png");

    private final String modName;
    private final ResourceLocation texture;

    ModTextures(String modName, String texturePath) {
        this.modName = modName;
        this.texture = new ResourceLocation("tooltipimprove", texturePath);
    }

    public String getModName() {
        return modName;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public static ResourceLocation getTextureForMod(String modName) {
        for (ModTextures mod : values()) {
            if (mod.getModName()
                .equalsIgnoreCase(modName)) {
                return mod.getTexture();
            }
        }
        return null;
    }
}
