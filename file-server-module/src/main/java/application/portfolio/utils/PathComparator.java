package application.portfolio.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class PathComparator implements Comparator<Path> {

    @Override
    public int compare(Path p1, Path p2) {

        boolean isDir1 = Files.isDirectory(p1);
        boolean isDir2 = Files.isDirectory(p2);

        if (isDir1 && !isDir2) {
            return -1;
        } else if (!isDir1 && isDir2) {
            return 1;
        } else {
            return p1.compareTo(p2);
        }
    }
}
