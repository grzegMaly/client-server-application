package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Task.Task;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog.TaskDialog;
import application.portfolio.clientmodule.Model.View.Page;
import application.portfolio.clientmodule.Model.View.Scenes.start.MainScene;
import application.portfolio.clientmodule.Model.Model.Person.Role;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class Tasks extends VBox implements Page {

    private Button newTaskBtn = null;
    private Button receivedTasksBtn = null;
    private Button wroteTasksBtn = null;
    private final Button refreshBtn = new Button("Refresh");
    private final ButtonBar buttonBar = new ButtonBar();
    private TableView<Task> tasksTable;
    private final TaskController taskController = new TaskController();

    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private Tasks() {
    }

    private Boolean checkRole() {
        Role role = UserSession.getInstance().getLoggedInUser().getRole();
        return role == Role.MANAGER || role == Role.ADMIN;
    }

    @Override
    public CompletableFuture<Boolean> initializePage() {

        taskController.setExecutor(executor);

        return CompletableFuture.supplyAsync(this::checkRole, executor)
                .thenComposeAsync(roleAllowed -> roleAllowed ?
                                initializeManagerControls() : CompletableFuture.completedFuture(null),
                        executor)
                .thenApply(v -> {
                    this.tasksTable = taskController.getTasksTable();
                    this.tasksTable.getStyleClass().add("tasksTableView");
                    return tasksTable;
                })
                .thenComposeAsync(table -> {
                    if (table == null) {
                        return CompletableFuture.completedFuture(false);
                    }
                    return taskController.loadReceivedTasks().thenApply(v -> true);
                }, executor)
                .thenApplyAsync(v -> {
                    Platform.runLater(() -> {
                        buttonBar.getButtons().add(refreshBtn);
                        this.getChildren().addAll(buttonBar, tasksTable);
                    });
                    return true;
                })
                .exceptionally(e -> false);
    }

    private CompletableFuture<Void> initializeManagerControls() {

        return CompletableFuture.runAsync(() -> {
            newTaskBtn = new Button("New Task");
            receivedTasksBtn = new Button("Received Tasks");
            wroteTasksBtn = new Button("Wrote Tasks");

            buttonBar.getButtons().addAll(receivedTasksBtn, wroteTasksBtn);

            newTaskBtn.setOnAction(evt -> new TaskDialog().createTask());
            wroteTasksBtn.setOnAction(evt -> {
                taskController.loadWroteTasks();
                refreshBtn.setOnAction(e -> taskController.loadWroteTasks());
            });
            receivedTasksBtn.setOnAction(evt -> {
                taskController.loadReceivedTasks();
                refreshBtn.setOnAction(e -> taskController.loadReceivedTasks());
            });

            Platform.runLater(() -> this.getChildren().addAll(newTaskBtn));
        });
    }

    @Override
    public Boolean loadStyleClass() {
        return LoadStyles.loadTasksSceneStyle(TeamLinkApp.getScene(MainScene.class));
    }

    @Override
    public void loadStyles() {

        Platform.runLater(() -> {
            this.setPadding(new Insets(20, 20, 20, 20));
            this.getStyleClass().add("tasksForm");

            List<Node> elements = new ArrayList<>(List.of(refreshBtn));

            if (UserSession.getInstance().getLoggedInUser().getRole().getId() > 0) {
                elements.addAll(List.of(receivedTasksBtn, newTaskBtn, wroteTasksBtn));
            }
            elements.forEach(e -> e.getStyleClass().add("tasksBtn"));
        });
    }

    @Override
    public Parent asParent() {
        return this;
    }
}