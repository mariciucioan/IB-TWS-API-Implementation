package com.ioanmariciuc;

import com.ioanmariciuc.utils.Values;
import com.ioanmariciuc.utils.ib.MyMethods;
import com.ioanmariciuc.utils.Utils;
import com.ioanmariciuc.utils.ui.ActionBox;
import com.ioanmariciuc.utils.ui.InputBox;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    ToggleGroup radioGroup;

    @FXML
    RadioButton R1, R2, R3, R4;

    @FXML
    Button buttonSubmit, buttonLong, buttonShort;

    @FXML
    TextField tfSymbol, tfEntry, tfStop, tfR, tfLMT;

    @FXML
    TextField tfQTY, tfRISK, tfAMT, tfTGT;

    @FXML
    HBox rHBox, lmtHBox;

    @FXML
    Circle circleConnection;
    @FXML
    Label labelStockName;

    public static Controller c;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        c=this;

        radioGroup = new ToggleGroup();
        R1.setToggleGroup(radioGroup);
        R2.setToggleGroup(radioGroup);
        R3.setToggleGroup(radioGroup);
        R4.setToggleGroup(radioGroup);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        tfSymbol.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                if(newValue.length() < 2) {
                    labelStockName.setText("STOCK NAME");
                    Values.setName("STOCK NAME");
                    Values.setContract(null);
                    return;
                }

                tfSymbol.setText(newValue.toUpperCase());
                Values.setSymbolValue(newValue);

                if(MyMethods.isConnected()) {
                    Values.ibReceiver.getClientSocket().reqMatchingSymbols(211, newValue);
                } else {
                    labelStockName.setText("STOCK NAME");
                    Values.setName("STOCK NAME");
                    Values.setContract(null);
                }

                tfSymbol.setId("");
                showDetails();
            });
            pause.playFromStart();
        });

        tfEntry.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d+)([,.]?)")) {
                tfEntry.setText(newValue.replaceAll("[^0-9.,]", ""));
            }

            Values.setEntryValue(Utils.toDouble(newValue));

            if(Values.getStopValue() != 0) {
                if (Utils.toDouble(newValue) > Values.getStopValue()) {
                    buttonLong.setStyle("-fx-background-color: darkgreen;-fx-text-fill: white;");
                    buttonShort.setStyle("-fx-background-color: lightgray;-fx-text-fill: gray;");
                    Values.setLong();
                } else {
                    buttonLong.setStyle("-fx-background-color: lightgray;-fx-text-fill: gray;");
                    buttonShort.setStyle("-fx-background-color: red;-fx-text-fill: white;");
                    Values.setShort();
                }
            }

            if(Utils.toDouble(newValue) == 0) {
                buttonShort.setStyle("-fx-background-color: lightgray;");
                buttonLong.setStyle("-fx-background-color: lightgray;");
            }

            tfEntry.setId("");
            if(newValue.contains(","))
                tfEntry.setText(newValue.replaceAll(",", "."));
            showDetails();
        });

        tfStop.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d+)([,.]?)")) {
                tfStop.setText(newValue.replaceAll("[^0-9.,]", ""));
            }

            Values.setStopValue(Utils.toDouble(newValue));
            if(Values.getEntryValue() != 0) {
                if (Utils.toDouble(newValue) > Values.getEntryValue()) {
                    buttonLong.setStyle("-fx-background-color: lightgray;-fx-text-fill: gray;");
                    buttonShort.setStyle("-fx-background-color: red;-fx-text-fill: white;");
                    Values.setShort();
                } else {
                    buttonLong.setStyle("-fx-background-color: darkgreen;-fx-text-fill: white;");
                    buttonShort.setStyle("-fx-background-color: lightgray;-fx-text-fill: gray;");
                    Values.setLong();
                }
            }

            if(Utils.toDouble(newValue) == 0) {
                buttonShort.setStyle("-fx-background-color: lightgray;");
                buttonLong.setStyle("-fx-background-color: lightgray;");
            }

            tfStop.setId("");
            if(newValue.contains(","))
                tfStop.setText(newValue.replaceAll(",", "."));
            showDetails();
        });

        tfR.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d+)([,.]?)")) {
                tfR.setText(newValue.replaceAll("[^0-9.,]", ""));
                return;
            }

            Values.setRValue(Utils.toDouble(newValue));

            tfR.setId("");
            if(newValue.contains(","))
                tfR.setText(newValue.replaceAll(",", "."));
            showDetails();
        });
        tfLMT.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d+)([,.]?)")) {
                tfLMT.setText(newValue.replaceAll("[^0-9.,]", ""));
                return;
            }

            if(Utils.toDouble(newValue) >= 100) {
                tfLMT.setText("100");
                return;
            }

            Values.setLimitValue(Utils.toDouble(newValue));

            tfLMT.setId("");
            if(newValue.contains(","))
                tfLMT.setText(newValue.replaceAll(",", "."));
            showDetails();
        });

        updateCircle();
    }

    private void showDetails() {
        if(!tfSymbol.getText().equals("") && !tfEntry.getText().equals("") && Values.getRTypeValue() != 0
                && !tfStop.getText().equals("") && !tfLMT.getText().equals("") && !tfR.getText().equals("")) {

            DecimalFormat df2 = new DecimalFormat("#.##");

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

            symbols.setGroupingSeparator(',');
            formatter.setDecimalFormatSymbols(symbols);

            tfQTY.setText(df2.format(Values.getQuantity()));
            tfRISK.setText(df2.format(Values.getRealRisk()) + "$");
            tfAMT.setText(formatter.format(Values.getTotalAmount()) + "$");
            tfTGT.setText(df2.format(Values.getTarget()) + "$");
        }
    }

    public void updateCircle() {
        if(MyMethods.isConnected()) {
            circleConnection.setId("connected");
        } else {
            circleConnection.setId("disconnected");
        }
    }

    @FXML
    private void clickSubmit(ActionEvent event) {
        if(!MyMethods.isConnected()) {
            ActionBox.display("Error", "You are not connected to TWS application. Go to \"Connection->Connect\" in order to connect...", "Thanks!", "red");
            return;
        }

        if(Values.getSymbolValue().equals("")
                || Values.getEntryValue() == 0
                || Values.getStopValue() == 0
                || Values.getRTypeValue() == 0
                || Values.getRValue() == 0
                || Values.getName().equals("STOCK NAME")) {
            if(Values.getSymbolValue().equals(""))
                tfSymbol.setId("error");

            if(Values.getEntryValue() == 0)
                tfEntry.setId("error");

            if(Values.getStopValue() == 0 )
                tfStop.setId("error");

            if(Values.getRTypeValue() == 0)
                rHBox.setId("error");

            if(Values.getRValue() == 0)
                tfR.setId("error");

            if(Values.getLimitValue() == 0)
                tfLMT.setId("error");

            if(Values.getName().equals("STOCK NAME")) {
                labelStockName.setId("error");
                tfSymbol.setId("error");
            }
        } else {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println(Values.getSymbolValue());
            System.out.println(Values.getEntryValue());
            System.out.println(Values.getStopValue());
            System.out.println(Values.getRValue());
            System.out.println(Values.getLimitValue());
            System.out.println(Values.getRTypeValue());
            System.out.println(Values.getStopSizeValue());
            Values.ibReceiver.getClientSocket().reqIds(Values.ibReceiver.getCurrentOrderId());
        }

        System.out.println(event);
    }

    @FXML
    private void click1R(ActionEvent event) {
        Values.setRTypeValue(1);
        rHBox.setId("");
        showDetails();

        System.out.println(event);
    }

    @FXML
    private void click2R(ActionEvent event) {
        Values.setRTypeValue(2);
        rHBox.setId("");
        showDetails();

        System.out.println(event);
    }

    @FXML
    private void click3R(ActionEvent event) {
        Values.setRTypeValue(3);
        rHBox.setId("");
        showDetails();

        System.out.println(event);
    }

    @FXML
    private void click4R(ActionEvent event) {
        Values.setRTypeValue(4);
        rHBox.setId("");
        showDetails();

        System.out.println(event);
    }

    @FXML
    private void clickReset(ActionEvent event) {
        reset();

        System.out.println(event);
    }

    public void reset() {
        tfSymbol.clear();
        tfEntry.clear();
        tfStop.clear();
        tfQTY.clear();
        tfRISK.clear();
        tfAMT.clear();
        tfTGT.clear();

        Values.setSymbolValue("");
        Values.setName("STOCK NAME");
        Values.setEntryValue(0);
        Values.setStopValue(0);
        Values.setContract(null);

        buttonShort.setStyle("-fx-background-color: lightgray;");
        buttonLong.setStyle("-fx-background-color: lightgray;");
    }

    @FXML
    private void clickConnect(ActionEvent event) {
        InputBox.display("Configure client socket", "Connect");

        System.out.println(event);
    }

    @FXML
    private void clickDisconnect(ActionEvent event) {
        MyMethods.disconnect();
        updateCircle();

        System.out.println(event);
    }

    @FXML
    private void clickHD1(ActionEvent event) {
        if(Values.getContract() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            String endOfDay = dtf.format(LocalDateTime.now()) + " 16:30:00 EST";

            Values.ibReceiver.getClientSocket().reqHistoricalData(4001, Values.getContract(),
                    endOfDay, "26200 S", "1 day", "TRADES", 1, 2, false, null);
        }
                    
        System.out.println(event);
    }

    @FXML
    private void clickLD1(ActionEvent event) {
        if(Values.getContract() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            String endOfDay = dtf.format(LocalDateTime.now()) + " 16:30:00 EST";

            Values.ibReceiver.getClientSocket().reqHistoricalData(4002, Values.getContract(),
                    endOfDay, "26200 S", "1 day", "TRADES", 1, 2, false, null);
        }
        System.out.println(event);
    }

    @FXML
    private void clickHD2(ActionEvent event) {
        if(Values.getContract() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            String endOfDay = dtf.format(LocalDateTime.now()) + " 16:30:00 EST";

            Values.ibReceiver.getClientSocket().reqHistoricalData(4003, Values.getContract(),
                    endOfDay, "26200 S", "1 day", "TRADES", 1, 2, false, null);
        }

        System.out.println(event);
    }

    @FXML
    private void clickLD2(ActionEvent event) {
        if(Values.getContract() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            String endOfDay = dtf.format(LocalDateTime.now()) + " 16:30:00 EST";

            Values.ibReceiver.getClientSocket().reqHistoricalData(4004, Values.getContract(),
                    endOfDay, "26200 S", "1 day", "TRADES", 1, 2, false, null);
        }
        System.out.println(event);
    }

    public void setLabelName(String text) {
        labelStockName.setText(text);
        labelStockName.setId("");
    }

    public void setEntryText(String text) {
        tfEntry.setText(text);
    }

    public void setStopText(String text) {
        tfStop.setText(text);
    }

    public void circleTwinkle() {
        Timeline flasher = new Timeline(
                new KeyFrame(Duration.seconds(0.3), e -> circleConnection.setId("connected-twinkle")),
                new KeyFrame(Duration.seconds(0.6), e -> circleConnection.setId(
                        (MyMethods.isConnected() ? "connected" : "disconnected")))
        );

        flasher.setCycleCount(3);
        flasher.play();
    }
}
