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
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;


public class Chat extends VBox implements Page {

    private final ChatBinder chatBinder = new ChatBinder();

    private final TopChatBar topChatBar = new TopChatBar(chatBinder);
    private static final StackPane chatPages = new StackPane();
    private BottomChatBar bottomChatBar = null;
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

        CompletableFuture<BottomChatBar> bottomBarFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(BottomChatBar.class), executor);
        CompletableFuture<Friends> friendsFuture =
                CompletableFuture.supplyAsync(() -> PageFactory.getInstance(Friends.class), executor);

        return CompletableFuture.allOf(bottomBarFuture, friendsFuture)
                .thenComposeAsync(v -> {

                    bottomChatBar = bottomBarFuture.join();
                    friendsList = friendsFuture.join();

                    if (friendsList == null || bottomChatBar == null) {
                        return CompletableFuture.completedFuture(false);
                    }

                    friendsList.setChatBinder(chatBinder);
                    bottomChatBar.setChatBinder(chatBinder);

                    bindSizeProperties();

                    return CompletableFuture.completedFuture(true);
                }, executor)
                .thenApply(success -> {
                    if (success) {
                        Platform.runLater(() -> {

                            chatTemplate.getChildren().addAll(topChatBar, chatPages, bottomChatBar);
                            friendsList.loadChatAction(chatTemplate);

                            HBox elements = new HBox(chatTemplate, friendsList);
                            HBox.setHgrow(chatTemplate, Priority.ALWAYS);

                            this.getChildren().add(elements);
                            chatTemplate.setVisible(false);
                        });
                    }

                    return success;
                }).exceptionally(e -> {
                    System.out.println("initializeCheckBoxes " + this.getClass().getSimpleName());
                    e.printStackTrace();
                    return false;
                });
    }

    public static StackPane getChats() {
        return chatPages;
    }

    @Override
    public void bindSizeProperties() {

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenHeight = screenBounds.getHeight();

        Platform.runLater(() -> {

            topChatBar.setMinHeight(screenHeight * 0.03);
            topChatBar.setMaxHeight(screenHeight * 0.03);
            bottomChatBar.setMinHeight(screenHeight * 0.05);
            bottomChatBar.setMaxHeight(screenHeight * 0.05);

            friendsList.prefHeightProperty().bind(this.widthProperty().multiply(0.2));
            chatPages.prefWidthProperty().bind(this.widthProperty().multiply(0.8));
            topChatBar.prefWidthProperty().bind(chatPages.widthProperty());
            bottomChatBar.prefWidthProperty().bind(chatPages.widthProperty());

            friendsList.prefHeightProperty().bind(this.heightProperty());
            chatPages.prefHeightProperty().bind(this.heightProperty().subtract(bottomChatBar.heightProperty().add(topChatBar.heightProperty())));
        });
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadChatSceneStyle(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {
        this.getStyleClass().add("baseBG");
        friendsList.getStyleClass().add("chatFriendsList");
    }

    @Override
    public Parent asParent() {
        return this;
    }
}