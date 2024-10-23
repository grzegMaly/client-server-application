package application.portfolio.endpoints.endpointClasses;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@EndpointInfo(path = "/ping")
public class PingEndpoint implements HttpHandler, EndpointHandler {

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("GET".equals(exchange.getRequestMethod())) {
            byte[] response  = "pong".getBytes();
            exchange.sendResponseHeaders(HTTP_OK, response.length);
            exchange.getResponseBody().write(response);
        } else {
            exchange.sendResponseHeaders(HTTP_BAD_REQUEST, -1);
        }
        exchange.close();
    }
}
