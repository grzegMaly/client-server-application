package application.portfolio.endpoints.endpointClasses.notes.notesUtils;

import application.portfolio.requestResponse.Requests.PostRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;


public class NotesPostMethods {

    private static final Map<String, String> fData;

    static {
        fData = Infrastructure.getFileServerData();
    }

    public static void handlePost(HttpExchange exchange, Map<String, String> paramsMap) throws IOException {

        String params = DataParser.paramsString(paramsMap);
        String spec = Infrastructure.uriSpecificPart(fData, "notes", params);
        URI baseUri = Infrastructure.getBaseUri(fData).resolve(spec);
        PostRequests.handlePostRequest(exchange, baseUri);
    }
}
