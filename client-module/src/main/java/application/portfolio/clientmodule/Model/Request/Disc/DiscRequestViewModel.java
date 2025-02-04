package application.portfolio.clientmodule.Model.Request.Disc;

import application.portfolio.clientmodule.Model.Request.Disc.DiscRequest.DiscRequest;
import application.portfolio.clientmodule.Model.Model.Disc.DiscElement;
import javafx.beans.property.SimpleStringProperty;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public class DiscRequestViewModel {

    private final SimpleStringProperty currentPathProperty = new SimpleStringProperty();
    private final DiscRequestModel model = new DiscRequestModel();
    private final DiscRequestConverter converter = new DiscRequestConverter();

    public String getCurrentPathProperty() {
        return currentPathProperty.get();
    }

    public SimpleStringProperty currentPathPropertyProperty() {
        return currentPathProperty;
    }

    public void setCurrentPathProperty(String currentPathProperty) {
        this.currentPathProperty.set(currentPathProperty);
    }

    public List<DiscElement> loadResource() {
        Path pathObject = Path.of(getCurrentPathProperty());
        DiscRequest data = converter.convertToRequest(pathObject);
        return model.loadDiscView(data);
    }

    public InputStream downloadResource(Path path) {
        path = Path.of(getCurrentPathProperty()).resolve(path);
        DiscRequest data = converter.convertToRequest(path);
        return model.downloadResource(data);
    }

    public boolean deleteResource(Path path) {
        path = Path.of(getCurrentPathProperty()).resolve(path);
        DiscRequest data = converter.convertToRequest(path);
        return model.deleteResource(data);
    }

    public DiscElement uploadResource(Path path, boolean isFile, InputStream inputStream) {
        path = Path.of(getCurrentPathProperty()).resolve(path);
        DiscRequest data = converter.convertToRequest(path, isFile);
        return model.uploadResource(data, inputStream);
    }
}
