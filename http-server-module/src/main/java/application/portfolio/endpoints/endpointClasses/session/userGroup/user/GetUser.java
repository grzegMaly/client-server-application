package application.portfolio.endpoints.endpointClasses.session.userGroup.user;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.session.userGroup.baseUtils.BaseGetUtils;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
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

                Map<String, String> dbData = Infrastructure.getDatabaseData();
                Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
                Map<String, String> map = DataParser.handleMap(paramsMap);

                BaseGetUtils.handleBaseGet(exchange, map, dbData, "user");
            } else {
                ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
            }
            throw new IOException();
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
