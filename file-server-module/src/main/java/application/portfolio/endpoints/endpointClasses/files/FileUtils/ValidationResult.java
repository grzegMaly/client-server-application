package application.portfolio.endpoints.endpointClasses.files.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;

public class ValidationResult {

    private final boolean valid;
    private final int statusCode;
    private final JsonNode node;
    private final Path userPath;
    private final Path resourcePath;

    public ValidationResult(boolean valid, int statusCode, JsonNode node, Path userPath, Path resourcePath) {
        this.valid = valid;
        this.statusCode = statusCode;
        this.node = node;
        this.userPath = userPath;
        this.resourcePath = resourcePath;
    }

    public static ValidationResult error(int statusCode, JsonNode node) {
        return new ValidationResult(false, statusCode, node, null, null);
    }

    public static ValidationResult success(Path userDirPath, Path resourcePath) {
        return new ValidationResult(true, 0, null, userDirPath, resourcePath);
    }

    public boolean isNotValid() {
        return !valid;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public JsonNode getNode() {
        return node;
    }

    public Path getUserPath() {
        return userPath;
    }

    public Path getResourcePath() {
        return resourcePath;
    }
}
