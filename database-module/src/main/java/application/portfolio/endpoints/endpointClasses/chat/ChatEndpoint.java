package application.portfolio.endpoints.endpointClasses.chat;

import application.portfolio.clientServer.response.MessageResponse;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.chat.chatUtils.ChatPostMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@EndpointInfo(path = "/chat")
public class ChatEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            if (!"POST".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Bad Request", HTTP_BAD_REQUEST);
                return;
            }
            handlePost(exchange);
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (paramsMap != null && !paramsMap.isEmpty()) {
            ResponseHandler.handleError(exchange, "Unexpected Params", HTTP_BAD_REQUEST);
            return;
        }

        byte[] data = exchange.getRequestBody().readAllBytes();
        MessageResponse messageResponse = ChatPostMethods.sendMessage(data);
        Map.Entry<Integer, JsonNode> entry = messageResponse.toJsonResponse();
        ResponseHandler.sendResponse(exchange, entry);
    }
}
