package application.portfolio.clientmodule.Model.Request.Management.UserAndGroups;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.utils.DataParser;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import application.portfolio.clientmodule.utils.session.GroupMethods;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ManageUsersAndGroupsViewModel {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Group> getJoinedGroups(UUID userId) {

        HttpRequest request = ClientHolder.prepareRequest("?userId=" + userId.toString(),
                "groupsWithUsers", "GET", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }

        if (response.statusCode() == 200) {
            return GroupMethods.parseEntities(response);
        }
        return null;
    }

    public boolean addUserToGroup(UUID userId, UUID groupId) {

        ObjectNode node = objectMapper.createObjectNode();
        node.put("userId", userId.toString());
        node.put("groupId", groupId.toString());

        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = ClientHolder.prepareRequest("", "groupsWithUsers", "POST",
                HttpRequest.BodyPublishers.ofByteArray(data)).build();

        try {
            HttpResponse<Void> response = ClientHolder.getClient().send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean deleteUserFromGroup(UUID userId, UUID groupId) {

        String params = DataParser.paramsString(
                Map.of(
                        "userId", userId.toString(),
                        "groupId", groupId.toString()
                )
        );

        HttpRequest request = ClientHolder.prepareRequest(params, "groupsWithUsers", "DELETE",
                HttpRequest.BodyPublishers.noBody()).build();

        try {
            HttpResponse<Void> response = ClientHolder.getClient().send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
