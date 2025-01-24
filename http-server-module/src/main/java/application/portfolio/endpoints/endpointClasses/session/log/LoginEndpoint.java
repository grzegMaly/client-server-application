package application.portfolio.endpoints.endpointClasses.session.log;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.requestResponse.Requests.PostRequests;
import application.portfolio.utils.Infrastructure;
import application.portfolio.requestResponse.ResponseHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/user/login")
public class LoginEndpoint implements EndpointHandler, HttpHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            if ("POST".equals(exchange.getRequestMethod())) {

                Map<String, String> authData = Infrastructure.getAuthorizationData();
                String spec = Infrastructure.uriSpecificPart(authData, "authorization");
                URI baseUri = Infrastructure.getBaseUri(authData).resolve(spec);

                PostRequests.handlePostRequest(exchange, baseUri);
            } else {
                ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
            }
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}