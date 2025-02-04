package application.portfolio.objects.dao.file;

import application.portfolio.objects.model.file.File;

public class FileDAO extends FileSystemEntityDAO {

    private String extension;
    private boolean hasThumbnail;
    private String thumbnailString;

    public FileDAO() {
        super();
    }

    public FileDAO(String path, String name, int size, String scale, String lastModifiedDate,
                   boolean isDir, String extension, boolean hasThumbNail, String thumbnailString) {
        super(path, name, size, scale, lastModifiedDate, isDir);
        this.extension = extension;
        this.hasThumbnail = hasThumbNail;
        this.thumbnailString = thumbnailString;
    }

    public FileDAO(File file) {
        this.setPath(file.getPath().toString());
        String extension = file.getExtension();
        String name = file.getName().concat(".").concat(extension);
        this.setName(name);
        this.setSize(file.getSize());
        this.setScale(file.getScale());
        this.setLastModifiedDate(file.getLastModifiedDate().toString());
        this.setDirectory(file.isDirectory());
        this.setExtension(extension);
        this.setHasThumbnail(file.isHasThumbnail());
        this.setThumbnailString(file.getThumbnailString() == null ? "" : file.getThumbnailString());
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
}