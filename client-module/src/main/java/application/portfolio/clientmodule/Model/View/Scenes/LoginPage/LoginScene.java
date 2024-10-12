package application.portfolio.clientmodule.Model.View.Scenes.LoginPage;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.HttpClient.UserSession;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.OtherElements.temp.Objects;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import application.portfolio.clientmodule.utils.PageFactory;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;


public class LoginScene extends GridPane implements Page {

    private final Label loginLbl = new Label("Login/Email:");
    private final Label passLbl = new Label("Password:");

    private final TextField loginTf = new TextField();
    private final PasswordField passPf = new PasswordField();
    private final Button loginBtn = new Button("Log In");
    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private final List<PersonDAO> persons = Objects.getPersons();

    private LoginScene() {
    }


    @Override
    public CompletableFuture<Boolean> initializePage() {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Platform.runLater(() -> {
            try {
                completePage();
                bindFields();
                future.complete(true);
            } catch (Exception e) {
                future.complete(false);
                System.out.println("initializePage, " + this.getClass().getSimpleName());
                e.printStackTrace();
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

            this.add(loginLbl, 0, 0);
            this.add(loginTf, 1, 0);
            loginTf.setPromptText("example@company.com");

            this.add(passLbl, 0, 1);
            this.add(passPf, 1, 1);

            this.add(loginBtn, 0, 2);
            GridPane.setColumnSpan(loginBtn, 2);
            GridPane.setHalignment(loginBtn, HPos.CENTER);
            loginBtn.setMaxWidth(Double.MAX_VALUE);
            loginBtn.requestFocus();

    }

    public void bindFields() {

        loginBtn.setOnAction(evt -> {

            if (checkLogging()) {
                Page mainScene = PageFactory.getInstance(MainScene.class);
                TeamLinkApp.useScene(mainScene.getClass());
            }
        });
    }

    //TODO: TEMP
    private Boolean checkLogging() {

        String email = loginTf.getText();
        String password = passPf.getText();

        PersonDAO personDAO = new PersonDAO(email, password);

        Optional<PersonDAO> matchedUser = persons.stream()
                .filter(p -> p.equals(personDAO))
                .findFirst();

        return matchedUser.map(user -> {
            UserSession.getInstance().setLoggedInUser(user);
            System.out.println(user);
            return true;
        }).orElse(false);
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