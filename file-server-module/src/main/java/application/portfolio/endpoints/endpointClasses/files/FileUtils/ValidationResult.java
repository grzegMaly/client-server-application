package application.portfolio.endpoints.endpointClasses.files.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;

public class ValidationResult {

    private final boolean valid;
    private final int statusCode;
    private final JsonNode node;
    private final Path validatedPath;

    public ValidationResult(boolean valid, int statusCode, JsonNode node, Path validatedPath) {
        this.valid = valid;
        this.statusCode = statusCode;
        this.node = node;
        this.validatedPath = validatedPath;
    }

    public static ValidationResult error(int statusCode, JsonNode node) {
        return new ValidationResult(false, statusCode, node, null);
    }

    public static ValidationResult success(Path validatedPath) {
        return new ValidationResult(true, 0, null, validatedPath);
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

    public Path getValidatedPath() {
        return validatedPath;
    }
}
