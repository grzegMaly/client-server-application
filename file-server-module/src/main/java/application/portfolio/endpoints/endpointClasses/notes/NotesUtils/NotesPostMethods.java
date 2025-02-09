package application.portfolio.endpoints.endpointClasses.notes.NotesUtils;

import application.portfolio.endpoints.endpointClasses.files.FileUtils.BaseUtils;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.ValidationResult;
import application.portfolio.objects.model.note.Note;
import application.portfolio.objects.model.note.NoteType;
import application.portfolio.utils.FilesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.net.HttpURLConnection.*;

public class NotesPostMethods {

    private static final String NOTE_DIR;
    private static final ObjectMapper objectMapper;
    private static final String RESPONSE;
    private static final Function<Path, Boolean> exceptionFunction;

    static {
        objectMapper = new ObjectMapper();
        NOTE_DIR = FilesManager.getResourceNotesName();
        RESPONSE = "response";
        exceptionFunction = path -> {
            try {
                Files.delete(path);
            } catch (IOException ex) {
                //Ignore
            }
            return false;
        };
    }

    public static Map.Entry<Integer, JsonNode> handlePost(HttpExchange exchange, Map<String, String> paramsMap) {

        ValidationResult validationResult = BaseUtils.validateUserPath(paramsMap);
        if (validationResult.isNotValid()) {
            return Map.entry(validationResult.getStatusCode(), validationResult.getNode());
        }

        ObjectNode finalNode = objectMapper.createObjectNode();
        if (!paramsMap.containsKey("noteId") || paramsMap.size() != 2) {
            finalNode.put(RESPONSE, "Invalid Params");
            return Map.entry(HTTP_BAD_REQUEST, finalNode);
        }

        byte[] data;
        JsonNode node;
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

        Path noteFilePath = validationResult.getUserPath()
                .resolve(NOTE_DIR).resolve(note.getNoteId().toString() + ".txt");
        if (Files.exists(noteFilePath)) {
            finalNode.put(RESPONSE, "Unreachable");
            return Map.entry(HTTP_FORBIDDEN, finalNode);
        }

        boolean success = writeToFile(noteFilePath, note, StandardOpenOption.CREATE_NEW, exceptionFunction);
        if (!success) {
            finalNode.put(RESPONSE, "Failed To Save Note");
            return Map.entry(HTTP_INTERNAL_ERROR, finalNode);
        }

        finalNode.put(RESPONSE, "Note Successfully Created");
        return Map.entry(HTTP_OK, finalNode);
    }

    public static boolean writeToFile(Path noteFilePath, Note note, StandardOpenOption openOption,
                                      Function<Path, Boolean> executionException) {

        try (BufferedWriter writer = Files.newBufferedWriter(noteFilePath, openOption)) {

            writer.write("#noteId:" + note.getNoteId() + "\n");
            writer.write("#title:" + note.getTitle() + "\n");

            NoteType noteType = note.getNoteType();
            writer.write("#noteType:" + noteType.getValue() + "\n");
            if (noteType.equals(NoteType.REGULAR_NOTE)) {
                writer.write("#category:" + note.getCategory().getValue() + "\n");
            } else if (noteType.equals(NoteType.DEADLINE_NOTE)) {
                writer.write("#priority:" + note.getPriority().getValue() + "\n");
                writer.write("#deadline:" + note.getDeadline().toString() + "\n");
            }

            writer.write("#createdDate:" + note.getCreatedDate().toString() + "\n");
            writer.write("#lastModificationDate:" + note.getLastModificationDate().toString() + "\n");

            writer.write("@\n");
            writer.write("-\n");
            if (note.getContent() != null && !note.getContent().isEmpty()) {
                writer.write(note.getContent());
                writer.write("\n");
            }
            return true;
        } catch (IOException e) {
            return executionException.apply(noteFilePath);
        }
    }
}
