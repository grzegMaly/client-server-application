package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks;

import application.portfolio.clientmodule.Config.LoadStyles;
import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Task.Task;
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
    private TableView<Task> tasksTable = new TableView<>();
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
                .exceptionally(e -> {
                    //Todo: Make it Custom
                    System.out.println("Error in " + this.getClass().getSimpleName() + ", initializePage");
                    e.printStackTrace();
                    return false;
                });
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
        this.getStyleClass().add("baseBG");
    }

    @Override
    public Parent asParent() {
        return this;
    }
}