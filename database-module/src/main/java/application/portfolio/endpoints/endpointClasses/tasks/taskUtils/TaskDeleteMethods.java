package application.portfolio.endpoints.endpointClasses.tasks.taskUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.TaskResponse;
import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class TaskDeleteMethods {

    private static final String[] REQUIRED_PARAMS = {"taskId", "userId"};
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map.Entry<Integer, JsonNode> handleDelete(Map<String, String> paramsMap) {

        if (!DataParser.validateParams(paramsMap, REQUIRED_PARAMS)) {
            return invalidParamsResponse();
        }

        String userId = paramsMap.get("userId");
        String taskId = paramsMap.get("taskId");

        UUID uId = DataParser.parseId(userId);
        UUID tId = DataParser.parseId(taskId);

        if (uId == null || tId == null) {
            return invalidParamsResponse();
        }

        return deleteTask(tId, uId).toJsonResponse();
    }

    private static TaskResponse deleteTask(UUID tId, UUID uId) {

        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall(
                    DBConnectionHolder.deleteTask()
            )) {

                cs.setObject(1, tId);
                cs.setObject(2, uId);
                cs.registerOutParameter(3, Types.INTEGER);

                TaskResponse taskResponse = new TaskResponse()
                        .taskResponseFromDB(cs, 3);

                conn.commit();
                return taskResponse;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new TaskResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private static Map.Entry<Integer, JsonNode> invalidParamsResponse() {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("response", "Invalid Params");
        return Map.entry(HTTP_BAD_REQUEST, node);
    }
}
