package application.portfolio.clientmodule.utils.session;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PersonMethods {

    public static Person createPersonFromNode(JsonNode node) {
        try {
            String id = node.get("id").asText();
            String firstName = node.get("firstName").asText();
            String lastName = node.get("lastName").asText();
            int role = node.get("role").asInt();

            return new Person(id, firstName, lastName, role);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Person> parseEntities(HttpResponse<JsonNode> response) {

        JsonNode node = response.body().get("response");

        if (node == null) {
            return Collections.emptyList();
        }

        List<Person> people = new ArrayList<>();
        Consumer<JsonNode> personConsumer = n -> {
            Person personDAO = PersonMethods.createPersonFromNode(n);
            if (personDAO != null) {
                people.add(personDAO);
            }
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
        return people;
    }
}
