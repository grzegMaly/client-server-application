package application.portfolio.clientmodule.Model.View.LeftBarCards.Disc;

import application.portfolio.clientmodule.Model.Model.Disc.DiscElement;
import application.portfolio.clientmodule.Model.Model.Disc.FileElement;
import application.portfolio.clientmodule.Model.Request.Disc.DiscRequestViewModel;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


public class DiscBinder {

    private DiscController discController;
    private final DiscRequestViewModel viewModel = new DiscRequestViewModel();
    private EventHandler<MouseEvent> discViewEvent;

    public void bindDiscController(DiscController discController) {
        this.discController = discController;
        discController.setViewModel(viewModel);
        discController.setDiscBinder(this);
    }

    public Label bindPathLabel() {
        Label pathLabel = new Label();
        pathLabel.textProperty()
                .bind(viewModel.currentPathPropertyProperty());
        return pathLabel;
    }

    public Button bindBackBtn() {
        Button backBtn = new Button("Back");
        backBtn.setOnAction(evt -> discController.moveBack());
        return backBtn;
    }

    public Button bindUploadFileBtn() {
        Button uploadBtn = new Button("Upload File");
        uploadBtn.setOnAction(evt -> discController.handleFileUpload());
        return uploadBtn;
    }

    public Button bindUploadDirBtn() {
        Button uploadBtn = new Button("Upload Directory");
        uploadBtn.setOnAction(evt -> discController.handleDirUpload());
        return uploadBtn;
    }

    public void setDiscViewAction(DiscView element) {
        element.setOnMouseClicked(getMouseAction());
    }

    private EventHandler<MouseEvent> getMouseAction() {
        if (discViewEvent != null) {
            return discViewEvent;
        }

        discViewEvent = evt -> {
            MouseButton button = evt.getButton();
            Node element = evt.getPickResult().getIntersectedNode();
            if (button == MouseButton.PRIMARY) {
                if (element instanceof FileElement fileElement) {
                    discController.handleFileOpen(fileElement);
                } else if (element instanceof DiscElement discElement) {
                    discController.navigateTo(discElement.getPath());
                }
            } else if (button == MouseButton.SECONDARY) {
                discController.showContextMenu(element, evt.getScreenX(), evt.getScreenY());
            }
        };
        return discViewEvent;
    }
}
