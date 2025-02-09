package application.portfolio.endpoints.endpointClasses.notes;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.BaseUtils;
import application.portfolio.endpoints.endpointClasses.files.FileUtils.ValidationResult;
import application.portfolio.endpoints.endpointClasses.notes.NotesUtils.NotesGetMethods;
import application.portfolio.objects.model.note.Note;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.FilesManager;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.*;


@EndpointInfo(path = "/notes/list")
public class NotesListEndpoint implements EndpointHandler, HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Bad Method", HTTP_BAD_METHOD);
                return;
            }
            Map<String, String> paramsMap = DataParser.getParams(exchange.getRequestURI());
            String USER_ID_KEY = "userId";
            if (paramsMap == null || !paramsMap.containsKey(USER_ID_KEY) || paramsMap.size() != 1) {
                ResponseHandler.handleError(exchange, "Invalid Params", HTTP_BAD_REQUEST);
                return;
            }

            ValidationResult validationResult = BaseUtils.validateUserPath(paramsMap);
            Map.Entry<Integer, JsonNode> entryNode;
            if (validationResult.isNotValid()) {
                int statusCode = validationResult.getStatusCode();
                JsonNode node = validationResult.getNode();
                entryNode = Map.entry(statusCode, node);
            } else {
                entryNode = getNotes(validationResult);
            }
            ResponseHandler.sendResponse(exchange, entryNode);
        } catch (Exception e) {
            ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private Map.Entry<Integer, JsonNode> getNotes(ValidationResult validationResult) {

        String notesDir = FilesManager.getResourceNotesName();
        ObjectNode finalNode = objectMapper.createObjectNode();
        Path userNotesPath = validationResult.getUserPath().resolve(notesDir);

        Queue<Note> notesQueue = new ConcurrentLinkedQueue<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        try (Stream<Path> stream = Files.list(userNotesPath)) {
            stream.forEach(p -> futures.add(CompletableFuture.runAsync(() -> {
                Note note = NotesGetMethods.loadNotesList(p);
                if (note != null) {
                    notesQueue.add(note);
                }
            })));
        } catch (IOException e) {
            finalNode.put("response", "Unknown Error");
            return Map.entry(HTTP_INTERNAL_ERROR, finalNode);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        JsonNode node = null;
        try {
            node = objectMapper.valueToTree(Note.convertToDAOCollection(notesQueue));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finalNode.set("response", node);
        return Map.entry(HTTP_OK, finalNode);
    }
}
