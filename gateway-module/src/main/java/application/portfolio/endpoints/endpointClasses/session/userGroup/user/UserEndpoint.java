package application.portfolio.endpoints.endpointClasses.session.userGroup.user;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.session.userGroup.user.userUtils.DeleteUser;
import application.portfolio.endpoints.endpointClasses.session.userGroup.user.userUtils.PostUser;
import application.portfolio.endpoints.endpointClasses.session.userGroup.user.userUtils.PutUser;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_BAD_GATEWAY;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@EndpointInfo(path = "/user")
public class  UserEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            if ("POST".equals(method)) {
                PostUser.handlePost(exchange);
            } else if ("PUT".equals(method)) {
                PutUser.handlePut(exchange);
            }else if ("DELETE".equals(method)) {
                DeleteUser.handleDelete(exchange);
            } else {
                ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
            }
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
