package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Connection.WebSocket.WebSocketClientHolder;
import application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequestViewModel;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Bars.BottomChatBar;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Bars.TopChatBar;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import application.portfolio.clientmodule.utils.PageFactory;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;


public class Chat extends VBox implements Page {

    private final ChatBinder chatBinder = new ChatBinder();

    private TopChatBar topChatBar;
    private BottomChatBar bottomChatBar;
    private static final StackPane chatPages = new StackPane();
    private final VBox chatTemplate = new VBox();
    private Friends friendsList = null;

    private static final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(Chat.class.getSimpleName());

    private Chat() {
        ChatRequestViewModel.setUpChatConnection();
        setUpVisibilityListener();
    }

    private void setUpVisibilityListener() {
        this.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ChatRequestViewModel.setUpChatConnection();
            } else {
                WebSocketClientHolder.getInstance().close();
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        CompletableFuture<TopChatBar> topBarFuture =
                CompletableFuture.supplyAsync(() -> new TopChatBar(chatBinder), executor);

        CompletableFuture<BottomChatBar> bottomBarFuture =
                CompletableFuture.supplyAsync(() -> new BottomChatBar(chatBinder), executor);

        CompletableFuture<Friends> friendsFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(Friends.class), executor);

        return topBarFuture
                .thenCombine(bottomBarFuture, (topResult, bottomResult) -> {
                    this.topChatBar = topResult;
                    this.bottomChatBar = bottomResult;
                    return topResult != null && bottomResult != null;
                })
                .thenCombine(friendsFuture, (success, friendsResult) -> {
                    this.friendsList = friendsResult;
                    return success && friendsResult != null;
                })
                .thenApply(success -> {
                    if (success) {

                        friendsList.setChatBinder(chatBinder);
                        friendsList.loadChatAction(chatTemplate);

                        chatTemplate.getChildren().addAll(topChatBar, chatPages, bottomChatBar);
                        chatTemplate.setFillWidth(true);
                        chatTemplate.setVisible(false);

                        HBox elements = new HBox(chatTemplate, friendsList);
                        Platform.runLater(() -> this.getChildren().add(elements));
                    }
                    return success;
                })
                .thenRun(this::bindSizeProperties)
                .thenApply(v -> true)
                .exceptionally(e -> false);
    }

    public static StackPane getChats() {
        return chatPages;
    }

    @Override
    public void bindSizeProperties() {

        Platform.runLater(() -> {

            topChatBar.prefHeightProperty().bind(chatTemplate.heightProperty().multiply(0.05));

            chatPages.setMinHeight(100);
            chatPages.setMaxHeight(Double.MAX_VALUE);
            chatPages.prefHeightProperty().bind(
                    this.heightProperty()
                            .subtract(topChatBar.prefHeightProperty())
                            .subtract(bottomChatBar.prefHeightProperty())
            );

            friendsList.prefHeightProperty().bind(this.heightProperty());

            VBox.setVgrow(chatPages, Priority.ALWAYS);
            VBox.setVgrow(topChatBar, Priority.NEVER);
            VBox.setVgrow(bottomChatBar, Priority.NEVER);
            VBox.setVgrow(chatTemplate, Priority.ALWAYS);
            HBox.setHgrow(chatTemplate, Priority.ALWAYS);

            chatTemplate.requestLayout();
            this.requestLayout();
        });
    }


    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadChatSceneStyle(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {
        topChatBar.loadStyles();
        bottomChatBar.loadStyles();
    }

    @Override
    public Parent asParent() {
        return this;
    }
}