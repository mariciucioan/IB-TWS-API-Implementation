package com.ioanmariciuc.utils.ui;

import com.ioanmariciuc.utils.ib.MyMethods;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
        window.setWidth(500);
        window.setMinHeight(250);

        TextField ip = new TextField();
        ip.setText("127.0.0.1");
        ip.setPromptText("IP");
        ip.setStyle("-fx-background-radius: 25;" +
                "-fx-border-radius: 25;" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-color: black;" +
                "-fx-text-fill: #454545;" +
                "-fx-prompt-text-fill: #666666;");

        TextField port = new TextField();
        port.setText("7497");
        port.setPromptText("PORT");
        port.setStyle("-fx-background-radius: 25;" +
                "-fx-border-radius: 25;" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-color: black;" +
                "-fx-text-fill: #454545;" +
                "-fx-prompt-text-fill: #666666;");

        TextField id = new TextField();
        id.setText("2");
        id.setPromptText("ID");
        id.setStyle("-fx-background-radius: 25;" +
                "-fx-border-radius: 25;" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-color: black;" +
                "-fx-text-fill: #454545;" +
                "-fx-prompt-text-fill: #666666;");

        Button button = new Button();
        button.setText(buttonMessage);
        button.setOnAction(e -> {
            MyMethods.connect(ip.getText(), Integer.parseInt(port.getText()), Integer.parseInt(id.getText()));
            window.close();
        });
        button.setStyle("-fx-min-width: 75px;");

        VBox vBox = new VBox(15);
        vBox.getChildren().addAll(ip, port, id, button);
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
