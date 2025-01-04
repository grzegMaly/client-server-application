package application.portfolio.endpoints.endpointClasses.chat;

import application.portfolio.clientServer.response.MessageResponse;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.chat.chatUtils.ChatGetMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

@EndpointInfo(path = "/chat/history")
public class ChatHistoryEndpoint implements EndpointHandler, HttpHandler {

    private static final List<String> HISTORY_PARAMS = List.of("senderId", "receiverId", "offset", "limit");

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Bad Request", HTTP_BAD_REQUEST);
            }

            Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
            MessageResponse messageResponse = handleGetHistory(paramsMap);
            Map.Entry<Integer, JsonNode> entry = messageResponse.toJsonResponse();
            ResponseHandler.sendResponse(exchange, entry);
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private MessageResponse handleGetHistory(Map<String, String> paramsMap) {
        if (!DataParser.validateParams(paramsMap, HISTORY_PARAMS.toArray(new String[0]))) {
            return new MessageResponse("Bad Data", HTTP_FORBIDDEN);
        }

        int offset, limit;
        try {
            offset = Integer.parseInt(paramsMap.get("offset"));
            limit = Integer.parseInt(paramsMap.get("limit"));
            if (offset < 0 || limit <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            return new MessageResponse("Bad Params", HTTP_FORBIDDEN);
        }

        if (limit > 50) {
            limit = 10;
        }

        UUID senderId = DataParser.parseId(paramsMap.get("senderId"));
        UUID receiverId = DataParser.parseId(paramsMap.get("receiverId"));

        if (senderId == null || receiverId == null) {
            return new MessageResponse("Bad Data", HTTP_FORBIDDEN);
        }

        return ChatGetMethods.getChatHistoryFromDB(senderId, receiverId, offset, limit);
    }
}
