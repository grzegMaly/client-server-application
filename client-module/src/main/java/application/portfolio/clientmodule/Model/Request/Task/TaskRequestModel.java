package application.portfolio.clientmodule.Model.Request.Task;

import application.portfolio.clientmodule.Connection.ClientHolder;
import application.portfolio.clientmodule.Model.Model.Task.Task;
import application.portfolio.clientmodule.Model.Model.Task.TaskDAO;
import application.portfolio.clientmodule.Model.Request.Task.TaskRequest.TaskRequest;
import application.portfolio.clientmodule.utils.JsonBodyHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TaskRequestModel {

    private final ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
    }

    public void save(TaskDAO task) {

        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(task);
        } catch (JsonProcessingException e) {
            return;
        }
        HttpRequest request = ClientHolder.prepareRequest("", "tasks", "POST",
                HttpRequest.BodyPublishers.ofByteArray(data)).build();

        HttpResponse<String> response;
        try {
            response = ClientHolder.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> loadTasks(TaskRequest taskRequest, String key) {

        String params = TaskRequestConverter.toQueryLoadParams(taskRequest, key);
        HttpRequest request = ClientHolder.prepareRequest(params, "tasks", "GET",
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


    public void deleteTask(TaskRequest taskRequest) {

        String params = TaskRequestConverter.toQueryDeleteParams(taskRequest);

        HttpRequest request = ClientHolder.prepareRequest(params, "tasks", "DELETE",
                HttpRequest.BodyPublishers.noBody()).build();

        ClientHolder.getClient().sendAsync(request, JsonBodyHandler.getJsonHandler());
    }

    private List<Task> parseEntities(HttpResponse<JsonNode> response) {

        JsonNode node = response.body();
        node = node.get("response");

        if (node == null) {
            return Collections.emptyList();
        }

        List<Task> taskDAOS = new ArrayList<>();
        Consumer<JsonNode> taskConsumer = n -> {
            Task dao = Task.createTaskDAO(n);
            if (dao != null) {
                taskDAOS.add(dao);
            }
        };

        if (node.isArray()) {
            for (JsonNode n : node) {
                taskConsumer.accept(n);
            }
        } else if (node.isObject()) {
            taskConsumer.accept(node);
        } else {
            return Collections.emptyList();
        }

        return taskDAOS;
    }
}