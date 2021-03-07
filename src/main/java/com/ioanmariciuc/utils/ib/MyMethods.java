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

    public static void connect(String ip, int port, int clientID) {
        EClientSocket client = Values.ibReceiver.getClientSocket();
        client.eConnect(ip, port, clientID);

        final EReader reader = new EReader(client, Values.ibReceiver.getReaderSignal());
        final boolean[] wasConnected = {false};

        reader.start();
        new Thread(() -> {
            while (client.isConnected()) {
                if(!wasConnected[0]) {
                    Platform.runLater(() -> Controller.c.circleTwinkle());
                }
                wasConnected[0] = true;
                Platform.runLater(() -> {
                    Controller.c.updateCircle();
                });
                Values.ibReceiver.getReaderSignal().waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }

            if(!client.isConnected() && wasConnected[0]) {
                Platform.runLater(() -> {
                    Controller.c.updateCircle();
                    Controller.c.circleTwinkle();
                    ActionBox.display("INFO", "Connection lost !", "Uf, Ok...", "red");
                });
            } else {
                Platform.runLater(() -> {
                    Controller.c.updateCircle();
                    Controller.c.circleTwinkle();
                    ActionBox.display("INFO", "IB Error: -1 502 Couldn't connect to TWS. Confirm that \"Enable ActiveX and Socket Clients\" is enabled and connection port is the same as \"Socket Port\" on the TWS \"Edit->Global Configuration...->API->Settings\" menu.", "Uf, Ok...", "red");
                });
            }
        }).start();
    }

    public static boolean isConnected() {
        return Values.ibReceiver.getClientSocket().isConnected();
    }

    public static void disconnect() {
        Values.ibReceiver.getClientSocket().eDisconnect();
        Platform.runLater(() -> Controller.c.circleTwinkle());
    }

    public static List<Order> createBracketOrder(int parentOrderId, String action, double quantity, double auxPrice, double limitPrice, double takeProfitLimitPrice, double stopLossPrice) {
        Order parent = new Order();
        parent.orderId(parentOrderId);
        parent.action(action);
        parent.orderType("STP LMT");
        parent.totalQuantity(quantity);
        parent.lmtPrice(limitPrice);
        parent.auxPrice(auxPrice);
        parent.transmit(false);

        Order stopLoss = new Order();
        stopLoss.orderId(parent.orderId() + 1);
        stopLoss.action(action.equals("BUY") ? "SELL" : "BUY");
        stopLoss.orderType("STP");
        stopLoss.auxPrice(stopLossPrice);
        stopLoss.totalQuantity(quantity);
        stopLoss.parentId(parentOrderId);
        stopLoss.transmit(false);

        Order takeProfit = new Order();
        takeProfit.orderId(parent.orderId() + 2);
        takeProfit.action(action.equals("BUY") ? "SELL" : "BUY");
        takeProfit.orderType("LMT");
        takeProfit.totalQuantity(quantity);
        takeProfit.lmtPrice(takeProfitLimitPrice);
        takeProfit.parentId(parentOrderId);
        takeProfit.transmit(true);

        List<Order> bracketOrder = new ArrayList<>();
        bracketOrder.add(parent);
        bracketOrder.add(stopLoss);
        bracketOrder.add(takeProfit);

        return bracketOrder;
    }

    public static void placeOrder(List<Order> bracket) {
        for (Order o : bracket) {
            if(o != null)
                Values.ibReceiver.getClientSocket().placeOrder(o.orderId(), Values.getContract(), o);
        }

        Platform.runLater(() -> Controller.c.circleTwinkle());
        Platform.runLater(() -> Controller.c.reset());
    }
}