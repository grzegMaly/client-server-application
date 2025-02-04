package application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils;

import application.portfolio.requestResponse.Requests.PostRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

public class PostGroup {
    public static void handlePost(HttpExchange exchange, Map<String, String> paramsMap) throws IOException {

        if (paramsMap == null || !paramsMap.containsKey("option") || paramsMap.size() != 1) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        String option = paramsMap.get("option");
        if (!option.equals("modify") && !option.equals("add")) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        String params = DataParser.paramsString(paramsMap);
        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "group", params);

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        PostRequests.handlePostRequest(exchange, baseUri);
    }
}
