package application.portfolio.endpoints.endpointClasses.files.FileUtils;

import application.portfolio.objects.dao.file.FileSystemEntityDAO;
import application.portfolio.utils.FilesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.net.HttpURLConnection.*;

public class ResourcePostMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DRIVE = FilesManager.getResourceDriveName();

    public static Map.Entry<Integer, JsonNode> handleSingleFileUpload(HttpExchange exchange,
                                                                      ValidationResult validationResult) {

        int statusCode;
        ObjectNode finalNode = objectMapper.createObjectNode();

        Path userPath = validationResult.getUserPath().resolve(DRIVE);
        Path resourcePath = validationResult.getResourcePath();
        Path destinationPath = userPath.resolve(resourcePath);

        if (destinationPath.getParent() != null && Files.isDirectory(destinationPath.getParent())) {
            try (InputStream is = exchange.getRequestBody()) {
                saveFile(is, destinationPath);
            } catch (IOException e) {
                statusCode = HTTP_INTERNAL_ERROR;
                finalNode.put("response", "Unknown Error");
                return Map.entry(statusCode, finalNode);
            }
        } else {
            statusCode = HTTP_BAD_REQUEST;
            finalNode.put("response", "FileName Not Set");
            return Map.entry(statusCode, finalNode);
        }

        return afterUploadResponse(userPath, destinationPath);
    }

    private static void saveFile(InputStream is, Path destinationPath) throws IOException {

        Files.createDirectories(destinationPath.getParent());
        try (OutputStream outputStream = Files.newOutputStream(destinationPath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            is.transferTo(outputStream);
        }
    }

    public static Map.Entry<Integer, JsonNode> handleZipStreamUpload(HttpExchange exchange,
                                                                     ValidationResult validationResult) {

        int statusCode;
        ObjectNode finalNode = objectMapper.createObjectNode();

        Path userPath = validationResult.getUserPath().resolve(DRIVE);
        Path resourcePath = validationResult.getResourcePath();
        Path destinationPath = userPath.resolve(resourcePath);

        try (InputStream is = exchange.getRequestBody()) {
            unzip(is, destinationPath);
        } catch (IOException e) {
            statusCode = HTTP_INTERNAL_ERROR;
            finalNode.put("response", "Unknown Error");
            return Map.entry(statusCode, finalNode);
        }
        return afterUploadResponse(userPath, destinationPath);
    }

    private static void unzip(InputStream is, Path destinationPath) throws IOException {
        Files.createDirectories(destinationPath);

        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path resolvedPath = destinationPath.resolve(entry.getName()).normalize();

                if (entry.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    try (OutputStream os = Files.newOutputStream(resolvedPath)) {
                        zis.transferTo(os);
                    }
                }
            }
        }
    }

    private static Map.Entry<Integer, JsonNode> afterUploadResponse(Path userPath, Path destinationPath) {

        int statusCode;
        Path destinationParent = destinationPath.getParent();
        String destinationFileName = destinationPath.getFileName().toString();
        ObjectNode finalNode = objectMapper.createObjectNode();
        List<FileSystemEntityDAO> DAOs = LoadFiles.getFileEntityDAOList(userPath, destinationParent);
        FileSystemEntityDAO dao = DAOs.stream()
                .filter(d -> d.getName().equals(destinationFileName))
                .findFirst()
                .orElse(null);

        if (dao == null) {
            statusCode = HTTP_INTERNAL_ERROR;
            finalNode.put("response", "Unknown Error");
        } else {
            statusCode = HTTP_OK;
            ObjectNode node = objectMapper.valueToTree(dao);
            finalNode.set("response", node);
        }
        return Map.entry(statusCode, finalNode);
    }
}
