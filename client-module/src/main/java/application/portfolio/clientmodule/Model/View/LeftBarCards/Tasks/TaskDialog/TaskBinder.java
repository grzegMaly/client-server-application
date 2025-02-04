package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Task.TaskRequestViewModel;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.Model.Model.Task.TaskDAO;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TaskBinder {

    private TaskDialog.Operation operation;
    private PersonDAO selectedUser;
    PersonDAO actualUser = UserSession.getInstance().getLoggedInUser();
    private TaskDAO taskDAO;
    private Button saveBtn;

    private final TaskRequestViewModel viewModel = new TaskRequestViewModel();
    private final Set<Runnable> binds = new HashSet<>();
    private final Set<Runnable> clearSet = new HashSet<>();

    public TaskBinder() {
    }

    public void withTaskDAO(TaskDAO task) {
        this.taskDAO = task;
    }

    public void withOpenOperation(TaskDialog.Operation operation) {
        this.operation = operation;
    }

    public void withTitle(Label titleLbl) {

        binds.add(() -> {

            titleLbl.setText(taskDAO.getTitle());
            clearSet.add(() -> titleLbl.setText(null));

            if (operation == TaskDialog.Operation.READ_MODIFY) {
                viewModel.titleProperty().bindBidirectional(titleLbl.textProperty());
                clearSet.add(() -> viewModel.titleProperty().unbindBidirectional(titleLbl.textProperty()));
            }
        });
    }

    public void withTitle(TextField titleTf) {
        binds.add(() -> titleTf.textProperty().bindBidirectional(viewModel.titleProperty()));
        clearSet.add(() -> titleTf.textProperty().unbindBidirectional(viewModel.titleProperty()));
    }

    public void withAuthor(Label authorLbl) {

        binds.add(() -> {

            authorLbl.setText(taskDAO != null ?
                    taskDAO.getAssignedBy().getName() : actualUser.getName());
            clearSet.add(() -> authorLbl.setText(null));

            if (operation != TaskDialog.Operation.READ) {
                viewModel.setAssignedById(actualUser.getId().toString());
                clearSet.add(() -> viewModel.setAssignedById(null));
            }
        });
    }

    public void withAssignedTo(Label assignedToLbl) {

        binds.add(() -> {

            selectedUser = taskDAO.getAssignedTo();
            assignedToLbl.setText(selectedUser.getName());
            clearSet.add(() -> assignedToLbl.setText(null));
        });
    }

    public void withAssignedTo(TextField searchTf, Button searchBtn) {

        binds.add(() -> {

            searchTf.setText(selectedUser != null ? selectedUser.getName() : null);
            clearSet.add(searchTf::clear);

            searchBtn.setOnAction(evt -> {
                PersonDAO personDAO = UserSelectionDialog.getSelectedPerson();
                if (personDAO != null) {
                    selectedUser = personDAO;
                    searchTf.setText(selectedUser.getName());
                    viewModel.setAssignedToId(selectedUser.getId().toString());
                    clearSet.add(() -> viewModel.setAssignedById(null));
                }
            });
        });
    }

    public void withCreatedDate(Label createdDateLbl) {

        binds.add(() -> {

            if (operation != TaskDialog.Operation.CREATE) {
                createdDateLbl.setText(taskDAO.getCreatedDate().toString());
                clearSet.add(() -> createdDateLbl.setText(null));
            }

            if (operation != TaskDialog.Operation.READ) {

                viewModel.setCreatedDate(taskDAO != null ?
                        taskDAO.getCreatedDate().toString() : LocalDate.now().toString());
                createdDateLbl.textProperty().bindBidirectional(viewModel.createdDateProperty());
                clearSet.addAll(List.of(() -> viewModel.setCreatedDate(null),
                        () -> createdDateLbl.textProperty().unbindBidirectional(viewModel.createdDateProperty())));
            }
        });
    }

    public void withDeadline(DatePicker deadlineDp) {

        binds.add(() -> {
            DateTimeFormatter saveFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (viewModel.deadlineProperty().get() == null || viewModel.deadlineProperty().get().isEmpty()) {
                String savedDate = LocalDate.now().format(saveFormatter);
                viewModel.setDeadline(savedDate);
                deadlineDp.setValue(LocalDate.parse(viewModel.getDeadline(), saveFormatter));
                clearSet.add(() -> deadlineDp.setValue(null));
            }

            deadlineDp.valueProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    String savedDate = newV.format(saveFormatter);
                    viewModel.setDeadline(savedDate);
                }
            });

            clearSet.add(() -> viewModel.setDeadline(null));
        });
    }

    public void withDeadline(Label deadline) {

        binds.add(() -> deadline.setText(taskDAO.getDeadline().toString()));
    }

    public void withDescription(TextArea descriptionTa) {

        binds.add(() -> {

            if (operation != TaskDialog.Operation.CREATE) {
                descriptionTa.setText(taskDAO.getDescription());
            }

            if (operation != TaskDialog.Operation.READ) {

                if (taskDAO != null) {
                    viewModel.setDescription(taskDAO.getDescription());
                    clearSet.add(() -> viewModel.setDescription(null));
                }
                descriptionTa.textProperty().bindBidirectional(viewModel.descriptionProperty());
            }

            clearSet.addAll(List.of(descriptionTa::clear,
                    () -> descriptionTa.textProperty().unbindBidirectional(viewModel.descriptionProperty())));
        });
    }

    public void withSaveBtn(Button saveBtn, Runnable saveAction) {

        this.saveBtn = saveBtn;
        this.saveBtn.setOnMouseClicked(evt -> saveAction.run());

        binds.add(() -> this.saveBtn.setOnAction(evt -> {
            if (validate()) {
                viewModel.save();
            }
        }));
    }

    private boolean validate() {

        return true;
    }

    public void bind() {

        binds.forEach(Runnable::run);
    }

    public void clearFields() {
        clearSet.forEach(Runnable::run);
    }
}