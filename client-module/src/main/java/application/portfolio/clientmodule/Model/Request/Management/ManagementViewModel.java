package application.portfolio.clientmodule.Model.Request.Management;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import application.portfolio.clientmodule.utils.session.GroupMethods;
import application.portfolio.clientmodule.utils.session.PersonMethods;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class ManagementViewModel {

    public List<Person> loadUsers() {

        HttpRequest request = ClientHolder.prepareRequest("?all=true", "allUsers",
                "GET", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }

        if (response.statusCode() == 200) {
            return PersonMethods.parseEntities(response);
        }
        return Collections.emptyList();
    }

    public List<Group> getAllGroups() {

        HttpRequest request = ClientHolder.prepareRequest("?all=true", "group", "GET",
                HttpRequest.BodyPublishers.noBody()).build();

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
}
