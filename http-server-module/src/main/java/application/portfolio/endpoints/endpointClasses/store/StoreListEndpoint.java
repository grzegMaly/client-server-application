package application.portfolio.endpoints.endpointClasses.store;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.baseUtils.BaseGetUtils;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/store/list")
public class StoreListEndpoint implements EndpointHandler, HttpHandler {

    private final String[] VIEW_PARAMS = {"userId", "path", "limit", "offset"};
    private final Map<String, String> fData;

    {
        fData = Infrastructure.getFileServerData();
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {

                Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
                if (!DataParser.validateParams(paramsMap, VIEW_PARAMS)) {
                    ResponseHandler.handleError(exchange, "Bad Params", HTTP_FORBIDDEN);
                } else {
                    BaseGetUtils.handleBaseGet(exchange, paramsMap, fData, "store/list");
                }
            } else {
                ResponseHandler.handleError(exchange, "Bad Method", HTTP_BAD_REQUEST);
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
