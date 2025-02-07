package application.portfolio.endpoints.endpointClasses.notes.notesUtils;

import application.portfolio.requestResponse.Requests.GetRequests;
import application.portfolio.requestResponse.ResponseHandler;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class NotesGetMethods {

    private static final String NOTE_ID_KEY;
    private static final String OPTION;
    private static final Map<String, String> fData;

    static {
        NOTE_ID_KEY = "noteId";
        OPTION = "option";
        fData = Infrastructure.getFileServerData();
    }

    public static void handleGet(HttpExchange exchange, Map<String, String> paramsMap) {

        if (!paramsMap.containsKey(OPTION)) {
            ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
            return;
        }

        String option = paramsMap.get(OPTION);
        String spec = switch (option) {
            case "list-all" -> handleList(paramsMap);
            case "content" -> handleContent(paramsMap);
            default -> null;
        };

        if (spec != null) {
            URI baseUri = Infrastructure.getBaseUri(fData).resolve(spec);
            GetRequests.handleGetRequest(exchange, baseUri);
        } else {
            ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
        }
    }

    private static String handleList(Map<String, String> paramsMap) {
        if (paramsMap.size() == 2) {
            paramsMap.remove(OPTION);
            String params = DataParser.paramsString(paramsMap);
            return Infrastructure.uriSpecificPart(fData, "notes/list", params);
        } else {
            return null;
        }
    }

    private static String handleContent(Map<String, String> paramsMap) {
        if (paramsMap.containsKey(NOTE_ID_KEY) && paramsMap.size() == 3) {
            String params = DataParser.paramsString(paramsMap);
            return Infrastructure.uriSpecificPart(fData, "notes", params);
        } else {
            return null;
        }
    }
}
