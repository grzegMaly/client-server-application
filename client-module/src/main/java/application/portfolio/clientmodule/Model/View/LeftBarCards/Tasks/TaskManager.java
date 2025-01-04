package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import application.portfolio.clientmodule.OtherElements.TaskDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskManager {

    private static final PersonDAO loggedInUser;

    static {
        loggedInUser = UserSession.getInstance().getLoggedInUser();
    }

    //Todo: Connect to server in the Future
    public static ObservableList<TaskDAO> loadReceivedTasks() {
        /*return FXCollections.observableArrayList(
                Objects.getTasks().stream()
                        .filter(t -> t.getAssignedTo().equals(loggedInUser))
                        .toList()
        );*/

        return FXCollections.emptyObservableList();
    }

    //Todo: Connect to server in the Future
    public static ObservableList<TaskDAO> loadWroteTasks() {
        /*return FXCollections.observableArrayList(
                Objects.getTasks().stream()
                        .filter(t -> t.getAssignedBy().equals(loggedInUser))
                        .toList()
        );*/

        return FXCollections.emptyObservableList();
    }
}