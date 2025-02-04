package application.portfolio.endpoints.endpointClasses.files.FileUtils;

import application.portfolio.objects.dao.file.FileSystemEntityDAO;
import application.portfolio.objects.model.file.FileSystemEntity;
import application.portfolio.utils.FilesManager;
import application.portfolio.utils.UserUtils.StatsVisitor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;

public class LoadFiles {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final String DRIVE = FilesManager.getResourceDriveName();

    public static Map.Entry<Integer, JsonNode> loadFilesView(ValidationResult validationResult) {

        ObjectNode finalNode;
        Path userPath = validationResult.getUserPath().resolve(DRIVE);
        Path resourcePath = validationResult.getResourcePath();
        Path destinationPath = userPath.resolve(resourcePath);

        if (!Files.exists(destinationPath)) {
            finalNode = objectMapper.createObjectNode();
            finalNode.put("response", "Unreachable Resource");
            return Map.entry(HTTP_FORBIDDEN, finalNode);
        }

        List<FileSystemEntityDAO> daoList = getFileEntityDAOList(userPath, destinationPath);
        finalNode = objectMapper.createObjectNode();
        JsonNode node = objectMapper.valueToTree(daoList);
        finalNode.set("response", node);
        return Map.entry(HTTP_OK, finalNode);
    }

    public static List<FileSystemEntityDAO> getFileEntityDAOList(Path userDriverPath, Path destinationPath) {

        Map<Path, Long> filesSizesMap = StatsVisitor.getPathAndSizes(destinationPath);
        if (filesSizesMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<FileSystemEntity> list = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Void>> futures = filesSizesMap.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(() -> {

                    Path p = entry.getKey();
                    long size = entry.getValue();

                    FileSystemEntity entity = FileSystemEntity.createEntity(p, userDriverPath);
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
        return daoList;
    }
}
