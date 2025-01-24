package application.portfolio.objects.model.file;

import java.nio.file.Path;

public final class Directory extends FileSystemEntity {
    public Directory(Path path, Path rootPath) {
        super(path, rootPath);
        this.isDirectory = true;
    }
}
