package com.ioanmariciuc.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class Utils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double toDouble(String str) {
        if(str.equals(""))
            return 0;

        try {
            DecimalFormat df = new DecimalFormat();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            if(str.contains(","))
                symbols.setDecimalSeparator(',');
            else symbols.setDecimalSeparator('.');
            symbols.setGroupingSeparator(' ');
            df.setDecimalFormatSymbols(symbols);
            return df.parse(str).doubleValue();
        } catch (ParseException e) {
            System.out.println("Error.");
        }

        return -1;
    }
}
