package application.portfolio.clientmodule.Model.Request.Login;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Login.LoginRequest.LoginRequest;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.CustomAlert;
import application.portfolio.clientmodule.utils.PageFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ButtonType;

import java.util.Map;


public class LoginRequestViewModel {

    private final SimpleStringProperty email = new SimpleStringProperty();
    private final SimpleStringProperty password = new SimpleStringProperty();

    private final LoginRequestConverter converter = new LoginRequestConverter();
    private final LoginRequestModel model = new LoginRequestModel();

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void login() {

        Map<String, String> map = Map.of("email", getEmail(), "password", getPassword());
        LoginRequest data = converter.toLoginRequest(map);

        PersonDAO person = model.login(data);

        if (person != null) {
            UserSession.getInstance().setLoggedInUser(person);
            Page mainScene = PageFactory.getInstance(MainScene.class);
            TeamLinkApp.useScene(mainScene.getClass());
        } else {
            String message = "Unknown error";
            CustomAlert alert = new CustomAlert("", message);
            alert.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    alert.close();
                }
            });
        }
    }
}
