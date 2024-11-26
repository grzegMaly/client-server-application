package application.portfolio.endpoints.endpointClasses.session.user.methods;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class DeleteUser {

    public static void handleDelete(HttpExchange exchange) {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        String idVal = paramsMap.get("id");

        if (idVal == null) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }
        String id = DataParser.paramsString(Map.of("id", idVal));

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "user", id);
        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);

        HttpResponse<byte[]> response = ClientHolder.sendDeleteRequest(baseUri);
        if (response == null) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
        ResponseHandler.sendResponse(response, exchange);
    }
}
