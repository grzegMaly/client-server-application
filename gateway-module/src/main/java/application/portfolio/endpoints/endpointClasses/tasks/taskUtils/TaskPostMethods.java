package application.portfolio.endpoints.endpointClasses.tasks.taskUtils;

import application.portfolio.requestResponse.Requests.PostRequests;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class TaskPostMethods {

    private static final Map<String, String> dData;

    static {
        dData = Infrastructure.getDatabaseData();
    }

    public static void handlePost(HttpExchange exchange) throws IOException {

        String spec = Infrastructure.uriSpecificPart(dData, "tasks");
        URI baseUri = Infrastructure.getBaseUri(dData).resolve(spec);
        PostRequests.handlePostRequest(exchange, baseUri);
    }
}
