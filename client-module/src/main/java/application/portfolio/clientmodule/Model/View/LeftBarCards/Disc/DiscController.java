package application.portfolio.clientmodule.Model.View.LeftBarCards.Disc;

import application.portfolio.clientmodule.Model.Model.Disc.FileElement;
import application.portfolio.clientmodule.Model.Request.Disc.DiscRequestViewModel;
import application.portfolio.clientmodule.Model.Model.Disc.DiscElement;
import application.portfolio.clientmodule.TeamLinkApp;
import application.portfolio.clientmodule.utils.ImageMethods;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DiscController {

    private DiscRequestViewModel viewModel;
    private DiscStack discStack;
    private DiscBinder discBinder;
    private Path currentPath = Path.of("/");
    private Path previousPath;
    private ContextMenu lastContextMenu;

    public DiscController() {
    }

    public DiscRequestViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(DiscRequestViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public DiscStack getDiscStack() {
        return discStack;
    }

    public void setDiscStack(DiscStack discStack) {
        this.discStack = discStack;
    }

    public DiscBinder getDiscBinder() {
        return discBinder;
    }

    public void setDiscBinder(DiscBinder discBinder) {
        this.discBinder = discBinder;
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(Path currentPath) {
        this.currentPath = currentPath;
        viewModel.setCurrentPathProperty(this.currentPath.toString());
    }

    public void createDiskView(Path path) {

        DiscView view = new DiscView(path);
        discStack.addDiscView(path, view);
        discStack.setCurrentDiscView(view);
        setCurrentPath(path);

        loadResourcesView(view);
        CompletableFuture.runAsync(() -> discBinder.setDiscViewAction(view));
    }

    private void loadResourcesView(DiscView discView) {

        List<DiscElement> elements = viewModel.loadResource();
        Platform.runLater(() -> discView.getChildren().addAll(elements));

        CompletableFuture.runAsync(() -> {
            List<Runnable> uiUpdates = Collections.synchronizedList(new ArrayList<>(elements.size()));
            elements.parallelStream().forEach(e -> {
                ImageView imageView = ImageMethods.getGraphic(e);
                Label label = new Label(e.getFileName());
                label.getStyleClass().add("entityLabel");

                uiUpdates.add(() -> e.getChildren().addAll(imageView, label));
            });
            Platform.runLater(() -> uiUpdates.forEach(Runnable::run));
        });
    }

    public void addResourceToView(DiscElement element) {
        DiscView discView = discStack.getCurrentDiscView();
        Platform.runLater(() -> {
            discView.getChildren().add(element);

            ImageView imageView = ImageMethods.getGraphic(element);
            Label label = new Label(element.getFileName());
            label.getStyleClass().add("entityLabel");

            element.getChildren().addAll(imageView, label);
        });
    }

    public void moveBack() {

        if (currentPath != null && currentPath.getParent() != null) {
            Path parentPath = currentPath.getParent();
            DiscView parentView = discStack.getDiscView(parentPath);
            DiscView currentDiscView = discStack.getCurrentDiscView();

            Platform.runLater(() -> {
                currentDiscView.getChildren().forEach(e -> e.setVisible(false));
                currentDiscView.setVisible(false);

                parentView.setVisible(true);
                parentView.getChildren().forEach(e -> e.setVisible(true));
            });

            discStack.setCurrentDiscView(parentView);
            previousPath = currentPath;
            setCurrentPath(parentPath);
        }
    }

    public void navigateTo(Path nextPath) {

        DiscView nextView = discStack.getDiscView(nextPath);
        DiscView currentView = discStack.getCurrentDiscView();

        if (nextView == null) {
            createDiskView(nextPath);
            if (previousPath != null) {
                if (!isSameBranch(previousPath, nextPath)) {
                    Path removePath = previousPath;
                    CompletableFuture.runAsync(() -> discStack.removeDiscView(removePath));
                }
                Platform.runLater(() -> {
                    currentView.getChildren().forEach(e -> e.setVisible(false));
                    currentView.setVisible(false);
                });
            }
        } else {
            discStack.setCurrentDiscView(nextView);
            setCurrentPath(nextPath);
            Platform.runLater(() -> {
                currentView.getChildren().forEach(e -> e.setVisible(false));
                currentView.setVisible(false);

                nextView.setVisible(true);
                nextView.getChildren().forEach(e -> e.setVisible(true));
            });
        }
        previousPath = currentPath;
    }

    private boolean isSameBranch(Path path1, Path path2) {
        return path1.startsWith(path2) || path2.startsWith(path1);
    }

    protected void handleFileOpen(FileElement fileElement) {

        if (fileElement.isHasThumbnail()) {
            DiscFilesHandler.displayImage(fileElement, viewModel);
        } else {
            DiscFilesHandler.displayTextFile(fileElement, viewModel);
        }
    }

    public void showContextMenu(Node element, double screenX, double screenY) {

        if (lastContextMenu != null) {
            lastContextMenu.hide();
        }

        ContextMenu contextMenu = getContextMenu(element);
        lastContextMenu = contextMenu;
        contextMenu.show(element, screenX, screenY);
        contextMenu.setOnHidden(evt -> lastContextMenu.hide());
    }

    private ContextMenu getContextMenu(Node node) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem download = new MenuItem("Download");
        MenuItem delete = new MenuItem("Delete");
        contextMenu.getStyleClass().add("discContextMenu");
        download.getStyleClass().add("download");
        download.setStyle("-fx-text-fill: white;");
        delete.getStyleClass().add("delete");
        delete.setStyle("-fx-text-fill: white;");

        contextMenu.getItems().addAll(download, delete);

        download.setOnAction(evt -> DiscFilesHandler.download(node, viewModel));

        delete.setOnAction(evt -> {
            boolean result = DiscFilesHandler.delete(node, viewModel);
            if (result) {
                DiscView discView = discStack.getCurrentDiscView();
                discView.getChildren().remove(node);
            }
        });
        return contextMenu;
    }

    public void handleFileUpload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select File");
        File file = chooser.showOpenDialog(TeamLinkApp.getMainStage());

        if (file == null) {
            return;
        }
        DiscFilesHandler.upload(file, this);
    }

    public void handleDirUpload() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select File");
        File file = chooser.showDialog(TeamLinkApp.getMainStage());

        if (file == null) {
            return;
        }
        DiscFilesHandler.upload(file, this);
    }
}
