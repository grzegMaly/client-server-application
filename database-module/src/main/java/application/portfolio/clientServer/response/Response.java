package application.portfolio.clientServer.response;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.objects.dao.DAOConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class Response<T extends DAOConverter<T, D>, D> implements IResponse {

    private String message;
    private int statusCode;
    private List<T> items;
    private CallableStatement cs;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String responseKey = "response";

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public Response() {

    }

    public Response(T item, int statusCode) {
        this(statusCode);
        this.items = new ArrayList<>(List.of(item));
    }

    public Response(List<T> items, int statusCode) {
        this(statusCode);
        this.items = items;
    }

    public Response(CallableStatement cs, int statusCode) {
        this.cs = cs;
        this.statusCode = statusCode;
    }

    public Response(String message) {
        this.message = message;
    }

    public Response(int statusCode) {
        this.statusCode = statusCode;
    }

    public Response(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public CallableStatement getCallableStatement() {
        return cs;
    }

    public void setCallableStatement(CallableStatement cs) {
        this.cs = cs;
    }

    public T getItem() {
        return items.get(0);
    }

    public List<T> getItems() {
        return new ArrayList<>(items);
    }

    public void setItem(T item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    private boolean itemsExists() {
        return items != null;
    }

    protected Response<T, D> executeCallable(CallableStatement cs, Integer outputPosition) throws SQLException {

        Response<T, D> response;
        Connection conn = DBConnectionHolder.getConnection();
        try {
            if (!cs.execute()) {
                int statusCode = -1;
                if (outputPosition == null) {
                    throw new SQLException();
                }
                statusCode = cs.getInt(outputPosition);
                if (statusCode == -1) {
                    throw new SQLException();
                } else if (statusCode == 0) {
                    response = new Response<>("OK", HTTP_OK);
                } else {
                    try {
                        CallableStatement cs2 = conn.prepareCall(DBConnectionHolder.getStatusInfo());
                        cs2.setInt(1, statusCode);
                        return executeCallable(cs2, null);
                    } catch (SQLException e) {
                        throw new SQLException();
                    }
                }
            } else {
                response = new Response<>(cs, HTTP_OK);
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
        return response;
    }

    @Override
    public Map.Entry<Integer, JsonNode> toJsonResponse() {

        ObjectNode responseNode = objectMapper.createObjectNode();
        if (itemsExists()) {
            JsonNode itemsNode;
            if (items.size() > 1) {
                try {
                    List<D> DAOs = items.stream()
                            .map(T::toDAO)
                            .toList();
                    itemsNode = objectMapper.valueToTree(DAOs);
                } catch (Exception e) {
                    itemsNode = null;
                }
            } else if (items.size() == 1) {
                T object = items.get(0);
                D objectDAO = object.toDAO();
                itemsNode = objectMapper.valueToTree(objectDAO);
            } else {
                itemsNode = objectMapper.createArrayNode();
            }
            responseNode.set(responseKey, itemsNode);
        } else {
            responseNode.put(responseKey, this.getMessage());
        }

        return Map.entry(this.getStatusCode(), responseNode);
    }
}
