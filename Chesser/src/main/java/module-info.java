module com.example.chesser {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chesser to javafx.fxml;
    exports com.example.chesser;
}