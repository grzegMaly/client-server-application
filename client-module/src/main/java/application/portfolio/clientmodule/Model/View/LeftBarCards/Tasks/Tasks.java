package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog.TaskDialog;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.Model.Model.Person.Role;
import application.portfolio.clientmodule.Model.Model.Task.TaskDAO;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class Tasks extends VBox implements Page {

    private Button newTaskBtn = null;
    private Button receivedTasksBtn = null;
    private Button wroteTasksBtn = null;
    private Button refreshBtn = new Button("Refresh");
    private ButtonBar buttonBar = new ButtonBar();
    private TableView<TaskDAO> tasksTable = new TableView<>();

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private Tasks() {
    }

    public static boolean moreThanEmployee() {
        Role role = UserSession.getInstance().getLoggedInUser().getRole();
        return role == Role.MANAGER || role == Role.ADMIN;
    }

    private Boolean checkRole() {
        return moreThanEmployee();
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        return CompletableFuture.supplyAsync(this::checkRole, executor)
                .thenAccept(success -> {
                    if (success) {
                        CompletableFuture.runAsync(this::initializeManagerControls, executor)
                                .join();
                    }
                }).thenRunAsync(() -> {
                    tasksTable = TaskController.getTaskTable();
                    TaskController.loadReceivedTasks(tasksTable);
                }, executor)
                .thenApply(v -> {
                    Platform.runLater(() -> {
                        buttonBar.getButtons().add(refreshBtn);
                        this.getChildren().addAll(buttonBar, tasksTable);
                    });
                    return true;
                }).exceptionally(e -> {
                    //Todo: Make it Custom
                    System.out.println("Error in " + this.getClass().getSimpleName() + ", initializePage");
                    e.printStackTrace();
                    return false;
                });
    }

    private void initializeManagerControls() {

        newTaskBtn = new Button("New Task");
        receivedTasksBtn = new Button("Received Tasks");
        wroteTasksBtn = new Button("Wrote Tasks");

        buttonBar.getButtons().addAll(receivedTasksBtn, wroteTasksBtn);

        newTaskBtn.setOnAction(evt -> new TaskDialog().createTask());
        wroteTasksBtn.setOnAction(evt -> TaskController.loadWroteTasks(tasksTable));
        receivedTasksBtn.setOnAction(evt -> TaskController.loadReceivedTasks(tasksTable));

        Platform.runLater(() -> this.getChildren().addAll(newTaskBtn));
    }


    @Override
    public Boolean loadStyleClass() {
        return CompletableFuture.supplyAsync(() ->
                        LoadStyles.loadTasksSceneStyle(TeamLinkApp.getScene(MainScene.class)), executor)
                .exceptionally(e -> {
                    //Todo: Make it custom
                    System.out.println("loadStyleClass, " + this.getClass().getSimpleName());
                    e.printStackTrace();
                    return false;
                }).join();
    }

    @Override
    public void loadStyles() {
        this.getStyleClass().add("tasksBG");
    }

    @Override
    public Parent asParent() {
        return this;
    }
}