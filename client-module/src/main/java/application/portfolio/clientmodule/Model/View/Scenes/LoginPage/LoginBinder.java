package application.portfolio.clientmodule.Model.View.Scenes.LoginPage;

import application.portfolio.clientmodule.Model.Request.Login.LoginRequestViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.HashSet;
import java.util.Set;

public class LoginBinder {

    private final LoginRequestViewModel viewModel = new LoginRequestViewModel();
    private final Set<Runnable> clearFields = new HashSet<>();

    private TextField emailTf;
    private PasswordField passPf;

    {
        viewModel.setLoginBinder(this);
    }

    public void withEmailTf(TextField emailTf) {

        this.emailTf = emailTf;
        emailTf.setPromptText("example@company.com");
        viewModel.emailProperty().bind(emailTf.textProperty());
        clearFields.add(() -> {
            emailTf.setText("");
            viewModel.emailProperty().unbind();
        });
    }

    public void withPasswordTf(PasswordField passPf) {

        this.passPf = passPf;
        viewModel.passwordProperty().bind(passPf.textProperty());
        clearFields.add(() -> {
            passPf.setText("");
            viewModel.passwordProperty().unbind();
        });
    }

    public void withLoginBtn(Button loginBtn) {
        loginBtn.setOnAction(evt -> login());
    }

    private void login() {

        if (!validate()) {
            addListeners();
            return;
        }
        viewModel.login();
    }

    private boolean validate() {

        boolean flag = true;

        if (emailTf.getText() == null || emailTf.getText().isBlank()) {
            emailTf.getStyleClass().add("loginBadElement");
            flag = false;
        }

        if (passPf.getText() == null || passPf.getText().isBlank()) {
            passPf.getStyleClass().add("loginBadElement");
            flag = false;
        }
        return flag;
    }

    private void addListeners() {
        emailTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                emailTf.getStyleClass().remove("loginBadElement");
            }
        });

        passPf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                passPf.getStyleClass().remove("loginBadElement");
            }
        });
    }

    public void clearFields() {
        clearFields.forEach(Runnable::run);
    }
}
