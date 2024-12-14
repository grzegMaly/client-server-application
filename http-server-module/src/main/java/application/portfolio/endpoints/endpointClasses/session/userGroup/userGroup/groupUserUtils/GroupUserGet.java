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

public class GroupUserGet {
    public static void handleGet(HttpExchange exchange, Map<String, String> paramsMap) throws IOException {

        if (paramsMap == null) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        String params = null;
        if (paramsMap.containsKey("userId")) {
            if (paramsMap.size() == 1 ||
                    (paramsMap.size() == 2 && paramsMap.containsKey("friends"))) {
                params = DataParser.paramsString(paramsMap);
            }
        } else if (paramsMap.containsKey("groupId")) {
            if (paramsMap.size() == 3 && paramsMap.containsKey("limit") &&
                    paramsMap.containsKey("offset")) {
                params = DataParser.paramsString(paramsMap);
            }
        }

        if (params == null) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "group/user", params);

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        ClientHolder.handleGetRequest(exchange, baseUri);
    }
}
