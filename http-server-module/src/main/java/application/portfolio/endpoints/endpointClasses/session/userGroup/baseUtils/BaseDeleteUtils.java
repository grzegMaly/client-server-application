package application.portfolio.endpoints.endpointClasses.session.userGroup.baseUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

public class BaseDeleteUtils {

    public static void baseDelete(HttpExchange exchange, Map<String, String> paramsMap,
                                  Map<String, String> serverData, String endpoint) {

        if (paramsMap == null || !paramsMap.containsKey("id")) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }
        String idVal = paramsMap.get("id");
        String id = DataParser.paramsString(Map.of("id", idVal));

        String spec = Infrastructure.uriSpecificPart(serverData, endpoint, id);

        URI baseUri = Infrastructure.getBaseUri(serverData).resolve(spec);
        ClientHolder.handleDeleteRequest(exchange, baseUri);
    }
}
