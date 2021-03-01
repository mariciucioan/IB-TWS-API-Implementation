package com.ioanmariciuc.utils.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ActionBox {

    public static void display(String title, String message) {
        display(title, message, "OK", "gray");
    }

    public static void display(String title, String message, String buttonMessage, String color) {
        Stage window = new Stage();
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(false);
        window.setTitle(title);
        window.setWidth(500);
        window.setMinHeight(250);

        Label label = new Label();
        label.setText(message);
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setMaxWidth(window.getWidth()-(window.getWidth()/10));
        label.setStyle("-fx-text-fill: " + color + ";-fx-padding: 15px 10px 15px 10px;");

        Button button = new Button();
        button.setText(buttonMessage);
        button.setOnAction(e -> window.close());
        button.setStyle("-fx-min-width: 75px;");

        VBox vBox = new VBox(15);
        vBox.getChildren().addAll(label, button);
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
