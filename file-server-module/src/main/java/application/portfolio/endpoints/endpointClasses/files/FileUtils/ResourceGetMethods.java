package application.portfolio.endpoints.endpointClasses.files.FileUtils;

import application.portfolio.utils.FilesManager;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.net.HttpURLConnection.*;

public class ResourceGetMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DRIVE = FilesManager.getResourceDriveName();

    public static void handleDownload(HttpExchange exchange, ValidationResult validationResult) {

        Path userPath = validationResult.getUserPath();
        Path resourcePath = validationResult.getResourcePath();
        Path destinationPath = userPath.resolve(DRIVE).resolve(resourcePath);

        if (Files.isRegularFile(destinationPath)) {
            sendFile(exchange, destinationPath);
        } else if (Files.isDirectory(destinationPath)) {
            sendDirAsZip(exchange, destinationPath);
        } else {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("response", "Unsupported resource type");
            ResponseHandler.sendResponse(exchange, Map.entry(HTTP_BAD_REQUEST, node));
        }
    }

    private static void sendFile(HttpExchange exchange, Path resourcePath) {

        try {
            long fileSize = Files.size(resourcePath);
            exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
            exchange.getResponseHeaders().set("Content-Disposition",
                    "attachment; filename=\"" + resourcePath.getFileName() + "\"");
            exchange.sendResponseHeaders(HTTP_OK, fileSize);

            try (OutputStream os = exchange.getResponseBody();
                 InputStream is = Files.newInputStream(resourcePath)) {
                is.transferTo(os);
            }
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown error", HTTP_INTERNAL_ERROR);
        }
    }

    private static void sendDirAsZip(HttpExchange exchange, Path resourcePath) {

        try {
            exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
            exchange.getResponseHeaders().set("Content-Disposition",
                    "attachment; filename=\"" + resourcePath.getFileName() + ".zip\"");
            exchange.sendResponseHeaders(HTTP_OK, 0);

            try (OutputStream os = exchange.getResponseBody();
                 ZipOutputStream zos = new ZipOutputStream(os);
                 Stream<Path> stream = Files.walk(resourcePath)) {

                stream
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry entry = new ZipEntry(resourcePath.relativize(path).toString());
                            try {
                                zos.putNextEntry(entry);
                                Files.copy(path, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }
        } catch (IOException e) {
            ResponseHandler.handleError(exchange, "Unknown error", HTTP_INTERNAL_ERROR);
        }
    }
}
