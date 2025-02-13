package application.portfolio.endpoints.endpointClasses.tasks.taskUtils;

import application.portfolio.requestResponse.Requests.DeleteRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Map;

public class TaskDeleteMethods {

    private static final Map<String, String> dData;

    static {
        dData = Infrastructure.getDatabaseData();
    }

    public static void handleDelete(HttpExchange exchange, Map<String, String> paramsMap) {

        String params = DataParser.paramsString(paramsMap);
        String spec = Infrastructure.uriSpecificPart(dData, "tasks", params);

        URI baseUri = Infrastructure.getBaseUri(dData).resolve(spec);
        DeleteRequests.handleDeleteRequest(exchange, baseUri);
    }
}
