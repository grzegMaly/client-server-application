package application.portfolio.endpoints.endpointClasses.notes.NotesUtils;

import application.portfolio.endpoints.endpointClasses.files.FileUtils.BaseUtils;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.ValidationResult;
import application.portfolio.objects.model.note.Note;
import application.portfolio.utils.FilesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.function.Function;

import static java.net.HttpURLConnection.*;

public class NotesPutMethods {

    private static final String NOTE_DIR;
    private static final ObjectMapper objectMapper;
    private static final String RESPONSE;
    private static final Function<Path, Boolean> exceptionFunction;

    static {
        objectMapper = new ObjectMapper();
        NOTE_DIR = FilesManager.getResourceNotesName();
        RESPONSE = "response";
        exceptionFunction = path -> false;
    }

    public static Map.Entry<Integer, JsonNode> handlePut(HttpExchange exchange, Map<String, String> paramsMap) {

        ValidationResult validationResult = BaseUtils.validateUserPath(paramsMap);
        if (validationResult.isNotValid()) {
            return Map.entry(validationResult.getStatusCode(), validationResult.getNode());
        }

        byte[] data;
        JsonNode node;
        ObjectNode finalNode = objectMapper.createObjectNode();

        try {
            data = exchange.getRequestBody().readAllBytes();
            node = objectMapper.readTree(data);
        } catch (IOException e) {
            finalNode.put(RESPONSE, "Unknown Error");
            return Map.entry(HTTP_INTERNAL_ERROR, finalNode);
        }

        Note note = Note.createNote(node);
        if (note == null) {
            finalNode.put(RESPONSE, "Invalid Note Data");
            return Map.entry(HTTP_BAD_REQUEST, finalNode);
        }

        Path noteFilePath = validationResult.getUserPath().resolve(NOTE_DIR)
                .resolve(note.getNoteId().toString() + ".txt");
        if (!Files.exists(noteFilePath)) {
            finalNode.put(RESPONSE, "Unreachable");
            return Map.entry(HTTP_FORBIDDEN, finalNode);
        }

        System.out.println("Czysta egzystencja");
        boolean success = NotesPostMethods.writeToFile(noteFilePath, note,
                StandardOpenOption.TRUNCATE_EXISTING, exceptionFunction);
        if (!success) {
            finalNode.put(RESPONSE, "Failed To Update Note");
            return Map.entry(HTTP_INTERNAL_ERROR, finalNode);
        }

        finalNode.put(RESPONSE, "Note Successfully Updated");
        return Map.entry(HTTP_OK, finalNode);
    }
}
