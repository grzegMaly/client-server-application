package application.portfolio.endpoints.endpointClasses.session.userGroup.user;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.baseUtils.BaseGetUtils;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/user/get")
public class GetUser implements EndpointHandler, HttpHandler {

    private final String[] ID = {"id"};
    private final String[] LIMIT_OFFSET = {"limit", "offset"};
    private final String[] ALL = {"all"};

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if ("GET".equals(exchange.getRequestMethod())) {

                Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
                if (!DataParser.validateParams(paramsMap, LIMIT_OFFSET) &&
                        !DataParser.validateParams(paramsMap, ID) && !DataParser.validateParams(paramsMap, ALL)) {
                    ResponseHandler.handleError(exchange, "Bad Params", HTTP_FORBIDDEN);
                    return;
                }
                Map<String, String> dbData = Infrastructure.getDatabaseData();
                BaseGetUtils.handleBaseGet(exchange, paramsMap, dbData, "user");
            } else {
                ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
            }
            throw new IOException();
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
