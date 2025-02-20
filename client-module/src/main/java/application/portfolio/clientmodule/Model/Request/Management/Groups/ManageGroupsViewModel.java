package application.portfolio.clientmodule.Model.Request.Management.Groups;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.Model.Group.GroupDAO;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import application.portfolio.clientmodule.utils.session.GroupMethods;
import application.portfolio.clientmodule.utils.session.PersonMethods;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ManageGroupsViewModel {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public Group addGroup(Group group) {

        GroupDAO groupDAO = Group.toDAO(group);
        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(groupDAO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = ClientHolder.prepareRequest("", "group", "POST",
                HttpRequest.BodyPublishers.ofByteArray(data)).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() == 200) {
            return GroupMethods.parseEntities(response).get(0);
        }
        return null;
    }

    public Group updateGroup(Group group) {

        GroupDAO groupDAO = Group.toDAO(group);
        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(groupDAO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = ClientHolder.prepareRequest("", "group", "PUT",
                HttpRequest.BodyPublishers.ofByteArray(data)).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() == 200) {
            return GroupMethods.parseEntities(response).get(0);
        }
        return null;
    }

    public boolean removeGroup(UUID groupId) {

        HttpRequest request = ClientHolder.prepareRequest("?id=" + groupId.toString(), "group",
                "DELETE", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response;
        try {
            response = ClientHolder.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return response.statusCode() == 200;
    }

    public Person getOwner(UUID userId) {

        String id = userId.toString();
        HttpRequest request = ClientHolder.prepareRequest("?id=" + id, "allUsers", "GET",
                HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<JsonNode> response;

        try {
            response = ClientHolder .getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return null;
        }

        if (response.statusCode() == 200) {
            return PersonMethods.createPersonFromNode(response.body().get("response"));
        }
        return null;
    }
}
