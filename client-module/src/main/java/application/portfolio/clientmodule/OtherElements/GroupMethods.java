package application.portfolio.clientmodule.OtherElements;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.utils.DataParser;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupMethods {

    public static void loadGroups() {
        PersonDAO actualPerson = UserSession.getInstance().getLoggedInUser();
        loadGroups(actualPerson);
    }

    public static void loadGroups(PersonDAO personDAO) {

        HttpClient client = ClientHolder.getClient();

        Map<String, String> gData = Infrastructure.getGatewayData();
        String params = DataParser.paramsString(Map.of("userId", personDAO.getId().toString()));
        String spec = Infrastructure.uriSpecificPart(gData, "group/user", params);
        URI baseUri = Infrastructure.getBaseUri(gData).resolve(spec);

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .GET()
                .header("Accept", "application/json")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonBodyHandler handler = JsonBodyHandler.create(objectMapper);
        HttpResponse<JsonNode> response;
        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            return;
        }

        List<GroupDAO> groups;
        if (response.statusCode() != 200) {
            return;
        }

        groups = handleResponse(response);
        Map<Boolean, List<GroupDAO>> partitionedGroups = groups.stream()
                .collect(Collectors.partitioningBy(g -> g.getOwner().equals(personDAO.getId())));

        int role = personDAO.getRole().getId();
        if (role > 1) {
            personDAO.addOwnedGroups(partitionedGroups.get(true));
        }
        personDAO.addToGroup(partitionedGroups.get(false));
    }

    private static List<GroupDAO> handleResponse(HttpResponse<JsonNode> response) {

        List<GroupDAO> groups = new ArrayList<>();
        JsonNode node = response.body();
        node = node.get("request");

        if (node.isArray()) {
            for (JsonNode groupNode : node) {
                GroupDAO groupDAO = createGroup(node);
                if (groupDAO != null) {
                    groups.add(groupDAO);
                }
            }
        } else if (node.isObject()) {
            GroupDAO groupDAO = createGroup(node);
            if (groupDAO != null) {
                groups.add(groupDAO);
            }
        }
        return groups;
    }

    private static GroupDAO createGroup(JsonNode node) {

        try {
            String groupId = node.get("groupId").asText();
            String groupName = node.get("groupName").asText();
            String ownerId = node.get("ownerId").asText();
            return new GroupDAO(groupId, groupName, ownerId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
