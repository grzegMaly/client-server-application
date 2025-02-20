package application.portfolio.clientmodule.utils.session;

import application.portfolio.clientmodule.Model.Model.Group.Group;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Management.ManagementBinder;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GroupMethods {

    public static Group createGroupFromNode(JsonNode node) {
        try {

            UUID id = UUID.fromString(node.get("groupId").asText());
            String groupName = node.get("groupName").asText();
            UUID ownerId = UUID.fromString(node.get("ownerId").asText());

            Person owner = ManagementBinder.getUser(ownerId);
            Group group = new Group(id, groupName, owner);
            group.setOwnerId(ownerId);
            return group;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Group> parseEntities(HttpResponse<JsonNode> response) {

        JsonNode node = response.body().get("response");

        if (node == null) {
            return Collections.emptyList();
        }

        List<Group> groups = new ArrayList<>();
        Consumer<JsonNode> groupConsumer = n -> {
            Group group = GroupMethods.createGroupFromNode(n);
            if (group != null) {
                groups.add(group);
            }
        };

        if (node.isArray()) {
            for (JsonNode n : node) {
                groupConsumer.accept(n);
            }
        } else if (node.isObject()) {
            groupConsumer.accept(node);
        } else {
            return Collections.emptyList();
        }
        return groups;
    }
}
