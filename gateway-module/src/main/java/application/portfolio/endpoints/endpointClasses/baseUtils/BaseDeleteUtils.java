package application.portfolio.endpoints.endpointClasses.baseUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.requestResponse.Requests.DeleteRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Map;


public class BaseDeleteUtils {

    public static void baseDelete(HttpExchange exchange, Map<String, String> paramsMap,
                                  Map<String, String> serverData, String endpoint) {

        String params = DataParser.paramsString(paramsMap);
        String spec = Infrastructure.uriSpecificPart(serverData, endpoint, params);

        URI baseUri = Infrastructure.getBaseUri(serverData).resolve(spec);
        DeleteRequests.handleDeleteRequest(exchange, baseUri);
    }
}
