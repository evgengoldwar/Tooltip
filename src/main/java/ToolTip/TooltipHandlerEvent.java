package ToolTip;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.ItemInfo;
import com.gtnewhorizon.gtnhlib.client.event.RenderTooltipEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;


public class TooltipHandlerEvent {

    private final TooltipRenderer renderer = new TooltipRenderer();
    private final TooltipPositionCalculator positionCalculator = new TooltipPositionCalculator();

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent event) {
        if (event.itemStack == null) return;

        event.alternativeRenderer = tooltip -> {
            if (tooltip.isEmpty()) return;

            String displayName = event.itemStack.getDisplayName();
            String oredict = getIdentifier(event.itemStack);
            String modName = nameFromStack(event.itemStack.getItem());

            renderer.setAdditionalInfo(oredict, modName, displayName);

            int width = renderer.calculateTooltipWidth(tooltip, event.font);
            int height = renderer.calculateTooltipHeight(tooltip, event.font);

            int[] position = positionCalculator.calculateSafePosition(event.x, event.y, width, height);

            renderer.renderCustomTooltip(tooltip, event.font, position[0], position[1], width, height, event.itemStack);
        };
    }

    protected String getIdentifier(ItemStack stack) {
        String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
        return name == null || name.isEmpty() ? "Unknown:Unknown" : name;
    }

    protected static String nameFromStack(Item item) {
        try {
            ModContainer mod = Loader.instance().getIndexedModList().get(getModId(item));
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




