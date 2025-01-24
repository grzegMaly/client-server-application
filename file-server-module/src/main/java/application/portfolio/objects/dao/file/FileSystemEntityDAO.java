package application.portfolio.objects.dao.file;

public abstract class FileSystemEntityDAO {

    private String path;
    private String name;
    private int size;
    private String scale;
    private String lastModifiedDate;
    private boolean isDirectory;

    public FileSystemEntityDAO() {
    }

    public FileSystemEntityDAO(String path, String name, int size, String scale,
                               String lastModifiedDate, boolean isDir) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.scale = scale;
        this.lastModifiedDate = lastModifiedDate;
        this.isDirectory = isDir;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
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

    public void setSize(int size) {
        this.size = size;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }
}
