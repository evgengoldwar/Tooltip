package ToolTip;

import net.minecraft.util.ResourceLocation;

public enum ModTextures {

    // 1) ModName 2) Path border 3) Path separator 4) Path icon
    TEST("test", getPath("test/border"), getPath("test/separator"), getPath("test/icon")),

    GT_MAX_TIER("2,147,483,640", "", "", ""),
    GT_UXV_TIER("32_127", "", "", ""),
    BLOODMAGIC("bloodmagic", getPath("bloodmagic/border"), getPath("bloodmagic/separator"), getPath("bloodmagic/icon"));

    private final String modName;
    private final ResourceLocation borderTexture;
    private final ResourceLocation separatorTexture;
    private final ResourceLocation iconTexture;

    ModTextures(String modName, String borderTexture, String separatorTexture, String iconTexture) {
        this.modName = modName;
        this.borderTexture = new ResourceLocation("tooltipimprove", borderTexture);
        this.separatorTexture = new ResourceLocation("tooltipimprove", separatorTexture);
        this.iconTexture = new ResourceLocation("tooltipimprove", iconTexture);
    }

    public String getModName() {
        return modName;
    }

    public ResourceLocation getBorderTextureTexture() {
        return borderTexture;
    }

    public ResourceLocation getSeparatorTexture() {
        return separatorTexture;
    }

    public ResourceLocation getIconTexture() {
        return iconTexture;
    }

    public static ModTextures getTextureForMod(String modName) {
        for (ModTextures mod : values()) {
            if (mod.getModName()
                .equalsIgnoreCase(modName)) {
                return mod;
            }
        }
        return null;
    }

    private static String getPath(String folderNameAndTextureName) {
        return "textures/gui/" + folderNameAndTextureName + ".png";
    }
}
