package com.ioanmariciuc.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class StringUtils {

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
