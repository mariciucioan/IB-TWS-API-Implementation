module Trading.main {
    requires java.base;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires TwsApi;

    opens com.ioanmariciuc to javafx.fxml;
    exports com.ioanmariciuc;
}