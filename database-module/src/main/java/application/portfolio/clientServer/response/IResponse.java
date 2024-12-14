package application.portfolio.clientServer.response;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface IResponse {
    Map.Entry<Integer, JsonNode> toJsonResponse();
}
