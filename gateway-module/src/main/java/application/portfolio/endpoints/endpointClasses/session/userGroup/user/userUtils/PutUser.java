package application.portfolio.endpoints.endpointClasses.session.userGroup.user.userUtils;

import application.portfolio.requestResponse.Requests.PutRequests;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;


public class PutUser {

    public static void handlePut(HttpExchange exchange) throws IOException {

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "user");

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        PutRequests.handlePutRequest(exchange, baseUri);
    }
}
