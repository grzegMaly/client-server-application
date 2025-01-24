package application.portfolio.endpoints.endpointClasses.chat;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.requestResponse.Requests.GetRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@EndpointInfo(path = "/chat/history")
public class ChatHistoryEndpoint implements EndpointHandler, HttpHandler {

    private final Map<String, String> dbData;

    {
        dbData = Infrastructure.getDatabaseData();
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        try (exchange) {
            if ("GET".equals(exchange.getRequestMethod())) {
                if (!DataParser.validateParams(paramsMap, "senderId", "receiverId", "offset", "limit")) {
                    ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
                    return;
                }
                handleGet(exchange, paramsMap);
            } else {
                ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private void handleGet(HttpExchange exchange, Map<String, String> paramsMap) {

        String params = DataParser.paramsString(paramsMap);
        String spec = Infrastructure.uriSpecificPart(dbData, "chat/history", params);

        URI uri = Infrastructure.getBaseUri(dbData).resolve(spec);
        GetRequests.handleGetRequest(exchange, uri);
    }
}
