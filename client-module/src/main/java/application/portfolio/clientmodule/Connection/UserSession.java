package application.portfolio.clientmodule.Connection;

import application.portfolio.clientmodule.OtherElements.PersonDAO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class UserSession {

    private static UserSession instance;
    private PersonDAO loggedInUser;
    private String token;

    private UserSession() {
    }

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

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void clearSession() {
        this.loggedInUser = null;
    }

    public static Boolean ping() {

        HttpClient client = ClientHolder.getClient();
        Map<String, String> gData = Infrastructure.getGatewayData();
        String spec = Infrastructure.uriSpecificPart(gData, "ping");
        URI baseUri = Infrastructure.getBaseUri(gData).resolve(spec);

        HttpResponse<String> response;

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .GET()
                .build();

        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            return response != null && response.statusCode() == HTTP_OK;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}