package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequestViewModel;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequestViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Bars.BottomChatBar;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.TeamLinkApp;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Friends extends ListView<Person> implements Page {

    private static ChatBinder chatBinder = null;

    private Friends() {
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        CompletableFuture<Void> cellFuture =
                CompletableFuture.runAsync(this::initListCell);
        CompletableFuture<Void> friendsFuture =
                CompletableFuture.runAsync(this::loadFriends);

        return CompletableFuture.allOf(cellFuture, friendsFuture)
                .thenRun(this::bindFriendsToViewModel)
                .thenApply(v -> true)
                .exceptionally(e -> {
                    //TODO: Send to server
                    e.printStackTrace();
                    return false;
                });
    }

    private void initListCell() {

        this.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Person> call(ListView<Person> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Person item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item.getName());
                            setStyle("-fx-text-fill: white;");
                        }
                    }
                };
            }
        });
    }

    private void bindFriendsToViewModel() {
        Platform.runLater(() -> {
            this.setItems(FriendsRequestViewModel.getFriends());
        });
    }

    private void loadFriends() {

        if (UserSession.getPersonObjects().isEmpty()) {
            List<Person> friendsList = FriendsRequestViewModel.loadFriends();
            CompletableFuture.runAsync(() -> friendsList.forEach(UserSession::addPerson));
        }
    }

    public void setChatBinder(ChatBinder chatBinder) {
        Friends.chatBinder = chatBinder;
    }

    public void loadChatAction(VBox struct) {

        StackPane chats = Chat.getChats();
        this.setOnMouseClicked(evt -> {

            if (!struct.isVisible()) {
                struct.setVisible(true);
            }

            Person personDAO = this.getSelectionModel().getSelectedItem();
            if (personDAO == null) {
                return;
            }

            ChatController.setActualUser(personDAO);
            Optional<ChatView> existingChatView = chats.getChildren().stream()
                    .filter(c -> c instanceof ChatView)
                    .map(c -> (ChatView) c)
                    .filter(chatView -> chatView.getChatId().equals(personDAO.getUserId()))
                    .findFirst();

            ChatView chatView;
            if (existingChatView.isPresent()) {
                chatView = existingChatView.get();
            } else {
                chatView = createChat(personDAO);
                chats.getChildren().add(chatView);
            }
            chats.getChildren().forEach(child -> child.setVisible(false));
            chatView.setVisible(true);
            ChatController.setActualChat(chatView);

            chatBinder.withReceiver(personDAO);
        });
    }

    public static ChatView createChat(Person personDAO) {

        ChatRequestViewModel viewModel = chatBinder.getViewModel();
        return ChatController.createChat(personDAO, viewModel);
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
