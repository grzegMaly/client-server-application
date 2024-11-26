package application.portfolio.endpoints.endpointClasses.session.user.methods;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class PostUser {

    public static void handlePost(HttpExchange exchange) throws IOException {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        Map<String, String> dbData = Infrastructure.getDatabaseData();

        String spec;
        if (paramsMap == null) {
            spec = Infrastructure.uriSpecificPart(dbData, "user/register");
        } else if (paramsMap.containsKey("id")) {
            String param = DataParser.paramsString(paramsMap);
            spec = Infrastructure.uriSpecificPart(dbData, "user", param);
        } else {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        byte[] data = exchange.getRequestBody().readAllBytes();
        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);

        HttpResponse<byte[]> response = ClientHolder.sendPostRequest(baseUri, data);
        if (response == null) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
        ResponseHandler.sendResponse(response, exchange);
    }
}
