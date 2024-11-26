package application.portfolio.endpoints.endpointClasses.session.user.userUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/user/get")
public class GetUser implements EndpointHandler, HttpHandler {

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if ("GET".equals(exchange.getRequestMethod())) {

                Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
                if (paramsMap == null) {
                    paramsMap = Map.of("limit", "10", "offset", "0");
                }

                String params = DataParser.paramsString(paramsMap);
                if (params == null) {
                    ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
                    return;
                }

                Map<String, String> dbData = Infrastructure.getDatabaseData();
                String spec = Infrastructure.uriSpecificPart(dbData, "user", params);
                URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);

                HttpResponse<byte[]> response = ClientHolder.sendGetRequest(baseUri);
                if (response == null) {
                    ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
                }
                ResponseHandler.sendResponse(response, exchange);
            } else {
                ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
            }
            throw new IOException();
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
