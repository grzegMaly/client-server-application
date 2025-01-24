package application.portfolio.endpoints.endpointClasses.files.FileUtils;

import application.portfolio.objects.dao.file.FileSystemEntityDAO;
import application.portfolio.objects.model.file.FileSystemEntity;
import application.portfolio.utils.FilesManager;
import application.portfolio.utils.UserUtils.StatsVisitor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;

public class LoadFiles {

    private static final String separator = File.separator;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Path rootPath;

    static {
        rootPath = FilesManager.getUserResourcePath();
    }

    public static Map.Entry<Integer, JsonNode> loadFilesView(UUID userId) {
        return loadFilesView(userId, "");
    }

    public static Map.Entry<Integer, JsonNode> loadFilesView(UUID userId, String... pathElements) {

        ObjectNode finalNode;
        Path currentRootPath = rootPath.resolve(userId.toString());
        if (!Files.exists(currentRootPath)) {
            finalNode = objectMapper.createObjectNode();
            finalNode.put("response", "Unreachable Resource");
            return Map.entry(HTTP_FORBIDDEN, finalNode);
        } else {
            String driveResource = FilesManager.getResourceDriveName();
            currentRootPath = currentRootPath.resolve(driveResource);
        }

        String elements = String.join(separator, pathElements);
        Path absolutePath = currentRootPath.resolve(elements);

        if (!Files.exists(absolutePath)) {
            finalNode = objectMapper.createObjectNode();
            finalNode.put("response", "Unreachable Resource");
            return Map.entry(HTTP_FORBIDDEN, finalNode);
        }

        Map<Path, Long> filesSizesMap = StatsVisitor.getPathAndSizes(absolutePath);
        if (filesSizesMap.isEmpty()) {
            finalNode = objectMapper.createObjectNode();
            finalNode.set("response", objectMapper.createArrayNode());
            return Map.entry(HTTP_OK, finalNode);
        }

        Path rPath = currentRootPath;
        List<FileSystemEntity> list = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Void>> futures = filesSizesMap.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(() -> {

                    Path p = entry.getKey();
                    long size = entry.getValue();

                    FileSystemEntity entity = FileSystemEntity.createEntity(p, rPath);
                    if (entity != null) {
                        entity.setSize(size);
                        list.add(entity);
                    }
                }, executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<FileSystemEntityDAO> daoList = new ArrayList<>();
        list.forEach(e -> {
            FileSystemEntityDAO dao = FileSystemEntity.createDAO(e);
            daoList.add(dao);
        });

        finalNode = objectMapper.createObjectNode();
        JsonNode node = objectMapper.valueToTree(daoList);
        finalNode.set("response", node);
        return Map.entry(HTTP_OK, finalNode);
    }
}
