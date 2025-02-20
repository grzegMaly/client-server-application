package application.portfolio.clientmodule.Connection;

import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequestViewModel;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static java.net.HttpURLConnection.HTTP_OK;

public class UserSession {

    private static UserSession instance;
    private static Person loggedInUser;
    private String token;
    private static final Map<UUID, Person> personObjects = new HashMap<>();

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    public static void loadFriends() {
        List<Person> friends = FriendsRequestViewModel.loadFriends();
        friends.forEach(f -> personObjects.put(f.getUserId(), f));
    }

    public void setLoggedInUser(Person user) {
        loggedInUser = user;
    }

    public Person getLoggedInUser() {
        return loggedInUser;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void clearSession() {
        loggedInUser = null;
    }

    public static Boolean ping() {

        HttpClient client = ClientHolder.getClient();
        Map<String, String> gData = Infrastructure.getGatewayData();
        String spec = Infrastructure.uriSpecificPart(gData, "ping");
        URI baseUri = Infrastructure.getBaseUri(spec);

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

    public static List<Person> getPersonObjects() {
        return new ArrayList<>(personObjects.values());
    }

    public static Person getPerson(UUID personId) {
        return personObjects.get(personId);
    }

    public static void addPerson(Person personDAO) {
        personObjects.put(personDAO.getUserId(), personDAO);
    }

    public static List<Person> getUsersFromGroups() {
        return getPersonObjects().stream()
                .filter(p -> p.getRole().getId() < loggedInUser.getRole().getId())
                .toList();
    }
}