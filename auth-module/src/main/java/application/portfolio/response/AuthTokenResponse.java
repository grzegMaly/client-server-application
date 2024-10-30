package application.portfolio.response;

import application.portfolio.token.AuthToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static java.net.HttpURLConnection.HTTP_OK;

public class AuthTokenResponse {

    private final JsonNode responseNode;
    private final AuthToken authToken;
    private final int statusCode;

    public AuthTokenResponse(JsonNode responseNode, AuthToken authToken) {
        this.responseNode = responseNode;
        this.authToken = authToken;
        this.statusCode = HTTP_OK;
    }

    public AuthTokenResponse(String message, int statusCode) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("response", message);

        this.responseNode = objectNode;
        this.authToken = null;
        this.statusCode = statusCode;
    }

    public AuthTokenResponse(JsonNode responseNode, int statusCode) {
        this.responseNode = responseNode;
        this.authToken = null;
        this.statusCode = statusCode;
    }

    public JsonNode getResponseNode() {
        return responseNode;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
