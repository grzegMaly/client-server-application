package application.portfolio.clientmodule.Model.Request.Chat.Friends;

import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest.FriendsRequest;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.utils.DataParser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.UUID;

public class FriendsRequestViewModel {

    private final FriendsRequestModel model = new FriendsRequestModel();
    private static final ObservableList<PersonDAO> friends = FXCollections.observableArrayList();

    public static ObservableList<PersonDAO> getFriends() {
        return friends;
    }

    public static PersonDAO getPerson(UUID id) {
        return friends.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void loadFriends(UUID userId) {
        try {
            List<PersonDAO> friendsList = model.getFriends(new FriendsRequest(userId));
            Platform.runLater(() -> friends.setAll(friendsList));
        } catch (Exception ignored) {
            //Nothing
        }
    }

}
