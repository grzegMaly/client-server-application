package application.portfolio.endpoints.endpointClasses.session.userGroup.user.userUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.*;

public class PostUser {

    public static void handlePost(HttpExchange exchange) throws IOException {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (paramsMap == null || !paramsMap.containsKey("option")) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        String spec;
        String option = paramsMap.get("option");
        Map<String, String> dbData = Infrastructure.getDatabaseData();
        if (option.equals("modify")) {
            spec = Infrastructure.uriSpecificPart(dbData, "user");
        } else if (option.equals("register")) {
            spec = Infrastructure.uriSpecificPart(dbData, "user/register");
        } else {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        ClientHolder.handlePostRequest(exchange, baseUri);
    }
}
