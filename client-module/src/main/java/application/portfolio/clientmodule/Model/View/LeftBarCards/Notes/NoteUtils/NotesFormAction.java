package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;

public class NotesFormAction {

    private ComboBox<String> typeCb;
    private ComboBox<String> categoryCb;
    private StackPane changingCheckBoxes;
    private Label deadlineLbl;
    private DatePicker datePicker;

    private ChangeListener<String> listener;

    public void setTypeCb(ComboBox<String> typeCb) {
        this.typeCb = typeCb;
    }

    public void setCategoryCb(ComboBox<String> categoryCb) {
        this.categoryCb = categoryCb;
    }

    public void setChangingCheckBoxes(StackPane changingCheckBoxes) {
        this.changingCheckBoxes = changingCheckBoxes;
    }

    public void setDeadlineLbl(Label deadlineLbl) {
        this.deadlineLbl = deadlineLbl;
        this.deadlineLbl.setVisible(false);
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;

        this.datePicker.setDayCellFactory(callback -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb");
                }
            }
        });
        this.datePicker.setVisible(false);
    }

    public void useListener() {

        if (listener == null) {
            listener = getListener();
        }

        typeCb.valueProperty().addListener(listener);
    }

    private ChangeListener<String> getListener() {
        return ((observableValue, oldVal, newVal) -> {
            if (newVal != null) {
                NoteDAO.NoteType type = NoteDAO.stringToEnum(NoteDAO.NoteType.class, newVal);
                updateFormVisibility(type);
            }
        });
    }

    private void updateFormVisibility(NoteDAO.NoteType type) {
        if (type.equals(NoteDAO.NoteType.REGULAR_NOTE)) {
            deadlineLbl.setVisible(false);
            datePicker.setVisible(false);
            changingCheckBoxes.getChildren().forEach(e -> e.setVisible(e == categoryCb));
        } else {
            deadlineLbl.setVisible(true);
            datePicker.setVisible(true);
            changingCheckBoxes.getChildren().forEach(e -> e.setVisible(e != categoryCb));
        }
    }
}
