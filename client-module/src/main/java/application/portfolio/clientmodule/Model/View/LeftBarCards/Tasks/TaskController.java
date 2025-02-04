package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks;

import application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog.TaskDialog;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.Model.Model.Task.TaskDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.util.List;

public class TaskController {

    public static TableView<TaskDAO> getTaskTable() {

        TableView<TaskDAO> tasksTable = new TableView<>();

        TableColumn<TaskDAO, String> titleCol = new TableColumn<>("Title");
        TableColumn<TaskDAO, String> authorCol = new TableColumn<>("Author");
        TableColumn<TaskDAO, String> deadlineCol = new TableColumn<>("Deadline");
        TableColumn<TaskDAO, String> descriptionCol = new TableColumn<>("Description");

        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorCol.setCellValueFactory(cellData -> {
            PersonDAO author = cellData.getValue().getAssignedBy();
            String fullName = author.getFirstName() + " " + author.getLastName();
            return new SimpleStringProperty(fullName);
        });
        deadlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline().toString()));
        descriptionCol.setCellValueFactory(celLData -> new SimpleStringProperty(celLData.getValue().getDescription()));

        tasksTable.getColumns().addAll(List.of(titleCol, authorCol, deadlineCol, descriptionCol));
        tasksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadBehavior(tasksTable);

        return tasksTable;
    }

    private static void loadBehavior(TableView<TaskDAO> tasksTable) {

        tasksTable.setRowFactory(evt -> {
            TableRow<TaskDAO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1 &&
                        (!row.isEmpty())) {

                    TaskDAO task = row.getItem();
                    TaskDialog.Operation operation = TaskDialog.Operation.determineOperation(task);
                    TaskDialog taskDialog = new TaskDialog();
                    taskDialog.useDialog(task, operation);
                }
            });
            return row;
        });
    }

    public static void loadReceivedTasks(TableView<TaskDAO> tasksTable) {

        tasksTable.getItems().clear();
        ObservableList<TaskDAO> tasks = TaskManager.loadReceivedTasks();
        tasksTable.getItems().addAll(tasks);
    }

    public static void loadWroteTasks(TableView<TaskDAO> tasksTable) {

        tasksTable.getItems().clear();
        ObservableList<TaskDAO> tasks = TaskManager.loadWroteTasks();
        tasksTable.getItems().addAll(tasks);
    }
}