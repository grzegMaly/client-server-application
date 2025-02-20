package application.portfolio.endpoints.endpointClasses.session.userGroup.group;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils.DeleteGroup;
import application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils.GetGroup;
import application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils.PostGroup;
import application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils.PutGroup;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

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

            switch (method) {
                case "GET" -> GetGroup.handleGet(exchange);
                case "POST" -> PostGroup.handlePost(exchange);
                case "PUT" -> PutGroup.handlePut(exchange);
                case "DELETE" -> DeleteGroup.handleDelete(exchange);
                default -> ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
            }
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
