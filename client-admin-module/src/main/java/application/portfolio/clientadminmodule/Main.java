package application.portfolio.clientadminmodule;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        Scene scene = new Scene(new HBox(), 300, 300);
        stage.setScene(scene);
        stage.show();
    }
}
