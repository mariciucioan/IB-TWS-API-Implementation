package com.ioanmariciuc.utils;

import com.ib.client.Contract;
import com.ioanmariciuc.utils.ib.IBReceiver;

public class Values {
    private static Contract contract;

    private static boolean shortValue;
    private static String symbolValue = "";
    private static double entryValue = 0;
    private static double stopValue = 0;
    private static double RValue = 0;
    private static double limitValue = 0;
    private static int RTypeValue = 0;

    private static double high;
    private static double low;

    private static String name = "STOCK NAME";

    public static IBReceiver ibReceiver = new IBReceiver();

    public static Contract getContract() {
        return contract;
    }

    public static void setContract(Contract contract) {
        Values.contract = contract;
    }

    public static boolean isShort() {
        return Values.shortValue;
    }
    public static boolean isLong() {
        return !Values.shortValue;
    }

    public static void setShort() {
        Values.shortValue = true;
    }

    public static void setLong() {
        Values.shortValue = false;
    }

    public static String getSymbolValue() {
        return symbolValue;
    }

    public static void setSymbolValue(String symbolValue) {
        Values.symbolValue = symbolValue;
    }

    public static double getEntryValue() {
        return entryValue;
    }

    public static void setEntryValue(double entryValue) {
        Values.entryValue = entryValue;
    }

    public static double getStopValue() {
        return stopValue;
    }

    public static void setStopValue(double stopValue) {
        Values.stopValue = stopValue;
    }

    public static double getRValue() {
        return RValue;
    }

    public static void setRValue(double RValue) {
        Values.RValue = RValue;
    }

    public static double getLimitValue() {
        return limitValue;
    }

    public static void setLimitValue(double limitValue) {
        Values.limitValue = limitValue;
    }

    public static int getRTypeValue() {
        return RTypeValue;
    }

    public static void setRTypeValue(int RTypeValue) {
        Values.RTypeValue = RTypeValue;
    }

    public static double getStopSizeValue() {
        return Math.abs(Values.entryValue-Values.stopValue);
    }

    public static double getHigh() {
        return high;
    }

    public static void setHigh(double high) {
        Values.high = high;
    }

    public static double getLow() {
        return low;
    }

    public static void setLow(double low) {
        Values.low = low;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Values.name = name;
    }

    public static int getQuantity() {
        return (int) Math.floor(Values.getRValue() / Values.getStopSizeValue());
    }

    public static double getRealRisk() {
        return Values.getQuantity() * Values.getStopSizeValue();
    }

    public static double getTotalAmount() {
        return Values.getEntryValue() * Values.getQuantity();
    }

    public static double getTarget() {
        if(isShort()) {
            return Values.getEntryValue() - (Values.getRTypeValue() * Values.getStopSizeValue());
        }

        return Values.getEntryValue() + (Values.getRTypeValue() * Values.getStopSizeValue());
    }
}
