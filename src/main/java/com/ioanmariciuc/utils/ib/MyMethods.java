package com.ioanmariciuc.utils.ib;

import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.Order;
import com.ioanmariciuc.Controller;
import com.ioanmariciuc.utils.Values;
import com.ioanmariciuc.utils.ui.ActionBox;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class MyMethods {

    public static StringBuilder sb;

    public static void connect(String ip, int port, int clientID) {
        EClientSocket client = Values.ibReceiver.getClientSocket();
        client.eConnect(ip, port, clientID);

        final EReader reader = new EReader(client, Values.ibReceiver.getReaderSignal());

        reader.start();
        new Thread(() -> {
            while (client.isConnected()) {
                Platform.runLater(() -> Controller.c.updateCircle());
                Values.ibReceiver.getReaderSignal().waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }

            if(!client.isConnected()) {
                Platform.runLater(() -> {
                    Controller.c.updateCircle();
                    ActionBox.display("INFO", "Connection lost !", "Uf, Ok...", "red");
                });
            }
        }).start();
    }

    public static boolean isConnected() {
        return Values.ibReceiver.getClientSocket().isConnected();
    }

    public static void disconnect() {
        Values.ibReceiver.getClientSocket().eDisconnect();
    }

    public static List<Order> createBracketOrder(int parentOrderId, String action, double quantity, double limitPrice, double takeProfitLimitPrice, double stopLossPrice) {

        sb = new StringBuilder();
        double lmtPercent = Values.getLimitValue() / 100;

        System.out.println("debug 1");

        Order parent = new Order();
        parent.orderId(parentOrderId);
        parent.action(action);
        parent.orderType("LMT");
        parent.totalQuantity(quantity);
        parent.lmtPrice(limitPrice);
        parent.transmit(false);

        System.out.println("debug 2");

        sb.append(action).append(" STOP LIMIT ORDER: ").append((int) quantity)
                .append(" shares @ ").append(Values.getEntryValue()).append("$ stop with ")
                .append(limitPrice).append("$ limit, according to the 10% limit preset (")
                .append(Values.getLimitValue()).append("% of ").append(Values.getStopSizeValue())
                .append("$ is ").append(lmtPercent * Values.getStopSizeValue()).append("$\n");

        System.out.println("debug 3");

        Order stopLoss = new Order();
        stopLoss.orderId(parent.orderId() + 1);
        stopLoss.action(action.equals("BUY") ? "SELL" : "BUY");
        stopLoss.orderType("STP");
        stopLoss.auxPrice(stopLossPrice);
        stopLoss.totalQuantity(quantity);
        stopLoss.parentId(parentOrderId);
        stopLoss.transmit(true);

        System.out.println("debug 4");

        sb.append(action.equals("BUY") ? "SELL" : "BUY").append(" STOP MARKET ORDER: ")
                .append((int) quantity).append(" shares @ ").append(stopLossPrice)
                .append("$. Indicated in the stop price.\n");

        System.out.println("debug 5");

        Order takeProfit = new Order();
        takeProfit.orderId(parent.orderId() + 2);
        takeProfit.action(action.equals("BUY") ? "SELL" : "BUY");
        takeProfit.orderType("LMT");
        takeProfit.totalQuantity(quantity);
        takeProfit.lmtPrice(takeProfitLimitPrice);
        takeProfit.parentId(parentOrderId);
        takeProfit.transmit(false);

        System.out.println("debug 6");

        sb.append(action.equals("BUY") ? "SELL" : "BUY").append(" LIMIT ORDER: ")
                .append((int) quantity).append(" shares @ ").append(takeProfitLimitPrice)
                .append("$ as calculated in N7.\n");

        List<Order> bracketOrder = new ArrayList<>();
        bracketOrder.add(parent);
        bracketOrder.add(stopLoss);
        bracketOrder.add(takeProfit);

        System.out.println("debug 7");

        Platform.runLater(() -> ActionBox.display("Bracket Order", sb.toString()));

        System.out.println("debug 8");

        return bracketOrder;
    }

    public static void placeOrder(List<Order> bracket) {
        for (Order o : bracket) {
            if(o != null)
                Values.ibReceiver.getClientSocket().placeOrder(o.orderId(), Values.getContract(), o);
        }

        if(Values.ibReceiver.getClientSocket().isAsyncEConnect())

        Platform.runLater(() -> Controller.c.reset());
    }
}