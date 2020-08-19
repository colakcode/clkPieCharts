package com.clk.donutchart;

import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class AmountAccetable {

    private static final String TAG = "clk_AmountAccetable";

    public static String get(double d) {
        d = Math.floor(d * 100) / 100;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter;
        String language = Locale.getDefault().getDisplayLanguage();
        if (language.equals("Türkçe")) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator(',');
            dfs.setGroupingSeparator('.');
            formatter = new DecimalFormat("###,###,##0.00", dfs);
        } else {
            formatter = (DecimalFormat) numberFormat;
            formatter.applyPattern("###,###,##0.00");
        }
        return formatter.format(d);
    }

    public static String get(String s) {
        //todo amountAcceptable
        double d = 0;
        try {
            d = Double.valueOf(s);
            d = floorDouble(d);
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter;
            String language = Locale.getDefault().getDisplayLanguage();
            if (language.equals("Türkçe")) {
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator(',');
                dfs.setGroupingSeparator('.');
                formatter = new DecimalFormat("###,###,##0.00", dfs);
            } else {
                formatter = (DecimalFormat) numberFormat;
                formatter.applyPattern("###,###,##0.00");
            }

            return formatter.format(d);
        } catch (NumberFormatException e) {
            return s;
        }

    }

    public static double amountDouble(String sAmount, String method) {

        Log.d(TAG, "amountDouble: ");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        Number number = null;
        try {

            number = numberFormat.parse(sAmount);
            Log.d(TAG, "amountDouble: try: " + floorDouble(number.doubleValue()));
            return floorDouble(number.doubleValue());

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "amountDouble: catch :" + "sAmount : " + sAmount + " catch :" + e.getMessage() +
                    "method: " + method);
            return 0;
        }
    }

    public static String amountSdouble(String sAmount) {
        new BigDecimal("123456789987654321123456789.0");
        BigDecimal mAmount;
        mAmount = BigDecimal.valueOf(Double.valueOf(sAmount));
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) numberFormat;
        formatter.applyPattern("########0.00");
        String s = formatter.format(mAmount);
        return s;
    }

    public static double floorDouble(double d) {
        d = Math.floor(d * 100) / 100;
        return d;
    }
}
