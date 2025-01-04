package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequest.FriendsRequest;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequestModel;
import application.portfolio.clientmodule.Model.Request.Chat.Friends.FriendsRequestViewModel;
import application.portfolio.clientmodule.OtherElements.PersonDAO;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;

public class UserInfoDialog extends Stage {

    private PersonDAO person = null;
    private PersonDAO supervisor = null;
    private static UserInfoDialog instance = null;

    private final GridPane gp = new GridPane();

    private final Label nameLbl1 = new Label("Name:");
    private final Label nameLbl2 = new Label();
    private final Label supervisorLbl1 = new Label("Supervisor:");
    private final Label supervisorLbl2 = new Label();
    private final Label roleLbl1 = new Label("Role:");
    private final Label roleLbl2 = new Label();

    private final List<Label> elements = List.of(nameLbl1, nameLbl2, supervisorLbl1,
            supervisorLbl2, roleLbl1, roleLbl2);

    private UserInfoDialog() {
        this.setOnCloseRequest(evt -> {
            close();
            evt.consume();
        });
    }

    public static UserInfoDialog getInstance() {

        if (instance == null) {
            instance = new UserInfoDialog();
        }

        return instance;
    }

    public void showDialog(PersonDAO personDAO) {

        this.person = personDAO;

        getSupervisor();
        bindFields();
        completeGp();

        Scene scene = new Scene(gp, 400, 200);
        this.setScene(scene);
        this.setResizable(false);
        this.setTitle("Info about " + personDAO.getName());
        this.show();
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

    @Override
    public void close() {
        instance = null;
        super.close();
    }
}
