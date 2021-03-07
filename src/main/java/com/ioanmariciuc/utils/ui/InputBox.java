package com.ioanmariciuc.utils.ui;

import com.ioanmariciuc.utils.ib.MyMethods;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InputBox {

    public static void display(String title, String buttonMessage) {
        Stage window = new Stage();
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(false);
        window.setTitle(title);
        window.setWidth(300);
        window.setMinHeight(250);

        TextField port = new TextField();
        port.setText("7497");
        port.setPromptText("PORT");
        port.setStyle("-fx-background-radius: 25;" +
                "-fx-border-radius: 25;" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-color: black;" +
                "-fx-text-fill: #454545;" +
                "-fx-prompt-text-fill: #666666;-fx-max-width: 75px");
        port.setAlignment(Pos.CENTER);

        Label socketPort = new Label();
        socketPort.setText("Socket port");
        socketPort.setStyle("-fx-text-fill: darkgray;-fx-font-size: 15;");

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(port, socketPort);
        hBox.alignmentProperty().setValue(Pos.CENTER);

        Button button = new Button();
        button.setText(buttonMessage);
        button.setOnAction(e -> {
            MyMethods.connect("127.0.0.1", Integer.parseInt(port.getText()), 2);
            window.close();
        });
        button.setStyle("-fx-min-width: 75px;");

        VBox vBox = new VBox(15);
        vBox.getChildren().addAll(hBox, button);
        vBox.setPadding(new Insets(10, 30, 10, 30));
        vBox.alignmentProperty().setValue(Pos.CENTER);

        StackPane layout = new StackPane();
        layout.setStyle("-fx-padding: 20px;");
        layout.getChildren().addAll(vBox);
        layout.getStylesheets().add("theme.css");

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }

}
