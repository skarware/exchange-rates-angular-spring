package io.github.skarware.exchangerates.util;

public class NumberUtils {

    /**
     * Remove grouping commas if any and if comma used as decimal separator replace comma with dot
     */
    public static String toDotDecimalSeparator(String str) {
        return str.replaceAll(",(?=[0-9]+,)", "").replaceAll(",", ".");
    }
}
