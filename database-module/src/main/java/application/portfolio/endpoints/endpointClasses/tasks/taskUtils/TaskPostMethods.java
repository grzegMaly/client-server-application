package application.portfolio.endpoints.endpointClasses.tasks.taskUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.TaskResponse;
import application.portfolio.objects.model.Task.Task;
import application.portfolio.objects.model.Task.TaskUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class TaskPostMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<Map.Entry<String, Integer>> TASK_PARAMS = List.of(
            Map.entry("taskId", Types.NVARCHAR),
            Map.entry("title", Types.NVARCHAR),
            Map.entry("description", Types.NVARCHAR),
            Map.entry("createdBy", Types.NVARCHAR),
            Map.entry("assignedTo", Types.NVARCHAR),
            Map.entry("createdAt", Types.TIMESTAMP),
            Map.entry("deadline", Types.DATE),
            Map.entry("taskStatus", Types.INTEGER)
    );

    public static Map.Entry<Integer, JsonNode> handlePost(HttpExchange exchange) throws IOException {

        JsonNode node = objectMapper.readTree(exchange.getRequestBody().readAllBytes());
        Task task = TaskUtils.createTask(node);
        if (task == null) {
            ObjectNode finalNode = objectMapper.createObjectNode();
            finalNode.put("response", "Invalid Data");
            return Map.entry(HTTP_BAD_REQUEST, finalNode);
        }

        return createTask(task).toJsonResponse();
    }

    private static TaskResponse createTask(Task task) {
        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.createTask()
            )) {

                SQLServerDataTable taskDataTable = getTaskDataTable();
                taskDataTable.addRow(
                        null,
                        task.getTitle(),
                        task.getDescription(),
                        task.getCreatedBy(),
                        task.getAssignedTo(),
                        task.getCreateAt(),
                        task.getDeadline(),
                        task.getTaskStatus().getValue()
                );

                cs.setStructured(1, "TaskData", taskDataTable);
                TaskResponse taskResponse = new TaskResponse()
                        .taskResponseFromDB(cs, null);
                conn.commit();
                return taskResponse;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new TaskResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }
    }

    protected static SQLServerDataTable getTaskDataTable() throws SQLServerException {
        SQLServerDataTable groupDataTable = new SQLServerDataTable();
        for (Map.Entry<String, Integer> entry : TASK_PARAMS) {
            groupDataTable.addColumnMetadata(entry.getKey(), entry.getValue());
        }
        return groupDataTable;
    }
}
