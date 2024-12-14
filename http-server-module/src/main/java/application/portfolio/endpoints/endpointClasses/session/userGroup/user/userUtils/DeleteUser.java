package application.portfolio.endpoints.endpointClasses.session.userGroup.user.userUtils;

import application.portfolio.endpoints.endpointClasses.session.userGroup.baseUtils.BaseDeleteUtils;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;


public class DeleteUser {

    public static void handleDelete(HttpExchange exchange) {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        Map<String, String> dbData = Infrastructure.getDatabaseData();
        BaseDeleteUtils.baseDelete(exchange, paramsMap, dbData, "user");
    }
}
