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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;


public class UserSelectionDialog {

    private Scene scene;
    private final Stage stage = new Stage(StageStyle.UTILITY);
    private Person selectedPerson = null;
    private TableView<Person> tableView = new TableView<>();
    private ObservableList<Person> usersData = FXCollections.observableArrayList();
    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    public UserSelectionDialog() {
        Platform.runLater(() -> stage.initModality(Modality.APPLICATION_MODAL));
    }

    public Person useDialog() {

        showUserSelectionDialog();
        return selectedPerson;
    }

    private void showUserSelectionDialog() {

        configureTableView();
        loadUsers().thenAccept(users -> Platform.runLater(() -> {
                    usersData.setAll(users);
                    tableView.setItems(usersData);
                })
        ).exceptionally(e -> null).join();

        scene = new Scene(tableView, 400, 400);
        loadStyles();

        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setTitle("Select User");
            stage.showAndWait();
        });
    }

    private void configureTableView() {

        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");

        firstNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLastName()));

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

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(List.of(firstNameCol, lastNameCol));
    }

    private CompletableFuture<List<Person>> loadUsers() {
        return CompletableFuture.supplyAsync(UserSession::getUsersFromGroups, executor);
    }

    private void loadStyles() {

        URL resource = getClass().getResource("/View/Styles/Dialogs/UserSelectionDialog.css");
        if (resource == null) {
            return;
        }

        scene.getStylesheets().add(resource.toExternalForm());

        Platform.runLater(() ->
                tableView.getStyleClass().add("userSelectionDialogTableView"));
    }

    public void close() {

        tableView = null;
        usersData = null;
        ExecutorServiceManager.shutDownThis(this.getClass().getSimpleName());
        stage.close();
    }
}