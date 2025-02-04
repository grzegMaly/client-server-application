package application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequestViewModel;
import application.portfolio.clientmodule.Model.Model.Person.PersonDAO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.HashSet;
import java.util.Set;

public class ChatBinder {

    private static final ChatRequestViewModel viewModel = new ChatRequestViewModel();
    private final Set<Runnable> clearFields = new HashSet<>();

    {
        viewModel.setSender(UserSession.getInstance().getLoggedInUser());
    }

    public void withReceiver(PersonDAO personDAO) {

        viewModel.setReceiver(personDAO);
        clearFields.add(() -> viewModel.setReceiver(null));
    }

    public void withReceiver(Label nameLbl, Button moreInfoBtn) {

        viewModel.receiverProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {

                nameLbl.setText(newVal.getName());
                moreInfoBtn.setOnAction(evt -> {

                    moreInfoBtn.setDisable(true);

                    UserInfoDialog dialog = UserInfoDialog.getInstance();
                    dialog.showDialog(newVal);

                    dialog.setOnHidden(e -> moreInfoBtn.setDisable(false));
                });
            } else {
                nameLbl.setText(null);
            }
        });

        clearFields.add(() -> nameLbl.setText(null));
    }

    public void withTextFieldSendButton(TextField messageTf, Button sendBtn) {

        viewModel.messageProperty().bind(messageTf.textProperty());

        sendBtn.setOnAction(evt -> {
            viewModel.sendMessage();
            messageTf.clear();
        });
    }

    public void clearFields() {
        clearFields.forEach(Runnable::run);
    }

    public ChatRequestViewModel getViewModel() {
        return viewModel;
    }
}
