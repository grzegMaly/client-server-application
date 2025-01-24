package application.portfolio.requestResponse.Requests;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GetRequests {

    public static void handleGetRequest(HttpExchange exchange, URI baseUri) {
        handleGetRequest(exchange, baseUri, HttpResponse.BodyHandlers.ofByteArray());
    }

    public static <T> void handleGetRequest(HttpExchange exchange, URI baseUri, HttpResponse.BodyHandler<T> bodyHandler) {
        HttpResponse<T> response = sendGetRequest(exchange, baseUri, bodyHandler);
        ResponseHandler.handleReceivedResponse(exchange, response);
    }

    public static <T> HttpResponse<T> sendGetRequest(
            HttpExchange exchange,
            URI baseUri,
            HttpResponse.BodyHandler<T> bodyHandler
    ) {

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(baseUri)
                .GET()
                .timeout(Duration.ofSeconds(10));

        ResponseHandler.copyHeaders(exchange, requestBuilder);
        HttpRequest request = requestBuilder.build();
        return ClientHolder.handleRequestSend(request, bodyHandler);
    }
}
