package application.portfolio.endpoints.endpointClasses.files.FileUtils;

import application.portfolio.utils.DataParser;
import application.portfolio.utils.FilesManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

public class BaseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String separator;
    private static final Path rootPath;

    static {
        separator = File.separator;
        rootPath = FilesManager.getUserResourcePath();
    }

    public static ValidationResult validateAndResolvePath(Map<String, String> paramsMap) {

        String id = paramsMap.get("userId");
        UUID uId = DataParser.parseId(id);

        ObjectNode resultNode = objectMapper.createObjectNode();
        Path currentRootPath = rootPath.resolve(uId.toString());
        if (!Files.exists(currentRootPath)) {
            resultNode.put("response", "Unreachable Resource");
            return ValidationResult.error(HTTP_FORBIDDEN, resultNode);
        } else {
            String driveResource = FilesManager.getResourceDriveName();
            currentRootPath = currentRootPath.resolve(driveResource);
        }

        String path = paramsMap.get("path");
        if (!path.contains("/")) {
            resultNode.put("response", "Invalid Params");
            return ValidationResult.error(HTTP_FORBIDDEN, resultNode);
        }

        String[] pathElements = path.substring(1).split("/");

        if (pathElements[0].isBlank()) {
            resultNode.put("response", "Unreachable Resource");
            return ValidationResult.error(HTTP_FORBIDDEN, resultNode);
        }

        path = String.join(separator, pathElements);
        currentRootPath = currentRootPath.resolve(path);
        return ValidationResult.success(currentRootPath);
    }
}
