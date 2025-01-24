package application.portfolio.endpoints.endpointClasses.session;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.ResponseHandler;
import application.portfolio.utils.UserUtils.UserResourcesManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/user-resource/create")
public class AddUserResource implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            if ("POST".equals(exchange.getRequestMethod())) {
                UserResourcesManager.handleNewUsers(exchange);
                ResponseHandler.sendResponse(exchange, HTTP_OK);
            } else {
                ResponseHandler.handleError(exchange, "Bad Method", HTTP_BAD_REQUEST);
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
