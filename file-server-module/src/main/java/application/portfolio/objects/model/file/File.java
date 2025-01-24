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

    public File(Path path, Path rootPath) {
        super(path, rootPath);
        String[] name = path.getFileName().toString().split("\\.");
        this.name = name[0];
        this.extension = name[1];

        if (Arrays.stream(GRAPHICS_EXTENSIONS).anyMatch(e -> e.equals(name[1]))) {
            handleGraphicView(rootPath);
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

    private void handleGraphicView(Path rootDriverPath) {

        Path thumbnailPath = rootDriverPath.getParent().resolve(THUMBNAIL);
        thumbnailPath = thumbnailPath.resolve(path);

        String thumbnailName = this.name.concat("_min.jpg");
        thumbnailPath = thumbnailPath.getParent().resolve(thumbnailName);

        if (!Files.exists(thumbnailPath)) {
            Path sourcePath = rootDriverPath.resolve(this.path);
            try (InputStream is = Files.newInputStream(sourcePath)) {
                boolean result = ImageMethods.createImage(thumbnailPath, is, true);
            } catch (IOException e) {
                //Ignore
            }
        }

        String encodedImage = ImageMethods.encodeToBase64(thumbnailPath);
        if (encodedImage != null) {
            this.hasThumbnail = true;
            this.thumbnailString = encodedImage;
        }
    }
}
