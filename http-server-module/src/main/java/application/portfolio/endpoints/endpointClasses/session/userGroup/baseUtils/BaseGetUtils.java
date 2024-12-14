package application.portfolio.endpoints.endpointClasses.session.userGroup.baseUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

public class BaseGetUtils {

    public static void handleBaseGet(HttpExchange exchange, Map<String, String> paramsMap,
                                     Map<String, String> serverData, String endpoint) {
        Map<String, String> map = DataParser.handleMap(paramsMap);
        String params = DataParser.paramsString(map);
        if (params == null) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        String spec = Infrastructure.uriSpecificPart(serverData, endpoint, params);

        URI baseUri = Infrastructure.getBaseUri(serverData).resolve(spec);
        ClientHolder.handleGetRequest(exchange, baseUri);
    }
}
