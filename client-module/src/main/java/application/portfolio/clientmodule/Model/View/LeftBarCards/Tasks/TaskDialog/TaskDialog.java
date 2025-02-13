package application.portfolio.clientmodule.Model.View.LeftBarCards.Tasks.TaskDialog;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import application.portfolio.clientmodule.Model.Model.Person.Role;
import application.portfolio.clientmodule.Model.Model.Task.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;


public class TaskDialog extends Stage {

    public enum Operation {
        READ, READ_MODIFY, CREATE;

        public static Operation determineOperation(Task task) {
            PersonDAO person = UserSession.getInstance().getLoggedInUser();
            if (task == null) return CREATE;
            return person.getRole().equals(Role.EMPLOYEE) ? READ :
                    (person.getId().equals(task.getCreatedBy().getId()) ? READ_MODIFY : READ);
        }
    }

    private final GridPane gp = new GridPane();

    private final ButtonBar buttons = new ButtonBar();
    private final Button cancelBtn = new Button("Cancel");
    private Button editBtn = null;
    private Button saveBtn = null;
    private TextField titleTf = null;
    private HBox searchStruct = null;
    private final DatePicker deadlineDp = new DatePicker();


    private final Label titleLbl1 = new Label("Title:");
    private final Label authorLbl1 = new Label("Author:");
    private final Label assignedToLbl1 = new Label("Assigned To:");
    private final Label createdDateLbl1 = new Label("Created Date:");
    private final Label deadlineLbl1 = new Label("Deadline:");
    private final Label descriptionLbl = new Label("Description");


    private final Label titleLbl2 = new Label();
    private final StackPane titleSp = new StackPane();

    private final Label authorLbl2 = new Label();

    private final Label assignedToLbl2 = new Label();
    private final StackPane assignedToSp = new StackPane();

    private final Label createdDateLbl2 = new Label();

    private final Label deadlineLbl2 = new Label();
    private final StackPane deadlineSp = new StackPane();

    private final TextArea descriptionTa = new TextArea();

    private final List<Node> baseElements = List.of(titleLbl1, titleSp, authorLbl1, authorLbl2,
            assignedToLbl1, assignedToSp, createdDateLbl1, createdDateLbl2, deadlineLbl1,
            deadlineSp, descriptionLbl, descriptionTa);

    private Operation openOperation = null;
    private TaskBinder taskBinder = new TaskBinder();


    public TaskDialog() {
        this.setOnCloseRequest(evt -> {
            close();
            evt.consume();
        });
    }

    public void createTask() {
        useDialog(null, Operation.CREATE);
    }

    public void useDialog(Task task, Operation operation) {

        this.openOperation = operation;

        taskBinder.withOpenOperation(openOperation);
        taskBinder.withTask(task);

        initGP();
        boundSizeProperties();

        taskBinder.bind();
        this.showAndWait();
    }

    private void initGP() {

        buttons.getButtons().add(cancelBtn);
        gp.add(buttons, 0, 0, 2, 1);
        GridPane.setHalignment(buttons, HPos.LEFT);

        initBaseComponents();

        if (openOperation == Operation.READ) {
            initReadComponents();
        } else {
            initModifyComponents();
        }

        setButtonsActions(openOperation);
    }

    private void initBaseComponents() {

        taskBinder.withAuthor(authorLbl2);
        taskBinder.withDescription(descriptionTa);
        taskBinder.withCreatedDate(createdDateLbl2);

        int rowIndex = 1;
        for (int i = 0; i < baseElements.size(); i+=2) {

            Node secondElement = baseElements.get(i + 1);
            if (secondElement == descriptionTa) {
                gp.add(secondElement, 0, rowIndex + 1, 2, 1);
            } else {
                gp.add(baseElements.get(i), 0, rowIndex);
                gp.add(secondElement, 1, rowIndex);
                rowIndex++;
            }
        }

        GridPane.setVgrow(descriptionTa, Priority.ALWAYS);

        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(10));
    }

    private void initReadComponents() {

        taskBinder.withTitle(titleLbl2);
        taskBinder.withAssignedTo(assignedToLbl2);
        taskBinder.withDeadline(deadlineLbl2);

        titleSp.getChildren().add(titleLbl2);
        assignedToSp.getChildren().add(assignedToLbl2);
        deadlineSp.getChildren().add(deadlineLbl2);

        descriptionTa.setEditable(false);
    }

    private void initModifyComponents() {

        if (openOperation == Operation.READ_MODIFY) {
            initReadComponents();
            loadModifyComponents();
        } else {
            loadModifyComponents();
        }
    }

    private void swapVisible(boolean visibility) {

        if (openOperation == Operation.READ_MODIFY) {
            titleLbl2.setVisible(!visibility);
            assignedToLbl2.setVisible(!visibility);
            deadlineLbl2.setVisible(!visibility);
        }

        titleTf.setVisible(visibility);
        searchStruct.setVisible(visibility);
        deadlineDp.setVisible(visibility);
    }

    private void loadModifyComponents() {

        Runnable saveAction;
        saveBtn = new Button("Save");

        titleTf = new TextField();
        taskBinder.withTitle(titleTf);

        TextField searchTf = new TextField();
        searchTf.setEditable(false);
        Button searchBtn = new Button("≡");
        searchStruct = new HBox(searchTf, searchBtn);
        taskBinder.withAssignedTo(searchTf, searchBtn);

        deadlineDp.setEditable(false);
        taskBinder.withDeadline(deadlineDp);


        if (openOperation == Operation.READ_MODIFY) {

            swapVisible(false);
            editBtn = new Button("Edit");
            saveBtn.setVisible(false);
            buttons.getButtons().addAll(editBtn, saveBtn);

            saveAction = () -> {
                swapVisible(false);
                editBtn.setVisible(true);
                saveBtn.setVisible(false);
                descriptionTa.setEditable(false);
            };
        } else {

            saveAction = this::close;
            buttons.getButtons().addAll(saveBtn);
        }

        taskBinder.withSaveBtn(saveBtn, saveAction);

        titleSp.getChildren().add(titleTf);
        assignedToSp.getChildren().add(searchStruct);
        deadlineSp.getChildren().add(deadlineDp);
    }

    private void setButtonsActions(Operation operation) {

        cancelBtn.setOnAction(evt -> this.close());

        if (operation == Operation.READ_MODIFY) {
            editBtn.setOnAction(evt -> {
                swapVisible(true);
                descriptionTa.setEditable(true);
                saveBtn.setVisible(true);
                editBtn.setVisible(false);
            });
        }
    }

    private void boundSizeProperties() {
        Screen screen = Screen.getPrimary();
        double width = screen.getBounds().getWidth();
        double windowWidth = width * 0.20;

        Scene scene = new Scene(gp, windowWidth, 500);

        this.setScene(scene);
        this.setMinWidth(windowWidth);
        this.setMaxWidth(windowWidth);
    }

    @Override
    public void close() {
        taskBinder.clearFields();
        taskBinder = null;
        super.close();
    }
}