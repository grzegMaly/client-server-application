package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks;

import application.portfolio.clientmodule.Model.Model.Task.Task;
import application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog.TaskDialog;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class TaskController {

    private TableView<Task> tasksTable;
    private final TaskManager taskManager = new TaskManager();
    private ExecutorService executor;

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public TableView<Task> getTasksTable() {

        tasksTable = new TableView<>();
        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        TableColumn<Task, String> authorCol = new TableColumn<>("Author");
        TableColumn<Task, String> deadlineCol = new TableColumn<>("Deadline");
        TableColumn<Task, String> descriptionCol = new TableColumn<>("Description");

        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorCol.setCellValueFactory(cellData -> {
            Person author = cellData.getValue().getCreatedBy();
            String fullName = author.getFirstName() + " " + author.getLastName();
            return new SimpleStringProperty(fullName);
        });
        deadlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline().toString()));
        descriptionCol.setCellValueFactory(celLData -> new SimpleStringProperty(celLData.getValue().getDescription()));

        tasksTable.getColumns().addAll(List.of(titleCol, authorCol, deadlineCol, descriptionCol));
        tasksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        CompletableFuture.runAsync(this::loadBehavior, executor);
        return tasksTable;
    }

    private void loadBehavior() {

        tasksTable.setRowFactory(evt -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1 &&
                        (!row.isEmpty())) {

                    Task task = row.getItem();
                    TaskDialog.Operation operation = TaskDialog.Operation.determineOperation(task);
                    TaskDialog taskDialog = new TaskDialog();
                    taskDialog.useDialog(task, operation);
                }
            });
            return row;
        });
    }

    public CompletableFuture<Void> loadReceivedTasks() {

        return CompletableFuture.supplyAsync(taskManager::loadReceivedTasks, executor)
                .thenAccept(tasks -> Platform.runLater(() ->
                        tasksTable.getItems().setAll(tasks)));
    }

    public void loadWroteTasks() {

        CompletableFuture.supplyAsync(taskManager::loadWroteTasks, executor)
                .thenAccept(tasks -> Platform.runLater(() ->
                        tasksTable.getItems().setAll(tasks)));
    }
}