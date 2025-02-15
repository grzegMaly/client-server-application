package application.portfolio.objects.model.file;

import application.portfolio.objects.dao.file.DirectoryDAO;
import application.portfolio.objects.dao.file.FileDAO;
import application.portfolio.objects.dao.file.FileSystemEntityDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class FileSystemEntity {

    protected Path path;
    protected String name;
    protected int size;
    protected String scale;
    protected LocalDateTime lastModifiedDate;
    protected boolean isDirectory;

    public FileSystemEntity(Path absolutePath, Path userDriverPath) {

        this.path = userDriverPath.relativize(absolutePath);
        this.name = absolutePath.getFileName().toString();

        try {
            this.lastModifiedDate = LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(absolutePath).toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            this.lastModifiedDate = null;
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(long byteSize) {

        String scale;
        if (byteSize < 1024) {
            this.size = (int) byteSize;
            scale = "B";
        } else {
            int exp = (int) (Math.log(byteSize) / Math.log(1024));
            this.size = (int) (byteSize / Math.pow(1024, exp));
            scale = switch (exp) {
                case 1 -> "KB";
                case 2 -> "MB";
                case 3 -> "GB";
                default -> "B";
            };
        }
        this.scale = scale;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public static FileSystemEntity createEntity(Path absolutePath, Path userDriverPath) {

        if (Files.isRegularFile(absolutePath)) {
            return new File(absolutePath, userDriverPath);
        } else if (Files.isDirectory(absolutePath)) {
            return new Directory(absolutePath, userDriverPath);
        } else {
            return null;
        }
    }

    public static FileSystemEntityDAO createDAO(FileSystemEntity entity) {

        if (entity instanceof File e) {
            return new FileDAO(e);
        } else if (entity instanceof Directory d) {
            return new DirectoryDAO(d);
        } else {
            return null;
        }
    }
}
