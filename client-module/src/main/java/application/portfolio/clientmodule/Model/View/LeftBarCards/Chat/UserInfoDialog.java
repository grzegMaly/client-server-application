package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequestViewModel;
import application.portfolio.clientmodule.Model.Model.Person.Person;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;

public class UserInfoDialog {

    private final Person person;
    private Person supervisor = null;

    private final Stage stage = new Stage(StageStyle.UTILITY);
    private final GridPane gp = new GridPane();

    private final Label nameLbl1 = new Label("Name:");
    private final Label nameLbl2 = new Label();
    private final Label supervisorLbl1 = new Label("Supervisor:");
    private final Label supervisorLbl2 = new Label();
    private final Label roleLbl1 = new Label("Role:");
    private final Label roleLbl2 = new Label();
    private Scene scene;

    private final List<Label> elements = List.of(nameLbl1, nameLbl2, supervisorLbl1,
            supervisorLbl2, roleLbl1, roleLbl2);

    public UserInfoDialog(Person person) {
        this.person = person;

        Platform.runLater(() -> {
            scene = new Scene(gp, 400, 200);
            getSupervisor();
            bindFields();
            completeGp();
            loadStyles();
        });
    }

    public void showDialog() {

        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Info about " + person.getName());
            stage.show();
        });
    }

    private void completeGp() {

        for (int i = 0; i < elements.size(); i += 2) {
            gp.add(elements.get(i), 0, i / 2);
            gp.add(elements.get(i + 1), 1, i / 2);
        }

        gp.setAlignment(Pos.CENTER);
        gp.setHgap(10);
        gp.setVgap(10);
    }

    private void getSupervisor() {

        int role = UserSession.getInstance().getLoggedInUser().getRole().getId();
        supervisor = FriendsRequestViewModel.getFriends()
                .stream()
                .filter(f -> f.getRole().getId() > role)
                .findFirst()
                .orElse(null);

    }

    private void bindFields() {

        nameLbl2.setText(person.getName());
        if (supervisor == person) {
            supervisor = null;
        }
        supervisorLbl2.setText(supervisor == null ? "Unknown" : supervisor.getName());
        roleLbl2.setText(person.getRole().toString());
    }

    private void loadStyles() {

        URL resourceUrl = getClass().getResource("/View/Styles/Dialogs/ChatInfoDialog.css");
        if (resourceUrl == null) {
            return;
        }
        scene.getStylesheets().add(resourceUrl.toExternalForm());
        gp.getStyleClass().add("chatInfoDialogGB");
        elements.forEach(e -> e.getStyleClass().add("chatInfoDialogLbl"));
    }

    public void setOnHidden(Button infoBtn) {
        stage.setOnHidden(e -> infoBtn.setDisable(false));
    }
}
