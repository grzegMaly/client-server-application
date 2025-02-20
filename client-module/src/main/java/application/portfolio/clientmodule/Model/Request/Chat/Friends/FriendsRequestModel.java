package application.portfolio.clientmodule.Model.Request.Chat.Friends;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest.FriendsRequest;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import application.portfolio.clientmodule.utils.session.PersonMethods;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FriendsRequestModel {

    private final HttpClient client = ClientHolder.getClient();

    public List<Person> getFriends(FriendsRequest request) {

        String queryParams = FriendsRequestConverter.toQueryParams(request);
        Map<String, String> gData = Infrastructure.getGatewayData();
        String spec = Infrastructure.uriSpecificPart(gData, "groupsWithUsers", queryParams);

        URI baseUri = Infrastructure.getBaseUri(spec);
        HttpRequest httpRequest = HttpRequest.newBuilder(baseUri)
                .GET()
                .build();

        HttpResponse<JsonNode> response;
        try {
            response = client.send(httpRequest, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }

        if (response.statusCode() == 200) {
            return parseEntities(response);
        } else {
            return Collections.emptyList();
        }
    }

    private static List<Person> parseEntities(HttpResponse<JsonNode> response) {

        JsonNode node = response.body();
        node = node.get("response");

        if (node == null) {
            return Collections.emptyList();
        }

        List<Person> friends = new ArrayList<>();
        Consumer<JsonNode> personConsumer = n -> {
            Person friend = PersonMethods.createPersonFromNode(n);
            friends.add(friend);
        };

        if (node.isArray()) {
            for (JsonNode n : node) {
                personConsumer.accept(n);
            }
        } else if (node.isObject()) {
            personConsumer.accept(node);
        } else {
            return Collections.emptyList();
        }
        return friends;
    }
}
