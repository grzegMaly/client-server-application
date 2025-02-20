package application.portfolio.clientmodule.Model.Request.Chat.Friends;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest.FriendsRequest;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.UUID;

public class FriendsRequestViewModel {

    private static final FriendsRequestModel model = new FriendsRequestModel();
    private static final ObservableList<Person> friends = FXCollections.observableArrayList();
    private static final UUID actualUserId = UserSession.getInstance().getLoggedInUser().getUserId();

    public static ObservableList<Person> getFriends() {
        return friends;
    }

    public static Person getPerson(UUID id) {
        return friends.stream()
                .filter(p -> p.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<Person> loadFriends() {

        if (friends.isEmpty()) {
            List<Person> friendsList = model.getFriends(new FriendsRequest(actualUserId));
            Platform.runLater(() -> friends.setAll(friendsList));
            return friendsList;
        }
        return friends;
    }

}
