package com.ioanmariciuc;

import com.ioanmariciuc.utils.ib.MyMethods;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("IB TWS Trade");
            primaryStage.setScene(new Scene(root, 250, 540));
            primaryStage.setResizable(false);
            primaryStage.show();

            primaryStage.setOnCloseRequest(e -> MyMethods.disconnect());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
