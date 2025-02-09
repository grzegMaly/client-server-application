package application.portfolio.clientmodule.Model.Request.Chat.Friends;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest.FriendsRequest;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FriendsRequestModel {

    private final HttpClient client = ClientHolder.getClient();

    public List<PersonDAO> getFriends(FriendsRequest request) throws IOException {

        String queryParams = FriendsRequestConverter.toQueryParams(request);
        Map<String, String> gData = Infrastructure.getGatewayData();
        String spec = Infrastructure.uriSpecificPart(gData, "group/user", queryParams);

        URI baseUri = Infrastructure.getBaseUri(spec);
        HttpRequest httpRequest = HttpRequest.newBuilder(baseUri)
                .GET()
                .build();

        try {
            HttpResponse<JsonNode> response = client.send(httpRequest, JsonBodyHandler.getJsonHandler());

            if (response.statusCode() == 200) {
                JsonNode node = response.body();
                return FriendsRequestConverter.fromJson(node);
            } else {
                return Collections.emptyList();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }
}
