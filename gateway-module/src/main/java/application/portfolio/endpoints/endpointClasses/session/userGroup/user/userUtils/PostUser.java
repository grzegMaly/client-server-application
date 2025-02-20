package application.portfolio.endpoints.endpointClasses.session.userGroup.user.userUtils;

import application.portfolio.requestResponse.Requests.PostRequests;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.*;

public class PostUser {

    public static void handlePost(HttpExchange exchange) throws IOException {

        Map<String, String> dbData = Infrastructure.getDatabaseData();
        String spec = Infrastructure.uriSpecificPart(dbData, "user/register");

        URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);
        PostRequests.handlePostRequest(exchange, baseUri);
    }
}
