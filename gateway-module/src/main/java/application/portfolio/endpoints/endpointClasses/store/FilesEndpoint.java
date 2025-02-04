package application.portfolio.endpoints.endpointClasses.store;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.store.StoreUtils.StoreDeleteMethods;
import application.portfolio.endpoints.endpointClasses.store.StoreUtils.StoreGetMethods;
import application.portfolio.endpoints.endpointClasses.store.StoreUtils.StorePostMethods;
import application.portfolio.utils.DataParser;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/store/resource")
public class FilesEndpoint implements EndpointHandler, HttpHandler {

    private static final String[] RESOURCE_PARAMS = {"userId", "path"};

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {

        String method = exchange.getRequestMethod();
        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (!DataParser.validateParams(paramsMap, RESOURCE_PARAMS)) {
            ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
            return;
        }

        try (exchange) {
            switch (method) {
                case "GET" -> StoreGetMethods.handleDownload(exchange, paramsMap);
                case "POST" -> StorePostMethods.handleUpload(exchange, paramsMap);
                case "DELETE" -> StoreDeleteMethods.handleDelete(exchange, paramsMap);
                default -> ResponseHandler.handleError(exchange, "Bad Method", HTTP_BAD_METHOD);
            }
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
