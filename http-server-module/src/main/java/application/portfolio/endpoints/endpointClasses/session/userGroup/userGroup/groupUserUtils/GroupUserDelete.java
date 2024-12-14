package application.portfolio.endpoints.endpointClasses.session.userGroup.userGroup.groupUserUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

public class GroupUserDelete {
    public static void handleDelete(HttpExchange exchange, Map<String, String> paramsMap) throws IOException {

        if (paramsMap.size() != 2) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String paramsString = DataParser.paramsString(paramsMap);
        String spec = Infrastructure.uriSpecificPart(dbData, "group/user", paramsString);

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        ClientHolder.handleDeleteRequest(exchange, baseUri);
    }
}
