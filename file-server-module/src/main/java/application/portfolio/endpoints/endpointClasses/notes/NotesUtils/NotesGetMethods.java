package application.portfolio.endpoints.endpointClasses.notes.NotesUtils;

import application.portfolio.endpoints.endpointClasses.files.FileUtils.BaseUtils;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.ValidationResult;
import application.portfolio.objects.model.note.Note;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.FilesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.*;

public class NotesGetMethods {

    private static final String NOTES_DIR;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    static {
        NOTES_DIR = FilesManager.getResourceNotesName();
    }

    public static Note loadNotesList(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return loadNotesList(reader);
        } catch (IOException e) {
            return null;
        }
    }

    public static Note loadNotesList(BufferedReader reader) {
        return Note.readNoteMeta(reader);
    }

    public static Map.Entry<Integer, JsonNode> handleGet(Map<String, String> paramsMap) {

        ValidationResult validationResult = BaseUtils.validateUserPath(paramsMap);
        if (validationResult.isNotValid()) {
            int statusCode = validationResult.getStatusCode();
            JsonNode node = validationResult.getNode();
            return Map.entry(statusCode, node);
        }

        ObjectNode node = objectMapper.createObjectNode();
        if (paramsMap.containsKey("option")) {
            String option = paramsMap.get("option");
            if (option.equals("all") && paramsMap.size() == 2) {
                return NotesGetMethods.loadCompleteNotes(validationResult);
            } else if (option.equals("content")) {
                if (paramsMap.containsKey("noteId") && paramsMap.size() == 3) {

                    String noteId = paramsMap.get("noteId");
                    UUID nId = DataParser.parseId(noteId);

                    Path userPath = validationResult.getUserPath();
                    String content = NotesGetMethods.getContent(userPath, nId);
                    ObjectNode contentNode = objectMapper.createObjectNode();
                    contentNode.put("content", content);
                    node.set("response", contentNode);

                    return Map.entry(HTTP_OK, node);
                }
            }
        }
        node.put("response", "Invalid Params");
        return Map.entry(HTTP_BAD_REQUEST, node);
    }

    public static Map.Entry<Integer, JsonNode> loadCompleteNotes(ValidationResult validationResult) {

        ObjectNode finalNode = objectMapper.createObjectNode();
        Path userNotesPath = validationResult.getUserPath().resolve(NOTES_DIR);

        Queue<Note> notesQueue = new ConcurrentLinkedQueue<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        try (Stream<Path> stream = Files.list(userNotesPath)) {
            stream.forEach(p -> futures.add(CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = Files.newBufferedReader(p)) {
                    Note note = loadNotesList(reader);
                    if (note != null) {
                        String content = getContent(reader);
                        note.setContent(content);
                        notesQueue.add(note);
                    }
                } catch (IOException e) {
                    //Ignore
                }
            }, executor)));
        } catch (IOException e) {
            finalNode.put("response", "Unknown Error");
            return Map.entry(HTTP_INTERNAL_ERROR, finalNode);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        JsonNode node = objectMapper.valueToTree(Note.convertToDAOCollection(notesQueue));
        finalNode.set("response", node);
        return Map.entry(HTTP_OK, finalNode);
    }

    public static String getContent(Path userPath, UUID noteId) {

        if (noteId == null) {
            return "";
        }

        Path notesPath = userPath.resolve(NOTES_DIR).resolve(noteId + ".txt");

        if (!Files.exists(notesPath)) {
            return "";
        }

        try (BufferedReader reader = Files.newBufferedReader(notesPath)) {
            String line = reader.readLine();
            if (line != null && line.startsWith("#noteId")) {
                UUID fileId = UUID.fromString(line.substring(8).trim());
                if (fileId.equals(noteId)) {
                    return getContent(reader);
                }
            }
        } catch (IOException | IllegalArgumentException ignored) {
        }
        return null;
    }

    public static String getContent(BufferedReader reader) {

        StringBuilder content = new StringBuilder();
        try {
            String line;
            boolean startReading = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("-")) {
                    startReading = true;
                    continue;
                }

                if (startReading) {
                    content.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Cos poszło nie tak przy pobieraniu contentu");
            return "";
        }
        return content.toString().trim();
    }
}
