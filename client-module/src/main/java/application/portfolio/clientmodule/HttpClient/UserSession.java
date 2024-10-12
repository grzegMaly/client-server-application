package application.portfolio.clientmodule.HttpClient;

import application.portfolio.clientmodule.OtherElements.PersonDAO;

public class UserSession {

    private static UserSession instance;
    private PersonDAO loggedInUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    public void setLoggedInUser(PersonDAO user) {
        this.loggedInUser = user;
    }

    public PersonDAO getLoggedInUser() {
        return loggedInUser;
    }


    public void clearSession() {
        this.loggedInUser = null;
    }
}