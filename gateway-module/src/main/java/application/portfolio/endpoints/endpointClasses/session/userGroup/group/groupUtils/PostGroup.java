package application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils;

import application.portfolio.requestResponse.Requests.PostRequests;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class PostGroup {
    public static void handlePost(HttpExchange exchange) throws IOException {

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "group");

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        PostRequests.handlePostRequest(exchange, baseUri);
    }
}
