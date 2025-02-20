package application.portfolio.endpoints.endpointClasses.session;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import application.portfolio.utils.UserUtils.UserResourcesManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;


@EndpointInfo(path = "/user-resource/remove")
public class RemoveUserResource implements EndpointHandler, HttpHandler {

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {

        try (exchange) {
            if (!"DELETE".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Bad Method", HTTP_BAD_METHOD);
                return;
            }

            System.out.println("DUPA");
            Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
            if (paramsMap == null || !paramsMap.containsKey("userId") || paramsMap.size() != 1) {
                ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
                return;
            }
            UserResourcesManager.handleUserResourceDelete(paramsMap);
        }
    }
}
