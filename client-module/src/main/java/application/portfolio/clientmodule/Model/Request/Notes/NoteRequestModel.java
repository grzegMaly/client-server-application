package application.portfolio.clientmodule.Model.Request.Notes;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Model.Model.Notes.Note;
import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.NoteRequest;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NoteRequestModel {

    private final Map<String, String> gData;
    private final ObjectMapper objectMapper;

    {
        gData = Infrastructure.getGatewayData();
        objectMapper = new ObjectMapper();
    }

    public List<Note> loadNotes(NoteRequest noteRequest) {

        String params = NotesRequestConverter.toQueryLoadParams(noteRequest);
        HttpRequest request = prepareRequest(params, "GET",
                HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }

        if (response.statusCode() == 200) {
            return parseEntities(response);
        }
        return Collections.emptyList();
    }

    public String loadContent(NoteRequest noteRequest) {

        String params = NotesRequestConverter.toQueryContentRequest(noteRequest);
        HttpRequest request = prepareRequest(params, "GET",
                HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<JsonNode> response;
        try {
            response = ClientHolder.getClient().send(request, JsonBodyHandler.getJsonHandler());
        } catch (IOException | InterruptedException e) {
            return null;
        }

        if (response.statusCode() == 200) {
            return parseContent(response);
        }
        return null;
    }

    public boolean save(NoteRequest noteRequest, NoteDAO dao) {
        String params = NotesRequestConverter.toRegularUserQuery(noteRequest);
        return save(params, dao, "POST");
    }

    public boolean update(NoteRequest noteRequest, NoteDAO dao) {
        String params = NotesRequestConverter.toQueryWithNoteId(noteRequest);
        return save(params, dao, "PUT");
    }

    public boolean save(String params, NoteDAO dao, String method) {

        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(dao);
        } catch (IOException e) {
            return false;
        }

        HttpRequest request = prepareRequest(params, method,
                HttpRequest.BodyPublishers.ofByteArray(data)).build();

        HttpResponse<String> response;
        try {
            response = ClientHolder.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return response.statusCode() == 200;
    }

    public boolean delete(NoteRequest noteRequest) {

        String params = NotesRequestConverter.toQueryWithNoteId(noteRequest);
        HttpRequest request = prepareRequest(params, "DELETE",
                HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<Void> response;
        try {
             response = ClientHolder.getClient().send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            return false;
        }

        return response.statusCode() == 200;
    }

    private String parseContent(HttpResponse<JsonNode> response) {

        JsonNode node = response.body();
        node = node.get("response");

        if (node != null) {
            if (node.hasNonNull("content")) {
                return node.get("content").asText();
            }
        }
        return null;
    }

    private HttpRequest.Builder prepareRequest(String params, String method,
                                               HttpRequest.BodyPublisher bodyPublisher) {
        String spec = Infrastructure.uriSpecificPart(gData, "notes", params);

        URI baseUri = Infrastructure.getBaseUri(spec);
        return ClientHolder.getRequest(baseUri, method, bodyPublisher);
    }

    private List<Note> parseEntities(HttpResponse<JsonNode> response) {

        JsonNode node = response.body();
        node = node.get("response");

        if (node == null) {
            return Collections.emptyList();
        }

        List<Note> noteDAOS = new ArrayList<>();

        Consumer<JsonNode> consumer = n -> {
            Note dao = Note.createNote(n);
            if (dao != null) {
                noteDAOS.add(dao);
            }
        };

        if (node.isArray()) {
            for (JsonNode n : node) {
                consumer.accept(n);
            }
        } else if (node.isObject()) {
            consumer.accept(node);
        }
        return noteDAOS;
    }
}
