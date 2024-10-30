package application.portfolio.token;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.response.AuthTokenResponse;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.JsonBodyHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.*;

public class AuthService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonBodyHandler handler;

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        handler = JsonBodyHandler.create(objectMapper);
    }

    public AuthTokenResponse authorize(byte[] data) {
        JsonNode node = getDataFromDatabase(data);
        ObjectNode objectNode = objectMapper.createObjectNode();

        if (node != null) {
            String id = node.get("id").asText();
            UUID personId = UUID.fromString(id);
            AuthToken token = Tokens.createToken(personId);

            objectNode.set("response", node);
            return new AuthTokenResponse(objectNode, token);
        } else {
            objectNode.put("response", "Invalid Credentials");
            return new AuthTokenResponse(objectNode, HTTP_UNAUTHORIZED);
        }
    }

    private JsonNode getDataFromDatabase(byte[] data) {

        HttpClient client = ClientHolder.getClient();
        Map<String, String> authData = Infrastructure.getDatabaseData();
        URI baseUri = Infrastructure.getBaseUri(authData).resolve("/getUser");

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();

        client.sendAsync(request, handler)
                .thenApply(response -> {
                    if (response.statusCode() != HTTP_OK) {
                        return null;
                    }
                    return response.body();
                }).exceptionally(ex -> null);
        return null;
    }

    public AuthTokenResponse deactivate(String token, JsonNode node) {

        ObjectNode objectNode = objectMapper.createObjectNode();

        String id = node.get("id").asText();
        AuthToken authToken = Tokens.getToken(UUID.fromString(id));
        UUID tokenId = authToken.getToken();
        token = token.split(" ")[1];

        if (tokenId.equals(UUID.fromString(token))) {
            UUID personId = authToken.getUserId();
            Tokens.deleteToken(personId);

            objectNode.put("response", "Session Closed");
            return new AuthTokenResponse(objectNode, HTTP_OK);
        } else {
            objectNode.put("response", "Error");
            return new AuthTokenResponse(objectNode, HTTP_CONFLICT);
        }
    }
}