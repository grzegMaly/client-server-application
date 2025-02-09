package application.portfolio.endpoints.endpointClasses.notes.NotesUtils;

import application.portfolio.endpoints.endpointClasses.files.FileUtils.BaseUtils;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.ValidationResult;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.FilesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.*;

public class NotesDeleteMethods {

    private static final ObjectMapper objectMapper;
    private static final String NOTE_DIR;
    private static final String RESPONSE;

    static {
        objectMapper = new ObjectMapper();
        NOTE_DIR = FilesManager.getResourceNotesName();
        RESPONSE = "response";
    }

    public static Map.Entry<Integer, JsonNode> handleDelete(Map<String, String> paramsMap) {

        ValidationResult validationResult = BaseUtils.validateUserPath(paramsMap);
        if (validationResult.isNotValid()) {
            return Map.entry(validationResult.getStatusCode(), validationResult.getNode());
        }

        ObjectNode finalNode = objectMapper.createObjectNode();
        if (!paramsMap.containsKey("noteId") || paramsMap.size() != 2) {
            finalNode.put(RESPONSE, "Invalid Params");
            return Map.entry(HTTP_BAD_REQUEST, finalNode);
        }

        UUID noteId = DataParser.parseId(paramsMap.get("noteId"));
        if (noteId == null) {
            finalNode.put(RESPONSE, "Invalid Params");
            return Map.entry(HTTP_BAD_REQUEST, finalNode);
        }

        Path noteFilePath = validationResult.getUserPath().resolve(NOTE_DIR).resolve(noteId + ".txt");
        try {
            boolean deleted = Files.deleteIfExists(noteFilePath);
            if (deleted) {
                finalNode.put(RESPONSE, "Ok");
                return Map.entry(HTTP_OK, finalNode);
            } else {
                finalNode.put(RESPONSE, "Unreachable");
                return Map.entry(HTTP_FORBIDDEN, finalNode);
            }
        } catch (IOException e) {
            finalNode.put(RESPONSE, "Unknown Error");
            return Map.entry(HTTP_INTERNAL_ERROR, finalNode);
        }
    }
}
