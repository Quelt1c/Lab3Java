module com.example.smp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.smp to javafx.fxml;
    exports com.example.smp;
}