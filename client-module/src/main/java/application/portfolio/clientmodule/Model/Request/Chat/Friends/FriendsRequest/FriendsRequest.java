package application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest;

import java.util.UUID;

public class FriendsRequest {
    private final UUID userId;

    public FriendsRequest(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
