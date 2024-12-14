package application.portfolio.endpoints.endpointClasses.session.userGroup.group;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils.DeleteGroup;
import application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils.GetGroup;
import application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils.PostGroup;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/group")
public class GroupEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            String method = exchange.getRequestMethod();

            Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
            if (paramsMap == null || !paramsMap.containsKey("option")) {
                ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
                return;
            }

            if ("GET".equals(method)) {
                GetGroup.handleGet(exchange, paramsMap);
            } else if ("POST".equals(method)) {
                PostGroup.handlePost(exchange, paramsMap);
            } else if ("DELETE".equals(method)) {
                DeleteGroup.handleDelete(exchange, paramsMap);
            } else {
                ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
            }
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
