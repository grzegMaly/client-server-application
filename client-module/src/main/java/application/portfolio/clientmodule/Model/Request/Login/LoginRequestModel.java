package application.portfolio.clientmodule.Model.Request.Login;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Login.LoginRequest.LoginRequest;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.utils.CustomAlert;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class LoginRequestModel {

    public PersonDAO login(LoginRequest req) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        HttpClient client = ClientHolder.getClient();
        Map<String, String> gData = Infrastructure.getGatewayData();
        String loginEndpoint = Infrastructure.getLoginEndpoint();
        URI baseUri = Infrastructure.getBaseUri(gData).resolve(loginEndpoint);
        JsonBodyHandler handler = JsonBodyHandler.create(objectMapper);

        JsonNode node = objectMapper.valueToTree(req);
        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(node);
        } catch (JsonProcessingException e) {
            return null;
        }

        HttpResponse<JsonNode> response;
        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .version(HttpClient.Version.HTTP_2)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return handleResponse(response);
    }

    private PersonDAO handleResponse(HttpResponse<JsonNode> response) {

        JsonNode node = response.body();

        int status = response.statusCode();

        if (status != HTTP_OK) {
            String title = "";

            if (status == HTTP_UNAUTHORIZED) {
                title = "Unauthorized";
            }
            String responseText = node.get("response").asText();
            CustomAlert alert = new CustomAlert(title, responseText);
            alert.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    alert.close();
                }
            });
            return null;
        }

        PersonDAO personDAO = parseJsonToPerson(node);
        if (personDAO != null) {
            String token = response.headers().map().get("Authorization").get(0);
            UserSession.getInstance().setToken(token);
        }

        return personDAO;
    }

    private PersonDAO parseJsonToPerson(JsonNode node) {

        node = node.get("response");

        try {
            String id = node.get("id").asText();
            String firstName = node.get("firstName").asText();
            String lastName = node.get("lastName").asText();
            int role = node.get("role").asInt();

            return new PersonDAO(id, firstName, lastName, role);
        } catch (Exception e) {
            return null;
        }
    }
}
