package com.clk.donutchart.utils;

public class OptimizeName {
    private static final String TAG = "clk_OptimizeName";

    public static String get(String category) {
        String new_name = "";

        if (category.length() > 11) {
            for (int i = 0; i < category.length(); i++) {
                if (i <= 7) {
                    char fchar = category.charAt(i);
                    new_name = new_name + fchar;

                } else if (i > 7 && i < 11) {
                    new_name = new_name + ".";

                } else if (i == 11) {
                    break;
                }
            }
        } else {
            new_name = category;
        }
        return new_name;
    }
}
