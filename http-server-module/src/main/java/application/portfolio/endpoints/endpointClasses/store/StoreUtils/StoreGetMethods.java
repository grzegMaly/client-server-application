package application.portfolio.endpoints.endpointClasses.store.StoreUtils;

import application.portfolio.requestResponse.Requests.GetRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

public class StoreGetMethods {

    private static final Map<String, String> fData;

    static {
        fData = Infrastructure.getFileServerData();
    }

    public static void handleDownload(HttpExchange exchange, Map<String, String> paramsMap) {

        String params = DataParser.paramsString(paramsMap);
        String spec = Infrastructure.uriSpecificPart(fData, "store/resource", params);

        URI uri = Infrastructure.getBaseUri(paramsMap).resolve(spec);
        GetRequests.handleGetRequest(exchange, uri, HttpResponse.BodyHandlers.ofInputStream());
    }
}
