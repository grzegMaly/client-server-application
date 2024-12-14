package application.portfolio.endpoints.endpointClasses.session.userGroup.userGroup;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.session.userGroup.userGroup.groupUserUtils.GroupUserDelete;
import application.portfolio.endpoints.endpointClasses.session.userGroup.userGroup.groupUserUtils.GroupUserGet;
import application.portfolio.endpoints.endpointClasses.session.userGroup.userGroup.groupUserUtils.GroupUserPost;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/group/user")
public class GroupUserEndpoint implements EndpointHandler, HttpHandler {
    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange){

        try (exchange) {

            String method = exchange.getRequestMethod();
            Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
            if (paramsMap == null) {
                if ("POST".equals(method)) {
                    GroupUserPost.handlePost(exchange);
                } else {
                    ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
                }
            } else {
                switch (method) {
                    case "GET" -> GroupUserGet.handleGet(exchange, paramsMap);
                    case "DELETE" -> {
                        if (paramsMap.containsKey("userId") && paramsMap.containsKey("groupId")) {
                            GroupUserDelete.handleDelete(exchange, paramsMap);
                        } else {
                            ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
                        }
                    }
                    default -> ResponseHandler.handleError(exchange, "Bad Data", HTTP_BAD_REQUEST);
                }
            }
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
