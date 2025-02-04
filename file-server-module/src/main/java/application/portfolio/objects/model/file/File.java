package application.portfolio.objects.model.file;

import application.portfolio.utils.ImageMethods;
import application.portfolio.utils.FilesManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class File extends FileSystemEntity {

    private static final String THUMBNAIL;
    private static final String[] GRAPHICS_EXTENSIONS = {"png", "jpg", "jpeg"};
    private String extension;
    private boolean hasThumbnail;
    private String thumbnailString;

    static {
        THUMBNAIL = FilesManager.getResourceThumbnailName();
    }

    public File(Path absolutePath, Path userDriverPath) {
        super(absolutePath, userDriverPath);
        String[] name = absolutePath.getFileName().toString().split("\\.");
        this.name = name[0];
        this.extension = name[1];

        if (Arrays.stream(GRAPHICS_EXTENSIONS).anyMatch(e -> e.equals(name[1]))) {
            handleGraphicView(userDriverPath);
        }
        this.isDirectory = false;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isHasThumbnail() {
        return hasThumbnail;
    }

    public void setHasThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public String getThumbnailString() {
        return thumbnailString;
    }

    public void setThumbnailString(String thumbnailString) {
        this.thumbnailString = thumbnailString;
    }

    private void handleGraphicView(Path userDriverPath) {

        Path thumbnailPath = userDriverPath.getParent().resolve(THUMBNAIL);
        thumbnailPath = thumbnailPath.resolve(path);

        String thumbnailName = this.name.concat("_min.jpg");
        thumbnailPath = thumbnailPath.getParent().resolve(thumbnailName);

        boolean result = Files.exists(thumbnailPath);
        if (!result) {
            Path sourcePath = userDriverPath.resolve(this.path);
            try (InputStream is = Files.newInputStream(sourcePath)) {
                result = ImageMethods.createImage(thumbnailPath, is, true);
            } catch (IOException e) {
                result = false;
            }
        }

        if (!result) {
            return;
        }

        String encodedImage = ImageMethods.encodeToBase64(thumbnailPath);
        if (encodedImage != null) {
            setHasThumbnail(true);
            setThumbnailString(encodedImage);
        }
    }
}
