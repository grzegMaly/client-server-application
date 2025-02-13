package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.Model.Model.Task.Task;
import application.portfolio.clientmodule.Model.Model.Task.TaskDAO;
import application.portfolio.clientmodule.Model.Request.Task.TaskRequestViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final PersonDAO loggedInUser;
    private final TaskRequestViewModel viewModel = new TaskRequestViewModel();

    {
        loggedInUser = UserSession.getInstance().getLoggedInUser();
    }

    public TaskManager() {
    }

    public ObservableList<Task> loadReceivedTasks() {

        List<Task> daoList = viewModel.loadReceivedTasks(loggedInUser);
        return FXCollections.observableList(daoList);
    }

    public ObservableList<Task> loadWroteTasks() {
        List<Task> daoList = viewModel.loadCreatedTasks(loggedInUser);
        return FXCollections.observableList(daoList);
    }
}