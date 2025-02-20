package application.portfolio.endpoints.endpointClasses.session.userGroup.group.groupUtils;

import application.portfolio.endpoints.endpointClasses.baseUtils.BaseGetUtils;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;


public class GetGroup {

    private static final String[] ID = {"id"};
    private static final String[] LIMIT_OFFSET = {"limit", "offset"};
    private static final String[] ALL = {"all"};
    private static final Map<String, String> dbData;

    static {
        dbData = Infrastructure.getDatabaseData();
    }

    public static void handleGet(HttpExchange exchange) throws IOException {

        Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
        if (paramsMap == null) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
            return;
        }

        if (!DataParser.validateParams(paramsMap, ID)
                && !DataParser.validateParams(paramsMap, LIMIT_OFFSET)
                && !DataParser.validateParams(paramsMap, ALL)) {
            ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
        } else {
            BaseGetUtils.handleBaseGet(exchange, paramsMap, dbData, "group");
        }
    }
}
