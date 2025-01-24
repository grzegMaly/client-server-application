package application.portfolio.endpoints.endpointClasses.files.FileUtils;

import application.portfolio.utils.PathComparator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.*;

public class ResourceDeleteMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map.Entry<Integer, JsonNode> handleDelete(Map<String, String> paramsMap) {

        ValidationResult validationResult = BaseUtils.validateAndResolvePath(paramsMap);
        if (validationResult.isNotValid()) {
            int statusCode = validationResult.getStatusCode();
            JsonNode node = validationResult.getNode();
            return Map.entry(statusCode, node);
        }

        System.out.println("elo");
        ObjectNode finalNode = objectMapper.createObjectNode();
        Path currentRootPath = validationResult.getValidatedPath();
        boolean result = deleteRecursive(currentRootPath);
        if (!result) {
            finalNode.put("response", "Unknown Error");
            return Map.entry(HTTP_INTERNAL_ERROR, finalNode);
        }

        finalNode.put("response", "Ok");
        return Map.entry(HTTP_OK, finalNode);
    }

    private static boolean deleteRecursive(Path path) {
        if (!Files.exists(path)) {
            return false;
        }

        try (Stream<Path> stream = Files.walk(path)) {
            stream.sorted(new PathComparator().reversed())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            //Ignore
                        }
                    });
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}