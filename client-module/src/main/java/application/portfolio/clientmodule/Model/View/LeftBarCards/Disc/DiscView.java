package application.portfolio.clientmodule.Model.View.LeftBarCards.Disc;

import javafx.scene.layout.TilePane;

import java.nio.file.Path;

public class DiscView extends TilePane {

    private Path currentPath;
    public DiscView(Path resourcePath) {
        this.currentPath = resourcePath;
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(Path currentPath) {
        this.currentPath = currentPath;
    }
}