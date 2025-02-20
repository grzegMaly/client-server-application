package application.portfolio.endpoints.endpointClasses.tasks.taskUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.TaskResponse;
import application.portfolio.objects.model.Task.Task;
import application.portfolio.objects.model.Task.TaskUtils;
import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class TaskPutMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map.Entry<Integer, JsonNode> handlePut(HttpExchange exchange) throws IOException {

        JsonNode node = DataParser.convertToNode(exchange);
        Task task = TaskUtils.createTask(node);

        if (task == null) {
            ObjectNode finalNode = objectMapper.createObjectNode();
            finalNode.put("response", "Invalid Data");
            return Map.entry(HTTP_BAD_REQUEST, finalNode);
        }
        return updateTask(task).toJsonResponse();
    }

    private static TaskResponse updateTask(Task task) {
        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.updateTask()
            )) {

                SQLServerDataTable taskDataTable = TaskPostMethods.getTaskDataTable();
                taskDataTable.addRow(
                        task.getTaskId(),
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
}
