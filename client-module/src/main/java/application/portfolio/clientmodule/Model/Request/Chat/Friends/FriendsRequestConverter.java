package application.portfolio.clientmodule.Model.Request.Chat.Friends;

import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest.FriendsRequest;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.OtherElements.PersonMethods;
import application.portfolio.clientmodule.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendsRequestConverter {
    public static String toQueryParams(FriendsRequest request) {
        String uuid = request.getUserId().toString();
        return DataParser.paramsString(Map.of("userId", uuid, "friends", "true"));
    }

    public static List<PersonDAO> fromJson(JsonNode node) {
        List<PersonDAO> friends = new ArrayList<>();
        node = node.get("response");
        if (node.isArray()) {
            for (JsonNode friendNode : node) {
                PersonDAO friend = PersonMethods.createPersonFromNode(friendNode);
                friends.add(friend);
            }
        } else if (node.isObject()) {
            PersonDAO friend = PersonMethods.createPersonFromNode(node);
            friends.add(friend);
        }
        return friends;
    }
}
