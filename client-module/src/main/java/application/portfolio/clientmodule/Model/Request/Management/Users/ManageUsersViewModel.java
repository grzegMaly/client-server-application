package application.portfolio.clientmodule.Model.Request.Management.Users;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import application.portfolio.clientmodule.utils.session.PersonMethods;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class ManageUsersViewModel {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);;

    public Person addUser(PersonDAO user) {

        byte[] data;

        try {
            data = objectMapper.writeValueAsBytes(user);
        } catch (JsonProcessingException e) {
            return null;
        }

        HttpRequest request = ClientHolder.prepareRequest("", "user/register",
                "POST", HttpRequest.BodyPublishers.ofByteArray(data)).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return null;
        }

        if (response.statusCode() == 200) {
            return PersonMethods.parseEntities(response).get(0);
        }
        return null;
    }

    public Person updateUser(PersonDAO user) {

        byte[] data;

        try {
            data = objectMapper.writeValueAsBytes(user);
        } catch (JsonProcessingException e) {
            return null;
        }

        HttpRequest request = ClientHolder.prepareRequest("", "user",
                "PUT", HttpRequest.BodyPublishers.ofByteArray(data)).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return null;
        }

        if (response.statusCode() == 200) {
            return PersonMethods.parseEntities(response).get(0);
        }
        return null;
    }

    public boolean removeUser(Person personDAO) {

        UUID id = personDAO.getUserId();
        HttpRequest request = ClientHolder.prepareRequest("?id=" + id.toString(), "user",
                "DELETE", HttpRequest.BodyPublishers.noBody()).build();


        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return response.statusCode() == 200;
    }
}
