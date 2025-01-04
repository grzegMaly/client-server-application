package application.portfolio.clientmodule.OtherElements;

import com.fasterxml.jackson.databind.JsonNode;

public class PersonMethods {

    public static PersonDAO createPersonFromNode(JsonNode node) {
        try {
            String id = node.get("id").asText();
            String firstName = node.get("firstName").asText();
            String lastName = node.get("lastName").asText();
            int role = node.get("role").asInt();

            return new PersonDAO(id, firstName, lastName, role);
        } catch (Exception e) {
            return null;
        }
    }
}
