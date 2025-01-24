package application.portfolio.requestResponse.Requests;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.requestResponse.ResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class DeleteRequests {

    public static void handleDeleteRequest(HttpExchange exchange, URI baseUri) {
        HttpResponse<byte[]> response = sendDeleteRequest(exchange, baseUri);
        ResponseHandler.handleReceivedResponse(exchange, response);
    }

    public static HttpResponse<byte[]> sendDeleteRequest(HttpExchange exchange, URI baseUri) {

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(baseUri)
                .DELETE()
                .timeout(Duration.ofSeconds(10));

        ResponseHandler.copyHeaders(exchange, requestBuilder);
        HttpRequest request = requestBuilder.build();
        return ClientHolder.handleRequestSend(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
