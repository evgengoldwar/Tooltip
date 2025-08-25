package ToolTip;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PaginationHelper {

    private static int tooltipPage = 0;
    private static int maxTooltipPage = 1;
    private static boolean isTooltipActive = false;

    public List<List<String>> splitTooltipByPages(List<String> tooltip, int maxHeight) {
        List<List<String>> pages = new ArrayList<>();
        if (tooltip == null || tooltip.isEmpty()) return pages;

        List<String> currentPage = new ArrayList<>();
        int currentHeight = 0;
        int headerHeight = 40;
        int lineHeight = 10;

        for (String line : tooltip) {
            if (line != null && !line.trim().isEmpty()) {
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

    public static void nextPage() {
        if (isTooltipActive && maxTooltipPage > 1) {
            tooltipPage = (tooltipPage + 1) % maxTooltipPage;
        }
    }

    public void previousPage() {
        if (isTooltipActive && maxTooltipPage > 1) {
            tooltipPage = (tooltipPage - 1 + maxTooltipPage) % maxTooltipPage;
        }
    }

    public void setMaxTooltipPage(int maxPages) {
        maxTooltipPage = Math.max(1, maxPages);
        if (tooltipPage >= maxTooltipPage) {
            tooltipPage = 0;
        }
    }

    public void setTooltipActive(boolean active) {
        isTooltipActive = active;
        if (!active) {
            resetPagination();
        }
    }

    public static boolean isTooltipActive() {
        return isTooltipActive;
    }

    public void resetPagination() {
        tooltipPage = 0;
        maxTooltipPage = 1;
        isTooltipActive = false;
    }

    public int getTooltipPage() {
        return tooltipPage;
    }

    public int getMaxTooltipPage() {
        return maxTooltipPage;
    }
}
