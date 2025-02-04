package application.portfolio.endpoints.endpointClasses.store.StoreUtils;

import application.portfolio.requestResponse.Requests.PostRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;

public class StorePostMethods {

    private static final Map<String, String> fData;

    static {
        fData = Infrastructure.getFileServerData();
    }

    public static void handleUpload(HttpExchange exchange, Map<String, String> paramsMap) {

        String params = DataParser.paramsString(paramsMap);
        String spec = Infrastructure.uriSpecificPart(fData, "store/resource", params);

        URI uri = Infrastructure.getBaseUri(fData).resolve(spec);
        PostRequests.handlePostRequest(exchange, uri, HttpRequest.BodyPublishers.ofInputStream(exchange::getRequestBody));
    }
}
