package application.portfolio.clientmodule.Model.View.LeftBarCards.Notes.NoteUtils;

import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import application.portfolio.clientmodule.Model.Model.Notes.NoteType;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class InfoDialogActions {

    private Label categoryLbl;
    private StackPane categoryPriorityLblSp;
    private StackPane categoryPriorityCbSp;
    private StackPane deadlineSp;
    private ComboBox<String> typeCb;
    private ComboBox<String> categoryCb;
    private ComboBox<String> priorityCb;
    private Label deadlineLbl;
    private Label priorityLbl;
    private DatePicker datePicker;
    private ChangeListener<String> listener;

    public void setCategoryLbl(Label categoryLbl) {
        this.categoryLbl = categoryLbl;
    }

    public void setCategoryPriorityLblSp(StackPane sp) {
        this.categoryPriorityLblSp = sp;
    }

    public void setCategoryPriorityCbSp(StackPane sp) {
        this.categoryPriorityCbSp = sp;
    }

    public void setDeadlineSp(StackPane sp) {
        this.deadlineSp = sp;
    }

    public void setTypeCb(ComboBox<String> typeCb) {
        this.typeCb = typeCb;
    }

    public void setCategoryCb(ComboBox<String> categoryCb) {
        this.categoryCb = categoryCb;
    }

    public void setPriorityCb(ComboBox<String> priorityCb) {
        this.priorityCb = priorityCb;
    }

    public void setDeadlineLbl(Label deadlineLbl) {
        this.deadlineLbl = deadlineLbl;
    }

    public void setPriorityLbl(Label priorityLbl1) {
        this.priorityLbl = priorityLbl1;
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
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
                NoteType type = NoteDAO.stringToEnum(NoteType.class, newVal);
                if (type.equals(NoteType.REGULAR_NOTE)) {
                    swapForListener(categoryLbl, categoryCb);
                    deadlineLbl.setVisible(false);
                    deadlineSp.setVisible(false);
                    deadlineSp.getChildren().forEach(e -> e.setVisible(false));
                } else if (type.equals(NoteType.DEADLINE_NOTE)) {
                    swapForListener(priorityLbl, priorityCb);
                    deadlineLbl.setVisible(true);
                    deadlineSp.setVisible(true);
                    deadlineSp.getChildren().forEach(e -> e.setVisible(e.equals(datePicker)));
                }
            }
        });
    }

    private void swapForListener(Label newLbl, ComboBox<String> newCb) {
        categoryPriorityLblSp.getChildren().forEach(e -> e.setVisible(e.equals(newLbl)));
        categoryPriorityCbSp.getChildren().forEach(e -> {
            StackPane sp = (StackPane) e;
            if (!sp.isVisible()) {
                sp.getChildren().forEach(s -> s.setVisible(s.equals(newCb)));
                sp.setVisible(true);
            } else {
                sp.setVisible(false);
            }
        });
    }
}
