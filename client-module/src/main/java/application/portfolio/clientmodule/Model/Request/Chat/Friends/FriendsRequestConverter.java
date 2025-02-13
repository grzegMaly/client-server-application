package application.portfolio.clientmodule.Model.Request.Chat.Friends;

import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest.FriendsRequest;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.utils.session.PersonMethods;
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
}
