package application.portfolio.endpoints.endpointClasses.session.userGroup.userGroup;

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
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/group/user/move")
public class GroupUserMoveEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange){
        try (exchange) {
            if (!"POST".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Bad Method", HTTP_BAD_METHOD);
                return;
            }
            handlePostMove(exchange);
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private void handlePostMove(HttpExchange exchange) throws IOException {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (paramsMap != null) {
            ResponseHandler.handleError(exchange, "Bad Request", HTTP_BAD_REQUEST);
            return;
        }

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "group/user/move");

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        ClientHolder.handlePostRequest(exchange, baseUri);
    }
}
