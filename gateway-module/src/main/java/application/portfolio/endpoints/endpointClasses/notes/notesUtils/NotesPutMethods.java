package application.portfolio.endpoints.endpointClasses.notes.notesUtils;

import application.portfolio.endpoints.endpointClasses.notes.NotesEndpoint;
import application.portfolio.requestResponse.Requests.DeleteRequests;
import application.portfolio.requestResponse.ResponseHandler;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class NotesPutMethods {

    private static final Map<String, String> fData;

    static {
        fData = Infrastructure.getFileServerData();
    }

    public static void handlePut(HttpExchange exchange, Map<String, String> paramsMap) {

        if (NotesEndpoint.notesIdValidation(paramsMap)) {
            String params = DataParser.paramsString(paramsMap);
            String spec = Infrastructure.uriSpecificPart(fData, "notes", params);

            URI baseUri = Infrastructure.getBaseUri(fData).resolve(spec);
            DeleteRequests.handleDeleteRequest(exchange, baseUri);
        } else {
            ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
        }
    }
}
