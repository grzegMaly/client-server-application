package application.portfolio.utils.UserUtils;


import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class StatsVisitor implements FileVisitor<Path> {

    private final Map<Path, Long> sizesMap = new LinkedHashMap<>();
    private Path mainPath = null;
    private int initialCount;

    public static Map<Path, Long> getPathAndSizes(Path path) {

        StatsVisitor visitor = new StatsVisitor();
        try {
            Files.walkFileTree(path, visitor);
        } catch (IOException e) {
            return new HashMap<>();
        }
        return visitor.getSizeMap();
    }

    private Map<Path, Long> getSizeMap() {
        return sizesMap;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(attrs);

        if (mainPath == null) {
            mainPath = dir;
            initialCount = dir.getNameCount();
        } else {
            sizesMap.put(dir, 0L);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(attrs);

        if (file.getNameCount() - initialCount > 1) {
            sizesMap.merge(file.getParent(), attrs.size(), Long::sum);
        } else {
            sizesMap.merge(file, attrs.size(), Long::sum);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        Objects.requireNonNull(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Objects.requireNonNull(dir);

        if (dir.equals(mainPath)) {
            return FileVisitResult.TERMINATE;
        }

        int relativeLevel = dir.getNameCount() - initialCount;
        if (relativeLevel > 1) {
            long size = sizesMap.getOrDefault(dir, 0L);
            sizesMap.merge(dir.getParent(), size, Long::sum);
            sizesMap.remove(dir);
        }
        return FileVisitResult.CONTINUE;
    }
}
