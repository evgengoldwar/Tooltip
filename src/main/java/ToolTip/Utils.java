package ToolTip;

import java.util.List;
import java.util.Optional;

public class Utils {

    public static Optional<String> findGTNameFromList(List<String> strings) {
        return strings.stream()
            .flatMap(
                string -> java.util.Arrays.stream(ModTextures.values())
                    .filter(mod -> string.contains(mod.getModName()))
                    .map(ModTextures::getModName))
            .findFirst();
    }
}
