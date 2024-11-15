package application.portfolio.clientmodule.utils;

import javafx.scene.control.Alert;

public class CustomAlert extends Alert {

    public CustomAlert(String message) {
        super(AlertType.INFORMATION);
        this.setTitle("Error");
        this.setHeaderText(message);
    }

    public CustomAlert(String title, String message) {
        super(AlertType.INFORMATION);
        this.setTitle(title);
        this.setHeaderText(message);
    }
}
