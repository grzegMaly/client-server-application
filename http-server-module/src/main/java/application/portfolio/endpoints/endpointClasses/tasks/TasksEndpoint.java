package application.portfolio.endpoints.endpointClasses.tasks;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

@EndpointInfo(path = "/tasks")
public class TasksEndpoint implements EndpointHandler, HttpHandler {

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
