package application.portfolio.utils.UserUtils;

import application.portfolio.utils.DataParser;
import application.portfolio.utils.FilesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class UserResourcesManager {

    public static void handleNewUsers(HttpExchange exchange) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        byte[] data = exchange.getRequestBody().readAllBytes();

        JsonNode node = mapper.readTree(data);
        Set<UUID> userIds = new HashSet<>();

        Consumer<JsonNode> lambda = n -> {
            if (n.hasNonNull("userId")) {
                String sId = n.get("userId").asText();
                UUID id = DataParser.parseId(sId);
                if (id != null) {
                    userIds.add(id);
                }
            }
        };

        if (node.isArray()) {
            for (JsonNode userNode : node) {
                lambda.accept(userNode);
            }
        } else if (node.isObject()) {
            lambda.accept(node);
        } else {
            throw new Exception();
        }

        if (userIds.size() < node.size()) {
            throw new Exception();
        }

        Path driverPath = FilesManager.getUserResourcePath();
        Path userResourcesPath = driverPath.resolve("users");
        createDirectory(userResourcesPath);

        List<String> dirs = List.of("Drive", "Notes", "Tasks");
        for (UUID id : userIds) {
            createDirectories(userResourcesPath, id, dirs);
        }
    }

    private static void createDirectories(Path resourcePath, UUID userId, List<String> dirs) {

        Path finalPath = resourcePath.resolve(userId.toString());
        try {
            if (!Files.exists(finalPath)) createDirectory(finalPath);
            for (String dirName : dirs) {
                Path dirPath = finalPath.resolve(dirName);
                createDirectory(dirPath);
            }
        } catch (IOException ignored) {
            //Ignored
        }
    }

    private static void createDirectory(Path path) throws IOException {

        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }
}
