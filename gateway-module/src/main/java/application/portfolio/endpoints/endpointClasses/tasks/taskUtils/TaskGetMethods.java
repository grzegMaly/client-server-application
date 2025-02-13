package application.portfolio.endpoints.endpointClasses.tasks.taskUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.requestResponse.Requests.GetRequests;
import application.portfolio.requestResponse.ResponseHandler;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class TaskGetMethods {

    private static final Map<String, String> dData;

    static {
        dData = Infrastructure.getDatabaseData();
    }

    public static void handleGet(HttpExchange exchange, Map<String, String> paramsMap) {

        if (paramsMap.size() != 1) {
            ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
            return;
        }

        String params = DataParser.paramsString(paramsMap);
        String spec = Infrastructure.uriSpecificPart(dData, "tasks", params);

        URI baseUri = Infrastructure.getBaseUri(dData).resolve(spec);
        GetRequests.handleGetRequest(exchange, baseUri);
    }
}
