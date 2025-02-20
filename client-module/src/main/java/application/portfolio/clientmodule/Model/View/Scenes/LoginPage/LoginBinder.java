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

    public void withEmailTf(TextField emailTf) {

        this.emailTf = emailTf;
        emailTf.setPromptText("example@company.com");
        emailTf.setText("MiTy@teamLink.com");
        viewModel.emailProperty().bind(emailTf.textProperty());
        clearFields.add(() -> {
            emailTf.setText(null);
            viewModel.emailProperty().unbind();
        });
    }

    public void withPasswordTf(PasswordField passPf) {

        this.passPf = passPf;
        passPf.setText("Password123@");
        viewModel.passwordProperty().bind(passPf.textProperty());
        clearFields.add(() -> {
            passPf.setText(null);
            viewModel.passwordProperty().unbind();
        });
    }

    public void withLoginBtn(Button loginBtn) {
        loginBtn.setOnAction(evt -> login());
    }

    private void login() {

        if (!validate()) {
            //addListeners();
            return;
        }
        viewModel.login();
    }

    private boolean validate() {

        boolean flag = true;

        if (emailTf.getText() == null || emailTf.getText().isBlank()) {
//            emailTf.getStyleClass().add("badElement");
            flag = false;
        }

        if (passPf.getText() == null || passPf.getText().isBlank()) {
//            passPf.getStyleClass().add("badElement");
            flag = false;
        }

        return flag;
    }

    private void addListeners() {
        emailTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.isBlank()) {
//                titleTf.getStyleClass().remove("badElement");
            }
        });

        passPf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.isBlank()) {
//                contentTa.getStyleClass().remove("badElement");
            }
        });

    }

    public void clearFields() {
        clearFields.forEach(Runnable::run);
    }
}
