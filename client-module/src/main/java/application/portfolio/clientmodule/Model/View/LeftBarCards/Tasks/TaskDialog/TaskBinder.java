package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Task.Task;
import application.portfolio.clientmodule.Model.Request.Task.TaskRequestViewModel;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TaskBinder {

    private TaskDialog.Operation operation;
    private PersonDAO selectedUser;
    private final PersonDAO actualUser = UserSession.getInstance().getLoggedInUser();
    private Task task;
    private Button saveBtn;

    private final TaskRequestViewModel viewModel = new TaskRequestViewModel();
    private final Set<Runnable> binds = new HashSet<>();
    private final Set<Runnable> clearSet = new HashSet<>();
    private TextField titleTf;
    private TextArea descriptionTa;

    public TaskBinder() {
    }

    public void withTask(Task task) {
        this.task = task;
    }

    public void withOpenOperation(TaskDialog.Operation operation) {
        this.operation = operation;
    }

    public void withTitle(Label titleLbl) {

        binds.add(() -> {

            titleLbl.setText(task.getTitle());
            clearSet.add(() -> titleLbl.setText(null));

            if (operation == TaskDialog.Operation.READ_MODIFY) {
                viewModel.titleProperty().bindBidirectional(titleLbl.textProperty());
                clearSet.add(() -> viewModel.titleProperty().unbindBidirectional(titleLbl.textProperty()));
            }
        });
    }

    public void withTitle(TextField titleTf) {

        this.titleTf = titleTf;

        binds.add(() -> this.titleTf.textProperty().bindBidirectional(viewModel.titleProperty()));
        clearSet.add(() -> this.titleTf.textProperty().unbindBidirectional(viewModel.titleProperty()));
    }

    public void withAuthor(Label authorLbl) {

        binds.add(() -> {

            authorLbl.setText(task != null ?
                    task.getCreatedBy().getName() : actualUser.getName());
            clearSet.add(() -> authorLbl.setText(null));

            if (operation != TaskDialog.Operation.READ) {
                viewModel.setCreatedBy(actualUser.getId().toString());
                clearSet.add(() -> viewModel.setCreatedBy(null));
            }
        });
    }

    public void withAssignedTo(Label assignedToLbl) {

        binds.add(() -> {

            selectedUser = task.getAssignedTo();
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
                    viewModel.setAssignedTo(selectedUser.getId().toString());
                    clearSet.add(() -> viewModel.setCreatedBy(null));
                }
            });
        });
    }

    public void withCreatedDate(Label createdDateLbl) {

        binds.add(() -> {

            if (operation != TaskDialog.Operation.CREATE) {
                createdDateLbl.setText(task.getCreatedAt().toString());
                clearSet.add(() -> createdDateLbl.setText(null));
            }

            if (operation != TaskDialog.Operation.READ) {

                viewModel.setCreatedDate(task != null ?
                        task.getCreatedAt().toString() : LocalDateTime.now().withNano(0).toString());
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

        binds.add(() -> deadline.setText(task.getDeadline().toString()));
    }

    public void withDescription(TextArea descriptionTa) {

        this.descriptionTa = descriptionTa;

        binds.add(() -> {

            if (operation != TaskDialog.Operation.CREATE) {
                this.descriptionTa.setText(task.getDescription());
            }

            if (operation != TaskDialog.Operation.READ) {

                if (task != null) {
                    viewModel.setDescription(task.getDescription());
                    clearSet.add(() -> viewModel.setDescription(null));
                }
                this.descriptionTa.textProperty().bindBidirectional(viewModel.descriptionProperty());
            }

            clearSet.addAll(List.of(this.descriptionTa::clear,
                    () -> this.descriptionTa.textProperty().unbindBidirectional(viewModel.descriptionProperty())));
        });
    }

    public void withSaveBtn(Button saveBtn, Runnable saveAction) {

        this.saveBtn = saveBtn;
        binds.add(() -> this.saveBtn.setOnAction(evt -> {
            if (!validateElements()) {
                return;
            }
            viewModel.save();
            saveAction.run();
        }));
    }

    private boolean validateElements() {

        if (titleTf.getText() == null || titleTf.getText().isBlank()) {
            return false;
        }

        if (descriptionTa.getText() == null || descriptionTa.getText().isBlank()) {
            descriptionTa.getStyleClass().add("badElement");
            return false;
        }
        return true;
    }

    public void bind() {
        binds.forEach(Runnable::run);
    }

    public void clearFields() {
        clearSet.forEach(Runnable::run);
    }
}