package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import application.portfolio.clientmodule.utils.ExecutorServiceManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;


public class UserSelectionDialog extends Stage {

    private Person selectedPerson = null;
    private TableView<Person> tableView = new TableView<>();
    private ObservableList<Person> usersData = FXCollections.observableArrayList();
    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private UserSelectionDialog() {
        this.setOnCloseRequest(evt -> {
            close();
            evt.consume();
        });
    }

    public static Person getSelectedPerson() {

        UserSelectionDialog dialog = new UserSelectionDialog();
        dialog.showUserSelectionDialog();

        return dialog.selectedPerson;
    }

    private void showUserSelectionDialog() {

        configureTableView();
        loadUsers().thenAccept(users -> Platform.runLater(() -> {
                    usersData.setAll(users);
                    tableView.setItems(usersData);
                })
        ).exceptionally(e -> {
            System.out.println("Error in " + this.getClass().getSimpleName() + ", initializePage");
            return null;
        });

        VBox layout = new VBox(tableView);
        Scene scene = new Scene(layout, 400, 400);
        this.setScene(scene);
        this.setTitle("Select User");
        this.showAndWait();
    }

    private void configureTableView() {

        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));

        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));

        tableView.setRowFactory(evt -> {
            TableRow<Person> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    selectedPerson = row.getItem();
                    this.close();
                }
            });
            return row;
        });

        tableView.getColumns().addAll(List.of(firstNameCol, lastNameCol));
    }

    private CompletableFuture<List<Person>> loadUsers() {
        return CompletableFuture.supplyAsync(UserSession::getUsersFromGroups, executor);
    }

    @Override
    public void close() {

        tableView = null;
        usersData = null;


        ExecutorServiceManager.shutDownThis(this.getClass().getSimpleName());
        super.close();
    }
}