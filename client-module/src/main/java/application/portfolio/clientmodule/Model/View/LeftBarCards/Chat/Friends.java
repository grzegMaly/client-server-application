package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.HttpClient.UserSession;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.OtherElements.temp.Objects;
import application.portfolio.clientmodule.TeamLinkApp;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Friends extends ListView<PersonDAO> implements Page {

    private final Set<PersonDAO> friendsSet = new HashSet<>();
    private ChatBinder chatBinder = null;

    private Friends() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        return CompletableFuture.supplyAsync(() -> {
            initListCell();
            return loadFriends();
        }).thenApply(success -> {
            if (!success) {
                //Todo: Do usunięcia
                System.out.println("Błąd");
            }
            return success;
        }).exceptionally(e -> {
            System.out.println("initializePage, " + this.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        });
    }

    private void initListCell() {

        this.setCellFactory(new Callback<>() {
            @Override
            public ListCell<PersonDAO> call(ListView<PersonDAO> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(PersonDAO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });
    }

    private Boolean loadFriends() {

        //Todo: Connect to the server
        List<PersonDAO> people = Objects.getPersons();
        PersonDAO actualUser = UserSession.getInstance().getLoggedInUser();

        for (var p : people) {
            if (p.getGroups().stream().anyMatch(g -> actualUser.getGroups().contains(g))) {
                friendsSet.add(p);
            }
        }

        friendsSet.remove(actualUser);
        friendsSet.add(actualUser);
        this.getItems().addAll(friendsSet);

        return true;
    }

    public void setChatBinder(ChatBinder chatBinder) {
        this.chatBinder = chatBinder;
    }

    public void loadChatAction(VBox struct) {

        StackPane chats = (StackPane) struct.getChildren().stream()
                .filter(s -> s instanceof StackPane)
                .findFirst().get();

        this.setOnMouseClicked(evt -> {

            if (!struct.isVisible()) {
                struct.setVisible(true);
            }

            PersonDAO personDAO = this.getSelectionModel().getSelectedItem();
            if (personDAO == null) {
                return;
            }

            Optional<ChatView> existingChatView = chats.getChildren().stream()
                    .filter(c -> c instanceof ChatView)
                    .map(c -> (ChatView) c)
                    .filter(chatView -> chatView.getChatId().equals(personDAO.getId().toString()))
                    .findFirst();

            if (existingChatView.isPresent()) {

                chats.getChildren().forEach(child -> child.setVisible(false));
                existingChatView.get().setVisible(true);
            } else {

                ChatView newChatView = ChatController.getChat(personDAO);
                chats.getChildren().forEach(child -> child.setVisible(false));
                newChatView.setVisible(true);
                chats.getChildren().add(newChatView);
            }

            chatBinder.withReceiver(personDAO);
        });
    }

    @Override
    public Boolean loadStyleClass() {

        return LoadStyles.loadFriendsListClass(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {
        this.getStyleClass().add("friendsListBG");
    }

    @Override
    public Parent asParent() {
        return this;
    }
}
