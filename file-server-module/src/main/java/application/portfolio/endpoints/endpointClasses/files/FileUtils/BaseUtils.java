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
    private static final String RESPONSE_KEY = "response";
    private static final Path rootPath;

    static {
        rootPath = FilesManager.getUserResourcePath();
    }

    public static ValidationResult validateUserPath(Map<String, String> paramsMap) {

        ObjectNode resultNode = objectMapper.createObjectNode();
        String id = paramsMap.get("userId");
        UUID uId = DataParser.parseId(id);

        if (uId == null) {
            resultNode.put(RESPONSE_KEY, "Invalid Param");
            return ValidationResult.error(HTTP_FORBIDDEN, resultNode);
        }

        Path userDirPath = rootPath.resolve(uId.toString());
        if (!Files.exists(userDirPath)) {
            resultNode.put(RESPONSE_KEY, "Resource Not Found");
            return ValidationResult.error(HTTP_FORBIDDEN, resultNode);
        }
        return ValidationResult.success(userDirPath, null);
    }

    public static ValidationResult validateAndResolvePath(Map<String, String> paramsMap) {

        ValidationResult firstValidation = validateUserPath(paramsMap);
        if (firstValidation.isNotValid()) {
            return firstValidation;
        }

        String path = paramsMap.get("path");
        if (!path.contains("/")) {
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("response", "Invalid Params");
            return ValidationResult.error(HTTP_FORBIDDEN, resultNode);
        }

        path = path.replace("+", " ");
        Path resourcePath = Path.of(path.substring(1));
        return ValidationResult.success(firstValidation.getUserPath(), resourcePath);
    }
}
