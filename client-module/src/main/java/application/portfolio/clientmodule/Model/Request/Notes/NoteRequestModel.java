package application.portfolio.clientmodule.Model.Request.Notes;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Connection.Infrastructure;
import application.portfolio.clientmodule.Model.Model.Notes.NoteDAO;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.BaseNoteRequest;
import application.portfolio.clientmodule.Model.Request.Notes.NoteRequest.NoteRequest;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import com.fasterxml.jackson.databind.JsonNode;

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

    {
        gData = Infrastructure.getGatewayData();
    }

    public void save(BaseNoteRequest req) {
        System.out.println("Saving " + req);
    }

    public List<NoteDAO> loadNotes(NoteRequest noteRequest) {

        String params = NotesRequestConverter.toQueryLoadParams(noteRequest);
        HttpRequest request = prepareRequest(params, "notes/list",
                "GET", HttpRequest.BodyPublishers.noBody()).build();

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

    public String loadContent(NoteRequest data) {

        String params = NotesRequestConverter.toQueryContentRequest(data);
        HttpRequest request = prepareRequest(params, "notes",
                "GET", HttpRequest.BodyPublishers.noBody()).build();

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

    private HttpRequest.Builder prepareRequest(String params, String endpoint, String method,
                                               HttpRequest.BodyPublisher bodyPublisher) {
        String spec = Infrastructure.uriSpecificPart(gData, endpoint, params);

        URI baseUri = Infrastructure.getBaseUri(spec);
        return ClientHolder.getRequest(baseUri, method, bodyPublisher);
    }

    private List<NoteDAO> parseEntities(HttpResponse<JsonNode> response) {

        JsonNode node = response.body();
        node = node.get("response");

        if (node == null) {
            return Collections.emptyList();
        }

        List<NoteDAO> noteDAOS = new ArrayList<>();

        Consumer<JsonNode> consumer = n -> {
            NoteDAO dao = NoteDAO.createDAO(n);
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
