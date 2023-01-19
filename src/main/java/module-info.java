module com.example.filmaficionado_eksamen_projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires javafx.media;


    opens com.example.filmaficionado_eksamen_projekt to javafx.fxml;
    exports com.example.filmaficionado_eksamen_projekt;
}