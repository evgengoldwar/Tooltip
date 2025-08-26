package ToolTip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.gtnewhorizon.gtnhlib.client.event.RenderTooltipEvent;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.ItemInfo;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

public class TooltipHandlerEvent {

    private final TooltipRenderer renderer = new TooltipRenderer();
    private final TooltipPositionCalculator tooltipPositionCalculator = new TooltipPositionCalculator();

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent event) {
        event.alternativeRenderer = tooltip -> {
            if (tooltip.isEmpty()) return;

            boolean advancedSettings = Minecraft.getMinecraft().gameSettings.advancedItemTooltips;

            // Variables
            String displayName = event.itemStack.getDisplayName();
            String oreDict = getIdentifier(event.itemStack);
            String modName = nameFromStack(event.itemStack.getItem());
            List<String> filteredTooltip = new ArrayList<>();
            boolean isFirstLine = true;

            // Setter for TooltipRenderer
            renderer.setAdditionalInfo(oreDict, modName, displayName, advancedSettings);

            // Delete duplicate Name Item
            for (String line : tooltip) {
                if (!isFirstLine) {
                    filteredTooltip.add(line);
                }
                isFirstLine = false;
            }

            // Tier or ModName check in the list
            Optional<String> modNameFromList = Utils.findGTNameFromList(filteredTooltip);

            // Delete duplicate ModName
            filteredTooltip = filteredTooltip.stream()
                .filter(str -> !str.contains(modName))
                .collect(Collectors.toList());

            int width = tooltipPositionCalculator
                .calculateTooltipWidth(filteredTooltip, event.font, displayName, oreDict, modName, advancedSettings);
            int height = tooltipPositionCalculator
                .calculateTooltipHeight(filteredTooltip, event.font, displayName, oreDict, modName, advancedSettings);

            // Get textures if the ModName or Tier was found
            ResourceLocation path = null;
            if (modNameFromList.isPresent()) path = ModTextures.getTextureForMod(modNameFromList.get());

            // Render tooltip
            renderer.renderCustomTooltip(
                filteredTooltip,
                event.font,
                event.x,
                event.y,
                width,
                height,
                event.itemStack,
                path);
        };
    }

    protected String getIdentifier(ItemStack stack) {
        String name = GameData.getItemRegistry()
            .getNameForObject(stack.getItem());
        return name == null || name.isEmpty() ? "Unknown:Unknown" : name;
    }

    protected static String nameFromStack(Item item) {
        try {
            ModContainer mod = Loader.instance()
                .getIndexedModList()
                .get(getModId(item));
            return mod == null ? "Minecraft" : mod.getName();
        } catch (NullPointerException e) {
            return "Unknown";
        }
    }

    protected static String getModId(Item item) {
        if (!ItemInfo.itemOwners.containsKey(item)) {
            try {
                GameRegistry.UniqueIdentifier ident = GameRegistry.findUniqueIdentifierFor(item);
                ItemInfo.itemOwners.put(item, ident != null ? ident.modId : "Unknown");
            } catch (Exception ignored) {
                NEIClientConfig.logger.error("Failed to find identifier for: " + item);
                ItemInfo.itemOwners.put(item, "Unknown");
            }
        }
        return ItemInfo.itemOwners.get(item);
    }
}
