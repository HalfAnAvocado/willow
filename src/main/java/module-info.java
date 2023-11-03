module com.marvinelsen.willowkotlin {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens com.marvinelsen.willowkotlin to javafx.fxml;
    exports com.marvinelsen.willowkotlin;
}