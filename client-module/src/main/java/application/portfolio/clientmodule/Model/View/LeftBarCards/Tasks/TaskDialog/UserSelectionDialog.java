package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequestViewModel;
import application.portfolio.clientmodule.Model.Model.Group.GroupDAO;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
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
import java.util.stream.Collectors;


public class UserSelectionDialog extends Stage {

    private PersonDAO selectedPerson = null;
    private final PersonDAO loggedInUser = UserSession.getInstance().getLoggedInUser();
    private TableView<PersonDAO> tableView = new TableView<>();
    private ObservableList<PersonDAO> usersData = FXCollections.observableArrayList();
    private final ExecutorService executor =
            ExecutorServiceManager.createCachedThreadPool(this.getClass().getSimpleName());

    private UserSelectionDialog() {
        this.setOnCloseRequest(evt -> {
            close();
            evt.consume();
        });
    }

    public static PersonDAO getSelectedPerson() {

        UserSelectionDialog dialog = new UserSelectionDialog();
        dialog.showUserSelectionDialog();

        return dialog.selectedPerson;
    }

    private void showUserSelectionDialog() {

        configureTableView();
        loadUsers().thenAccept(users -> Platform.runLater(() -> {
                    usersData.addAll(users);
                    tableView.setItems(usersData);
                })
        ).exceptionally(e -> {
            //Todo: Make it Custom
            System.out.println("Error in " + this.getClass().getSimpleName() + ", initializePage");
            e.printStackTrace();
            return null;
        });

        VBox layout = new VBox(tableView);
        Scene scene = new Scene(layout, 400, 400);
        this.setScene(scene);
        this.setTitle("Select User");
        this.showAndWait();
    }

    private void configureTableView() {

        TableColumn<PersonDAO, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));

        TableColumn<PersonDAO, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));

        TableColumn<PersonDAO, String> groupNamesCol = new TableColumn<>("Group Name");
        groupNamesCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getGroups().stream()
                        .map(GroupDAO::getGroupName)
                        .collect(Collectors.joining(", "))
        ));

        tableView.setRowFactory(evt -> {
            TableRow<PersonDAO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    selectedPerson = row.getItem();
                    this.close();
                }
            });
            return row;
        });

        tableView.getColumns().addAll(List.of(firstNameCol, lastNameCol, groupNamesCol));
    }

    private CompletableFuture<List<PersonDAO>> loadUsers() {

        return CompletableFuture.supplyAsync(() -> {
            List<PersonDAO> persons = FriendsRequestViewModel.getFriends();
            persons.add(loggedInUser);
            return persons;
        }, executor);
    }

    @Override
    public void close() {

        tableView = null;
        usersData = null;

        ExecutorServiceManager.shutDownThis(this.getClass().getSimpleName());
        super.close();
    }
}