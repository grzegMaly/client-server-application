package application.portfolio.objects.model.Task;

import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TaskUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String[] REQUIRED_KEYS = {"title", "description", "createdBy", "assignedTo",
            "createdAt", "deadline", "taskStatus"};

    public static Task createTask(ResultSet rs) {

        try {

            UUID taskId = UUID.fromString(rs.getString(1));
            String title = rs.getString(2);
            String description = rs.getString(3);
            UUID createdById = UUID.fromString(rs.getString(4));
            UUID assignedToId = UUID.fromString(rs.getString(5));
            String createdAt = rs.getString(6);
            LocalDateTime createdAtLdt = LocalDateTime.parse(createdAt, formatter).withNano(0);
            String deadline = rs.getString(7);
            LocalDate deadlineLd = LocalDate.parse(deadline);
            int statusId = rs.getInt(8);
            TaskStatus status = TaskStatus.fromValue(statusId);

            return new Task(
                    taskId,
                    title,
                    description,
                    createdById,
                    assignedToId,
                    createdAtLdt,
                    deadlineLd,
                    status
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static Task createTask(JsonNode node) {

        if (!DataParser.validateNode(node, REQUIRED_KEYS)) {
            return null;
        }

        Task task = new Task();
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        try {
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                String key = entry.getKey().trim();
                String val = entry.getValue().asText().trim();
                completeTask(task, key, val);
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return task;
    }

    private static void completeTask(Task task, String key, String val) {
        switch (key) {
            case "taskId" -> {
                UUID id = UUID.fromString(val);
                task.setTaskId(id);
            }
            case "title" -> task.setTitle(val);
            case "description" -> task.setDescription(val);
            case "createdBy" -> {
                UUID createdBy = UUID.fromString(val);
                task.setCreatedBy(createdBy);
            }
            case "assignedTo" -> {
                UUID assignedTo = UUID.fromString(val);
                task.setAssignedTo(assignedTo);
            }
            case "createdAt" -> {
                LocalDateTime ldt = LocalDateTime.parse(val);
                task.setCreateAt(ldt);
            }
            case "deadline" -> {
                LocalDate ld = LocalDate.parse(val);
                task.setDeadline(ld);
            }
            case "taskStatus" -> {
                int v = Integer.parseInt(val);
                TaskStatus status = TaskStatus.fromValue(v);
                if (status == null) {
                    throw new IllegalArgumentException();
                }
                task.setTaskStatus(status);
            }
        }
    }
}
