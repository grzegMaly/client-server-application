package application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils;

import application.portfolio.endpoints.endpointClasses.session.userGroup.baseUtils.BaseDeleteUtils;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;


public class DeleteGroup {
    public static void handleDelete(HttpExchange exchange, Map<String, String> paramsMap) throws IOException {

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        BaseDeleteUtils.baseDelete(exchange, paramsMap, dbData, "group");
    }
}
