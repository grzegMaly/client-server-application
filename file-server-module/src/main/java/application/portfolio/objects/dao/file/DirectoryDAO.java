package application.portfolio.objects.dao.file;

import application.portfolio.objects.model.file.Directory;

public class DirectoryDAO extends FileSystemEntityDAO {
    public DirectoryDAO() {
        super();
    }

    public DirectoryDAO(Directory dir) {
        this.setPath(dir.getPath().toString());
        this.setName(dir.getName());
        this.setSize(dir.getSize());
        this.setScale(dir.getScale());
        this.setLastModifiedDate(dir.getLastModifiedDate().toString());
        this.setDirectory(dir.isDirectory());
    }

    public DirectoryDAO(String path, String name, int size, String scale, String lastModifiedDate, boolean isDir) {
        super(path, name, size, scale, lastModifiedDate, isDir);
    }
}
