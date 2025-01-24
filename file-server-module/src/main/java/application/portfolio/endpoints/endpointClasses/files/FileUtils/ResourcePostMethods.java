package application.portfolio.endpoints.endpointClasses.files.FileUtils;

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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.net.HttpURLConnection.*;

public class ResourcePostMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map.Entry<Integer, JsonNode> handleSingleFileUpload(HttpExchange exchange, Path destinationPath) {

        int statusCode;
        ObjectNode finalNode = objectMapper.createObjectNode();

        if (Files.isRegularFile(destinationPath)) {
            try (InputStream is = exchange.getRequestBody()) {
                saveFile(is, destinationPath);
                statusCode = HTTP_OK;
                finalNode.put("response", "Ok");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                statusCode = HTTP_INTERNAL_ERROR;
                finalNode.put("response", "Unknown Error");
            }
        } else {
            statusCode = HTTP_BAD_REQUEST;
            finalNode.put("response", "FileName Not Set");
        }
        return Map.entry(statusCode, finalNode);
    }

    private static void saveFile(InputStream is, Path destinationPath) throws IOException {

        Files.createDirectories(destinationPath.getParent());
        try (OutputStream outputStream = Files.newOutputStream(destinationPath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            is.transferTo(outputStream);
        }
    }

    public static Map.Entry<Integer, JsonNode> handleZipStreamUpload(HttpExchange exchange, Path destinationPath) {

        int statusCode;
        ObjectNode finalNode = objectMapper.createObjectNode();
        try (InputStream is = exchange.getRequestBody()) {
            unzip(is, destinationPath);
            statusCode = HTTP_OK;
            finalNode.put("response", "Ok");
        } catch (IOException e) {
            statusCode = HTTP_INTERNAL_ERROR;
            finalNode.put("response", "Unknown Error");
        }
        return Map.entry(statusCode, finalNode);
    }

    private static void unzip(InputStream is, Path destinationPath) throws IOException {

        Files.createDirectories(destinationPath.getParent());
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path resolvedPath = destinationPath.resolve(entry.getName());
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
}
