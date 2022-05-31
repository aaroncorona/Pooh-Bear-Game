module com.example.poohbearsnakegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.poohbearsnakegame to javafx.fxml;
    exports com.example.poohbearsnakegame;
}