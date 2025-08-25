package ToolTip;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent event) {
        event.alternativeRenderer = tooltip -> {
            if (tooltip.isEmpty()) return;

            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("wdawd");
            tooltip.add("AWDAWDAWDAWD");

            String displayName = event.itemStack.getDisplayName();
            String oredict = getIdentifier(event.itemStack);
            String modName = nameFromStack(event.itemStack.getItem());

            renderer.setAdditionalInfo(oredict, modName, displayName);

            List<String> filteredTooltip = new ArrayList<String>();
            boolean isFirstLine = true;

            for (String line : tooltip) {
                if (!isFirstLine) {
                    filteredTooltip.add(line);
                }
                isFirstLine = false;
            }

            int width = renderer.calculateTooltipWidth(filteredTooltip, event.font);
            int height = renderer.calculateTooltipHeight(filteredTooltip, event.font);

            int mouseX = event.x;
            int mouseY = event.y;

            renderer.renderCustomTooltip(filteredTooltip, event.font, mouseX, mouseY, width, height, event.itemStack);
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
            return "";
        }
    }

    protected static String getModId(Item item) {

        if (!ItemInfo.itemOwners.containsKey(item)) {
            try {
                GameRegistry.UniqueIdentifier ident = GameRegistry.findUniqueIdentifierFor(item);
                ItemInfo.itemOwners.put(item, ident.modId);
            } catch (Exception ignored) {
                NEIClientConfig.logger.error("Failed to find identifier for: " + item);
                ItemInfo.itemOwners.put(item, "Unknown");
            }
        }

        return ItemInfo.itemOwners.get(item);
    }
}
