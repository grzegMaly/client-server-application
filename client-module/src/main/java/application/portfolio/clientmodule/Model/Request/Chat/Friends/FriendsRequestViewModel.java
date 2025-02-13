package application.portfolio.clientmodule.Model.Request.Chat.Friends;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest.FriendsRequest;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.UUID;

public class FriendsRequestViewModel {

    private static final FriendsRequestModel model = new FriendsRequestModel();
    private static final ObservableList<PersonDAO> friends = FXCollections.observableArrayList();
    private static final UUID actualUserId = UserSession.getInstance().getLoggedInUser().getId();

    public static ObservableList<PersonDAO> getFriends() {
        return friends;
    }

    public static PersonDAO getPerson(UUID id) {
        return friends.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<PersonDAO> loadFriends() {

        if (friends.isEmpty()) {
            List<PersonDAO> friendsList = model.getFriends(new FriendsRequest(actualUserId));
            Platform.runLater(() -> friends.setAll(friendsList));
            return friendsList;
        }
        return friends;
    }

}
