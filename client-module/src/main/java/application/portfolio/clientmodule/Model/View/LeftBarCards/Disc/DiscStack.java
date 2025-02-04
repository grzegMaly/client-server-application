package application.portfolio.clientmodule.Model.View.LeftBarCards.Disc;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.nio.file.Path;
import java.util.*;

public class DiscStack extends StackPane {

    private final Map<Path, DiscView> views = new LinkedHashMap<>();
    private DiscView currentDiscView;

    public Map<Path, DiscView> getViews() {
        return views;
    }

    public DiscView getCurrentDiscView() {
        return currentDiscView;
    }

    public void setCurrentDiscView(DiscView currentDiscView) {
         this.currentDiscView = currentDiscView;
    }

    public DiscView getDiscView(Path path) {
        return views.get(path);
    }

    public void addDiscView(Path path, DiscView discView) {
        views.put(path, discView);
        this.getChildren().add(discView);
    }

    public void removeDiscView(Path path) {

        ObservableList<Node> items = this.getChildren();
        Iterator<Path> iterator = views.keySet().iterator();
        while (iterator.hasNext()) {
            Path p = iterator.next();
            if (p.startsWith(path)) {
                iterator.remove();
                items.remove(views.get(p));
            }
        }
    }
}
