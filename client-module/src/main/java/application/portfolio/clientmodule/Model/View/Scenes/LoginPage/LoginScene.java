package application.portfolio.clientmodule.Model.View.Scenes.LoginPage;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.TeamLinkApp;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


public class LoginScene extends GridPane implements Page {

    private final Label loginLbl = new Label("Login/Email:");
    private final Label passLbl = new Label("Password:");

    private final TextField emailTf = new TextField();
    private final PasswordField passPf = new PasswordField();
    private final Button loginBtn = new Button("Log In");
    private final LoginBinder binder = new LoginBinder();

    private LoginScene() {
    }


    @Override
    public CompletableFuture<Boolean> initializePage() {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Platform.runLater(() -> {
            try {
                completePage();
                future.complete(true);
            } catch (Exception e) {
                future.complete(false);
            }
        });

        return future;
    }

    private void completePage() {

            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            col1.setHgrow(Priority.NEVER);
            col1.setPercentWidth(15);
            col2.setHgrow(Priority.NEVER);
            col2.setPercentWidth(15);
            getColumnConstraints().addAll(col1, col2);

            binder.withEmailTf(emailTf);
            binder.withPasswordTf(passPf);
            binder.withLoginBtn(loginBtn);

            this.add(loginLbl, 0, 0);
            this.add(emailTf, 1, 0);

            this.add(passLbl, 0, 1);
            this.add(passPf, 1, 1);

            this.add(loginBtn, 0, 2);
            GridPane.setColumnSpan(loginBtn, 2);
            GridPane.setHalignment(loginBtn, HPos.CENTER);
            loginBtn.setMaxWidth(Double.MAX_VALUE);
            loginBtn.requestFocus();
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadLoginPageStyles(TeamLinkApp.getScene(this.getClass()));
    }

    @Override
    public void loadStyles() {

        this.getStyleClass().add("backGround");

        for (var node : this.getChildren()) {
            if (node instanceof Label lbl) {
                lbl.getStyleClass().add("labels");
            } else if (node instanceof TextField tf) {
                tf.getStyleClass().add("textFields");
            }
        }
    }

    @Override
    public Parent asParent() {
        return this;
    }

    @Override
    public AtomicBoolean usedAsScene() {
        return new AtomicBoolean(true);
    }
}