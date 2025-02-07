package application.portfolio.clientmodule.Model.Request.Disc;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Model.Request.Disc.DiscRequest.DiscRequest;
import application.portfolio.clientmodule.Model.Model.Disc.DiscElement;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DiscRequestModel {

    private final Map<String, String> gData;

    {
        gData = Infrastructure.getGatewayData();
    }

    public List<DiscElement> loadDiscView(DiscRequest discRequest) {

        HttpRequest request = prepareRequest(discRequest, "store/list",
                "GET", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }

        if (response.statusCode() == 200) {
            return parseEntities(response);
        }
        return Collections.emptyList();
    }

    public InputStream downloadResource(DiscRequest discRequest) {

        HttpRequest request = prepareRequest(discRequest, "store/resource",
                "GET", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<InputStream> response;
        try {
            response = ClientHolder.getClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException e) {
            return null;
        }

        if (response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }

    public boolean deleteResource(DiscRequest discRequest) {

        HttpRequest request = prepareRequest(discRequest, "store/resource",
                "DELETE", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<Void> response;
        try {
            response = ClientHolder.getClient().send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public DiscElement uploadResource(DiscRequest discRequest, InputStream inputStream) {

        HttpRequest.Builder requestBuilder = prepareRequest(discRequest, "store/resource",
                "POST", HttpRequest.BodyPublishers.ofInputStream(() -> inputStream));
        if (discRequest.isFileUpload()) {
            requestBuilder.header("Content-Type", "application/octet-stream");
        } else {
            requestBuilder.header("Content-Type", "application/zip");
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return null;
        }

        if (response.statusCode() != 200) {
            return null;
        }

        List<DiscElement> elements = parseEntities(response);
        if (elements.size() != 1) {
            return null;
        } else {
            return elements.get(0);
        }
    }

    private HttpRequest.Builder prepareRequest(DiscRequest discRequest, String endpoint, String method,
                                               HttpRequest.BodyPublisher bodyPublisher) {
        String params = DiscRequestConverter.toQueryParams(discRequest);
        String spec = Infrastructure.uriSpecificPart(gData, endpoint, params);

        URI baseUri = Infrastructure.getBaseUri(spec);
        return ClientHolder.getRequest(baseUri, method, bodyPublisher);
    }

    private List<DiscElement> parseEntities(HttpResponse<JsonNode> response) {

        JsonNode node = response.body();
        node = node.get("response");

        if (node == null) {
            return Collections.emptyList();
        }

        List<DiscElement> elements = new ArrayList<>();
        Consumer<JsonNode> consumer = n -> {
            DiscElement element = DiscElement.createElement(n);
            if (element != null) {
                elements.add(element);
            }
        };

        if (node.isArray()) {
            for (JsonNode n : node) {
                consumer.accept(n);
            }
        } else if (node.isObject()) {
            consumer.accept(node);
        }
        return elements;
    }
}
