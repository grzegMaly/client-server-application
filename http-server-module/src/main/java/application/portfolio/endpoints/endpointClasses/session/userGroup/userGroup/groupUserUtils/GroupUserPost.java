package application.portfolio.endpoints.endpointClasses.session.userGroup.userGroup.groupUserUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;


public class GroupUserPost {
    public static void handlePost(HttpExchange exchange) throws IOException {

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "group/user");

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        ClientHolder.handlePostRequest(exchange, baseUri);
    }
}
