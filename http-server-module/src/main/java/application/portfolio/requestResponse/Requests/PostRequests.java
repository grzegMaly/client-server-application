package application.portfolio.requestResponse.Requests;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PostRequests {

    public static void handlePostRequest(HttpExchange exchange, URI baseUri) throws IOException {
        byte[] data = exchange.getRequestBody().readAllBytes();
        handlePostRequest(exchange, baseUri, HttpRequest.BodyPublishers.ofByteArray(data));
    }

    public static void handlePostRequest(HttpExchange exchange, URI baseUri, HttpRequest.BodyPublisher publisher) {
        HttpResponse<byte[]> response = sendPostRequest(exchange, baseUri, publisher);
        ResponseHandler.handleReceivedResponse(exchange, response);
    }

    public static HttpResponse<byte[]> sendPostRequest(HttpExchange exchange, URI baseUri,
                                                       HttpRequest.BodyPublisher publisher) {

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(baseUri)
                .POST(publisher)
                .timeout(Duration.ofSeconds(10));

        ResponseHandler.copyHeaders(exchange, requestBuilder);
        HttpRequest request = requestBuilder.build();
        return ClientHolder.handleRequestSend(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
