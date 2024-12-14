package application.portfolio.clientmodule.Model.Request.Login;

import application.portfolio.clientmodule.Model.Request.Login.LoginRequest.LoginRequest;

import java.util.Map;

public class LoginRequestConverter {
    public LoginRequest toLoginRequest(Map<String, String> map) {
        String email = map.get("email");
        String password = map.get("password");
        return new LoginRequest(email, password);
    }
}
