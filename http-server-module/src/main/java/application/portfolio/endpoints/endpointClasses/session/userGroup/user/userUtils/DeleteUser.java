package application.portfolio.endpoints.endpointClasses.session.userGroup.user.userUtils;

import application.portfolio.endpoints.endpointClasses.baseUtils.BaseDeleteUtils;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;


public class DeleteUser {

    private static final String[] ID = {"id"};

    public static void handleDelete(HttpExchange exchange) {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (DataParser.validateParams(paramsMap, ID)) {
            Map<String, String> dbData = Infrastructure.getDatabaseData();
            BaseDeleteUtils.baseDelete(exchange, paramsMap, dbData, "user");
        } else {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
        }
    }
}
