package application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils;

import application.portfolio.endpoints.endpointClasses.session.userGroup.baseUtils.BaseGetUtils;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;


public class GetGroup {
    public static void handleGet(HttpExchange exchange, Map<String, String> paramsMap) throws IOException {

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        Map<String, String> map = DataParser.handleMap(paramsMap);
        BaseGetUtils.handleBaseGet(exchange, map, dbData, "group");

    }
}
