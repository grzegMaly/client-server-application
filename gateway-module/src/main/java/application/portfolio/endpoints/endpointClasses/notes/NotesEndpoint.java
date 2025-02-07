package application.portfolio.endpoints.endpointClasses.notes;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.notes.notesUtils.NotesDeleteMethods;
import application.portfolio.endpoints.endpointClasses.notes.notesUtils.NotesGetMethods;
import application.portfolio.endpoints.endpointClasses.notes.notesUtils.NotesPostMethods;
import application.portfolio.endpoints.endpointClasses.notes.notesUtils.NotesPutMethods;
import application.portfolio.requestResponse.ResponseHandler;
import application.portfolio.utils.DataParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;


@EndpointInfo(path = "/notes")
public class NotesEndpoint implements EndpointHandler, HttpHandler {

    private final String USER_ID_KEY;

    {
        USER_ID_KEY = "userId";
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            String method = exchange.getRequestMethod();
            Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
            if (!paramsMap.containsKey(USER_ID_KEY)) {
                ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
            } else {
                if ("GET".equals(method)) {
                    NotesGetMethods.handleGet(exchange, paramsMap);
                } else if ("POST".equals(method)) {
                    NotesPostMethods.handlePost(exchange);
                } else if (notesIdValidation(paramsMap)) {
                    switch (method) {
                        case "PUT" -> NotesPutMethods.handlePut(exchange, paramsMap);
                        case "DELETE" -> NotesDeleteMethods.handleDelete(exchange, paramsMap);
                    }
                }
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static boolean notesIdValidation(Map<String, String> paramsMap) {
        return paramsMap.size() == 2 && paramsMap.containsKey("noteId");
    }
}
