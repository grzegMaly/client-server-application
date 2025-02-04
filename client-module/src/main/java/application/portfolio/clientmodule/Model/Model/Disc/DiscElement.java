package application.portfolio.clientmodule.Model.Model.Disc;

import application.portfolio.clientmodule.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.layout.VBox;

import java.io.File;
import java.nio.file.Path;

public class DiscElement extends VBox {

    private static final String[] REQUIRED_KEYS = {"path", "name", "size", "scale", "lastModifiedDate", "directory"};
    private static final Path START_PATH = Path.of(File.separator);

    private Path path;
    private String fileName;
    private String lastModifiedDate;
    private int size;
    private String scale;
    private boolean isDirectory;

    public DiscElement() {
        this.setPickOnBounds(true);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public static DiscElement createElement(JsonNode node) {

        if (!DataParser.validateElements(node, REQUIRED_KEYS)) {
            return null;
        }

        DiscElement discElement;

        boolean directory = node.get(REQUIRED_KEYS[5]).asBoolean();
        if (directory) {
            discElement = new DiscElement();
        } else {
            discElement = FileElement.completeElement(node);
            if (discElement == null) {
                return null;
            }
        }
        discElement.getStyleClass().add("discElement");
        discElement.setDirectory(directory);

        Path sourcePath = Path.of(node.get(REQUIRED_KEYS[0]).asText().replace("\\", File.separator));
        Path finalPath = sourcePath.isAbsolute() ? sourcePath.normalize() : START_PATH.resolve(sourcePath).normalize();
        discElement.setPath(finalPath);


        String filename = sourcePath.getFileName().toString();
        discElement.setFileName(filename);

        int size = node.get(REQUIRED_KEYS[2]).asInt();
        String scale = node.get(REQUIRED_KEYS[3]).asText();

        discElement.setSize(size);
        discElement.setScale(scale);

        discElement.setLastModifiedDate(node.get(REQUIRED_KEYS[4]).toString());
        return discElement;
    }
}