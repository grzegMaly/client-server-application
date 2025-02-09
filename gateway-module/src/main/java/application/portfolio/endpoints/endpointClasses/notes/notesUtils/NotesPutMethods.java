package application.portfolio.endpoints.endpointClasses.notes.notesUtils;

import application.portfolio.requestResponse.Requests.DeleteRequests;
import application.portfolio.requestResponse.Requests.PutRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;


public class NotesPutMethods {

    private static final Map<String, String> fData;

    static {
        fData = Infrastructure.getFileServerData();
    }

    public static void handlePut(HttpExchange exchange, Map<String, String> paramsMap) throws IOException {

            String params = DataParser.paramsString(paramsMap);
            String spec = Infrastructure.uriSpecificPart(fData, "notes", params);

            URI baseUri = Infrastructure.getBaseUri(fData).resolve(spec);
            PutRequests.handlePutRequest(exchange, baseUri);
    }
}
