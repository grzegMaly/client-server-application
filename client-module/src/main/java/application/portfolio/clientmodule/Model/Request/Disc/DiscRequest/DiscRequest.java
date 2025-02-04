package application.portfolio.clientmodule.Model.Request.Disc.DiscRequest;


import java.nio.file.Path;
import java.util.UUID;

public class DiscRequest {

    private UUID userId;
    private Path path;
    private boolean fileUpload;

    public DiscRequest(UUID userId, Path path) {
        this(userId, path, false);
    }

    public DiscRequest(UUID userId, Path path, boolean fileUpload) {
        this.userId = userId;
        this.path = path;
        this.fileUpload = fileUpload;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean isFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(boolean fileUpload) {
        this.fileUpload = fileUpload;
    }
}
