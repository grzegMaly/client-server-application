package application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils;

import application.portfolio.endpoints.endpointClasses.baseUtils.BaseDeleteUtils;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;


public class DeleteGroup {

    private static final String[] ID = {"id"};

    public static void handleDelete(HttpExchange exchange, Map<String, String> paramsMap) throws IOException {

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        if (DataParser.validateParams(paramsMap, ID)) {
            BaseDeleteUtils.baseDelete(exchange, paramsMap, dbData, "group");
        } else {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
        }
    }
}
