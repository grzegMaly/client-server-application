package application.portfolio.endpoints.endpointClasses.notes;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.notes.NotesUtils.NotesDeleteMethods;
import application.portfolio.endpoints.endpointClasses.notes.NotesUtils.NotesGetMethods;
import application.portfolio.endpoints.endpointClasses.notes.NotesUtils.NotesPostMethods;
import application.portfolio.endpoints.endpointClasses.notes.NotesUtils.NotesPutMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/notes")
public class NotesEndpoint implements EndpointHandler, HttpHandler {

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {

            String method = exchange.getRequestMethod();
            Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());

            if (paramsMap == null || !paramsMap.containsKey("userId")) {
                ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
                return;
            }

            switch (method) {
                case "GET" -> {
                    Map.Entry<Integer, JsonNode> responseEntry = NotesGetMethods.handleGet(paramsMap);
                    ResponseHandler.sendResponse(exchange, responseEntry);
                }
                case "POST" -> {
                    Map.Entry<Integer, JsonNode> responseEntry = NotesPostMethods.handlePost(exchange, paramsMap);
                    ResponseHandler.sendResponse(exchange, responseEntry);
                }
                case "PUT" -> {
                    Map.Entry<Integer, JsonNode> responseEntry = NotesPutMethods.handlePut(exchange, paramsMap);
                    ResponseHandler.sendResponse(exchange, responseEntry);
                }
                case "DELETE" -> {
                    Map.Entry<Integer, JsonNode> responseEntry = NotesDeleteMethods.handleDelete(paramsMap);
                    ResponseHandler.sendResponse(exchange, responseEntry);
                }
                default -> ResponseHandler.handleError(exchange, "Bad Method", HTTP_BAD_METHOD);
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
