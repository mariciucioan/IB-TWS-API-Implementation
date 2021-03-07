package com.ioanmariciuc.utils.ib;

import com.ib.client.*;
import com.ioanmariciuc.Controller;
import com.ioanmariciuc.utils.Utils;
import com.ioanmariciuc.utils.Values;
import com.ioanmariciuc.utils.ui.ActionBox;
import javafx.application.Platform;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IBReceiver implements EWrapper {
    private final EReaderSignal readerSignal;
    private final EClientSocket clientSocket;
    protected int currentOrderId = 10;

    public IBReceiver() {
        readerSignal = new EJavaSignal();
        clientSocket = new EClientSocket(this, readerSignal);
    }

    public EClientSocket getClientSocket() {
        return clientSocket;
    }
    public EReaderSignal getReaderSignal() { return readerSignal; }

    public int getCurrentOrderId() {
        return currentOrderId;
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attribs) {
        if(field == 6) { // hotd
            if(tickerId == 3001 || tickerId == 3003) {
                Values.setHigh(price);
            }

            if(tickerId == 3001) {
                Platform.runLater(()-> Controller.c.setEntryText(String.valueOf(Values.getHigh())));
            }
            if(tickerId == 3003) {
                Platform.runLater(()-> Controller.c.setStopText(String.valueOf(Values.getHigh())));
            }
        } else if(field == 7) {
            if(tickerId == 3002 || tickerId == 3004) {
                Values.setLow(price);
            }
            if(tickerId == 3002) {
                Platform.runLater(()-> Controller.c.setEntryText(String.valueOf(Values.getLow())));
            }

            if(tickerId == 3004) {
                Platform.runLater(()-> Controller.c.setStopText(String.valueOf(Values.getLow())));
            }
        }
    }

    @Override
    public void tickSize(int i, int i1, int i2) {

    }

    @Override
    public void tickOptionComputation(int i, int i1, int i2, double v, double v1, double v2, double v3, double v4, double v5, double v6, double v7) {

    }

    @Override
    public void tickGeneric(int i, int i1, double v) {

    }

    @Override
    public void tickString(int i, int i1, String s) {

    }

    @Override
    public void tickEFP(int i, int i1, double v, String s, double v1, int i2, String s1, double v2, double v3) {

    }

    @Override
    public void orderStatus(int orderId, String status, double filled,
                            double remaining, double avgFillPrice, int permId, int parentId,
                            double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {
        System.out.println("OrderStatus. Id: "+orderId+", Status: "+status+", Filled"+filled+", Remaining: "+remaining
                +", AvgFillPrice: "+avgFillPrice+", PermId: "+permId+", ParentId: "+parentId+", LastFillPrice: "+lastFillPrice+
                ", ClientId: "+clientId+", WhyHeld: "+whyHeld+", MktCapPrice: "+mktCapPrice);
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        System.out.println(EWrapperMsgGenerator.openOrder(orderId, contract, order, orderState));
    }

    @Override
    public void openOrderEnd() {
        System.out.println("Open Order end");
    }

    @Override
    public void updateAccountValue(String s, String s1, String s2, String s3) {

    }

    @Override
    public void updatePortfolio(Contract contract, double v, double v1, double v2, double v3, double v4, double v5, String s) {

    }

    @Override
    public void updateAccountTime(String s) {

    }

    @Override
    public void accountDownloadEnd(String s) {

    }

    @Override
    public void nextValidId(int orderId) {
        //currentOrderId = i;

        if(Values.getContract() != null) {
            String action = (Values.isLong() ? "BUY" : "SELL");
            int qty = Values.getQuantity();
            double aux = Utils.round(Values.getEntryValue(), 2);
            double lmtPercent = Utils.round(Values.getLimitValue() / 100, 2);
            double lmt;
            double takeProfit = Utils.round(Values.getTarget(), 2);
            double stopLoss = Utils.round(Values.getStopValue(), 2);
            if (Values.isShort())
                lmt = Utils.round(Values.getEntryValue() - (lmtPercent * Values.getStopSizeValue()), 2);
            else lmt = Utils.round(Values.getEntryValue() + (lmtPercent * Values.getStopSizeValue()), 2);

            System.out.println("bracket order: " + action + ", " + qty + ", " + aux + ", " + lmt + ", " + takeProfit + ", " + stopLoss);

            List<Order> bracket = MyMethods.createBracketOrder(orderId++, action, qty, aux, lmt, takeProfit, stopLoss);
            MyMethods.placeOrder(bracket);

            System.out.println();
            System.out.println();
            System.out.println();
        }
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        Values.setName(contractDetails.longName());
        Values.ibReceiver.getClientSocket().reqMarketDataType(3);
        Platform.runLater(() -> Controller.c.setLabelName(Values.getName()));
    }

    @Override
    public void bondContractDetails(int i, ContractDetails contractDetails) {

    }

    @Override
    public void contractDetailsEnd(int reqId) {

    }

    @Override
    public void execDetails(int i, Contract contract, Execution execution) {

    }

    @Override
    public void execDetailsEnd(int i) {

    }

    @Override
    public void updateMktDepth(int i, int i1, int i2, int i3, double v, int i4) {

    }

    @Override
    public void updateMktDepthL2(int i, int i1, String s, int i2, int i3, double v, int i4, boolean b) {

    }

    @Override
    public void updateNewsBulletin(int i, int i1, String s, String s1) {

    }

    @Override
    public void managedAccounts(String accountsList) {
        System.out.println("Account list: " +accountsList);
    }

    @Override
    public void receiveFA(int i, String s) {

    }

    @Override
    public void historicalData(int tickerId, Bar bar) {
        switch (tickerId) {
            case 4001:
                Values.setHigh(bar.high());
                Platform.runLater(() -> Controller.c.setEntryText(String.valueOf(bar.high())));
                break;
            case 4002:
                Values.setLow(bar.low());
                Platform.runLater(() -> Controller.c.setEntryText(String.valueOf(bar.low())));
                break;
            case 4003:
                Values.setHigh(bar.high());
                Platform.runLater(() -> Controller.c.setStopText(String.valueOf(bar.high())));
                break;
            case 4004:
                Values.setLow(bar.low());
                Platform.runLater(() -> Controller.c.setStopText(String.valueOf(bar.low())));
                break;
        }
    }

    @Override
    public void scannerParameters(String s) {

    }

    @Override
    public void scannerData(int i, int i1, ContractDetails contractDetails, String s, String s1, String s2, String s3) {

    }

    @Override
    public void scannerDataEnd(int i) {

    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high,
                            double low, double close, long volume, double wap, int count) {
        System.out.println("RealTimeBars. " + reqId + " - Time: " + time + ", Open: " + open + ", High: " + high + ", Low: " + low + ", Close: " + close + ", Volume: " + volume + ", Count: " + count + ", WAP: " + wap);
    }

    @Override
    public void currentTime(long l) {

    }

    @Override
    public void fundamentalData(int i, String s) {

    }

    @Override
    public void deltaNeutralValidation(int i, DeltaNeutralContract deltaNeutralContract) {

    }

    @Override
    public void tickSnapshotEnd(int i) {

    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {
        System.out.println("MarketDataType. ["+reqId+"], Type: ["+marketDataType+"]\n");
    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {

    }

    @Override
    public void position(String s, Contract contract, double v, double v1) {

    }

    @Override
    public void positionEnd() {

    }

    @Override
    public void accountSummary(int i, String s, String s1, String s2, String s3) {

    }

    @Override
    public void accountSummaryEnd(int i) {

    }

    @Override
    public void verifyMessageAPI(String s) {

    }

    @Override
    public void verifyCompleted(boolean b, String s) {

    }

    @Override
    public void verifyAndAuthMessageAPI(String s, String s1) {

    }

    @Override
    public void verifyAndAuthCompleted(boolean b, String s) {

    }

    @Override
    public void displayGroupList(int i, String s) {

    }

    @Override
    public void displayGroupUpdated(int i, String s) {

    }

    @Override
    public void error(Exception e) {
        System.err.println("IBBroker error " + e.getMessage());
    }

    @Override
    public void error(String str) {
        System.err.println("IBBroker error " + str);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg)
    {
        System.err.println("IB Error: " + id + " " + errorCode + " " + errorMsg);

        // Some data farm is connected/disconnected, I just need to ignore it
        if (Arrays.asList(2103, 2104, 2105, 2106, 2108).contains(errorCode)) {
            Platform.runLater(() -> Controller.c.reset());
            return;
        }

        if (errorCode == 200) {
            System.err.println("IBBroker error: " + id + "," + errorCode + "," + errorMsg);
            return;
        }

        // Order cancelled, can't modify a filled order, order held while securities are located, cancel attempted
        // when order is not in a cancellable state, Unable to modify this order as its still being processed
        if (Arrays.asList(201, 202, 104, 404, 161, 2102).contains(errorCode)) {
            return;
        }

        if (errorMsg.equals(""))
            return;

        // Happens when IB kills the order for whatever reason, and then I try to modify it
        if (errorCode == 103)
            return;

        // if can't find order
        if(errorCode == 135)
            return;

        // Duplicate ticker id
        if (errorCode == 322 && errorMsg.contains("Duplicate ticker id"))
            return;

        // Requested market data is not subscribed, not sure why I get this - maybe 2
        // seconds isn't enough to subscribe and unsubscribe
        if (errorCode == 354 || errorCode == 300 || errorCode == 420) {
            Platform.runLater(() -> ActionBox.display("TWS Error", errorMsg, "Ok", "red"));
            return;
        }

        // ib disconnected, ib connection restored
        if (errorCode == 1100 || errorCode == 1102)
            return;

        if (errorCode == 0 && errorMsg.contains("Warning: Approaching max rate of 50 messages per second")) {
            System.out.println("IB approaching max rate of messages per second");
            return;
        }

        if(id != -1)
            Platform.runLater(() -> ActionBox.display("TWS Error", errorMsg, "Ok", "red"));
    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectAck() {

    }

    @Override
    public void positionMulti(int i, String s, String s1, Contract contract, double v, double v1) {

    }

    @Override
    public void positionMultiEnd(int i) {

    }

    @Override
    public void accountUpdateMulti(int i, String s, String s1, String s2, String s3, String s4) {

    }

    @Override
    public void accountUpdateMultiEnd(int i) {

    }

    @Override
    public void securityDefinitionOptionalParameter(int i, String s, int i1, String s1, String s2, Set<String> set, Set<Double> set1) {

    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int i) {

    }

    @Override
    public void softDollarTiers(int i, SoftDollarTier[] softDollarTiers) {

    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {

    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {
        int counter = 0;

        for (ContractDescription  cd : contractDescriptions) {
            Contract c = cd.contract();

            if(!c.currency().equals("USD") || !c.secType().equals(Types.SecType.STK))
                return;

            if(!c.symbol().equals(Values.getSymbolValue())) {
                Values.setContract(null);
                Platform.runLater(() -> Controller.c.setLabelName("STOCK NAME"));
                Values.setName("STOCK NAME");
                return;
            }

            counter ++;

            c.exchange("SMART");
            Values.setContract(c);
            clientSocket.reqContractDetails(222, c);
            break;
        }

        if(counter == 0) {
            Values.setContract(null);
            Platform.runLater(() -> Controller.c.setLabelName("STOCK NAME"));
            Values.setName("STOCK NAME");
        }
    }

    @Override
    public void historicalDataEnd(int i, String s, String s1) {
        System.out.println("End of historical data: " + s + " -> " + s1);
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {

    }

    @Override
    public void tickNews(int i, long l, String s, String s1, String s2, String s3) {

    }

    @Override
    public void smartComponents(int i, Map<Integer, Map.Entry<String, Character>> map) {

    }

    @Override
    public void tickReqParams(int i, double v, String s, int i1) {

    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {

    }

    @Override
    public void newsArticle(int i, int i1, String s) {

    }

    @Override
    public void historicalNews(int i, String s, String s1, String s2, String s3) {

    }

    @Override
    public void historicalNewsEnd(int i, boolean b) {

    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {
        System.out.println("Head timestamp. Req Id: " + reqId + ", headTimestamp: " + headTimestamp);
    }

    @Override
    public void histogramData(int i, List<HistogramEntry> list) {

    }

    @Override
    public void historicalDataUpdate(int i, Bar bar) {

    }

    @Override
    public void rerouteMktDataReq(int i, int i1, String s) {

    }

    @Override
    public void rerouteMktDepthReq(int i, int i1, String s) {

    }

    @Override
    public void marketRule(int i, PriceIncrement[] priceIncrements) {

    }

    @Override
    public void pnl(int i, double v, double v1, double v2) {

    }

    @Override
    public void pnlSingle(int i, int i1, double v, double v1, double v2, double v3) {

    }

    @Override
    public void historicalTicks(int i, List<HistoricalTick> list, boolean b) {

    }

    @Override
    public void historicalTicksBidAsk(int i, List<HistoricalTickBidAsk> list, boolean b) {
        list.forEach(e -> {
            Values.setLow(e.priceAsk());
            Values.setHigh(e.priceBid());
            if(i == 255) {
                Platform.runLater(() -> Controller.c.setEntryText(String.valueOf(Values.getHigh())));
            } else if(i == 256) {
                Platform.runLater(() -> Controller.c.setEntryText(String.valueOf(Values.getLow())));
            }
        });
    }

    @Override
    public void historicalTicksLast(int i, List<HistoricalTickLast> list, boolean b) {

    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, int size, TickAttribLast tickAttribLast,
                                  String exchange, String specialConditions) {
        System.out.println(EWrapperMsgGenerator.tickByTickAllLast(reqId, tickType, time, price, size, tickAttribLast, exchange, specialConditions));
    }

    @Override
    public void tickByTickBidAsk(int tickerId, long time, double bidPrice, double askPrice, int bidSize, int askSize,
                                 TickAttribBidAsk tickAttribBidAsk) {
    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {
        System.out.println(EWrapperMsgGenerator.tickByTickMidPoint(reqId, time, midPoint));
    }

    @Override
    public void orderBound(long l, int i, int i1) {

    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {

    }

    @Override
    public void completedOrdersEnd() {

    }

    @Override
    public void replaceFAEnd(int i, String s) {

    }
}
