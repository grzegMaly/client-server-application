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
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class TaskGetMethods {

    private static final ObjectMapper objectMapper;
    private static final String ASSIGNED_TO;
    private static final String CREATED_BY;
    private static final String RESPONSE;

    static {
        objectMapper = new ObjectMapper();
        ASSIGNED_TO = "assignedTo";
        CREATED_BY = "createdBy";
        RESPONSE = "response";
    }

    public static Map.Entry<Integer, JsonNode> handleGet(Map<String, String> paramsMap) {

        ObjectNode finalNode = objectMapper.createObjectNode();

        if (paramsMap.size() != 1) {
            finalNode.put(RESPONSE, "Invalid Params");
            return Map.entry(HTTP_BAD_REQUEST, finalNode);
        }

        TaskResponse response;
        if (paramsMap.containsKey(ASSIGNED_TO)) {
            response = loadAssignedTasks(paramsMap.get(ASSIGNED_TO));
        } else if (paramsMap.containsKey(CREATED_BY)) {
            response = loadCreatedTAsks(paramsMap.get(CREATED_BY));
        } else {
            finalNode.put(RESPONSE, "Invalid Params");
            return Map.entry(HTTP_BAD_REQUEST, finalNode);
        }

        return response.toJsonResponse();
    }

    private static TaskResponse loadAssignedTasks(String assignedTo) {

        UUID userId = DataParser.parseId(assignedTo);
        if (userId == null) {
            new TaskResponse("Invalid Param", HTTP_BAD_REQUEST);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.loadReceivedTasks()
        )) {

            cs.setObject(1, userId);

            return new TaskResponse()
                    .taskResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new TaskResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    private static TaskResponse loadCreatedTAsks(String createdBy) {

        UUID userId = DataParser.parseId(createdBy);
        if (userId == null) {
            new TaskResponse("Invalid Param", HTTP_BAD_REQUEST);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.loadCreatedTasks()
        )) {

            cs.setObject(1, userId);
            return new TaskResponse()
                    .taskResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new TaskResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
