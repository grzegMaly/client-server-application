package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.objects.dao.person.PersonDAO;
import application.portfolio.objects.model.Person;
import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

import static java.net.HttpURLConnection.*;

public class UserMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String responseKey = "response";

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Map.Entry<Integer, JsonNode> getPersonFromDatabase(JsonNode node) {

        ObjectNode responseNode = objectMapper.createObjectNode();
        String email, password;

        try {
            email = DataParser.parseElements(node, "email");
            password = DataParser.parseElements(node, "password");
        } catch (IllegalArgumentException e) {
            responseNode.put(responseKey, e.getMessage());
            return new AbstractMap.SimpleEntry<>(HTTP_UNAUTHORIZED, responseNode);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getUserStatementCall()
        )) {

            cs.setString(1, email);
            cs.setString(2, password);
            return personResponseFromDB(cs);
        } catch (SQLException e) {
            responseNode.put(responseKey, "UnknownError");
            return new AbstractMap.SimpleEntry<>(HTTP_INTERNAL_ERROR, responseNode);
        }
    }

    public static Map.Entry<Integer, JsonNode> getPersonFromDatabase(UUID id) {

        ObjectNode responseNode = objectMapper.createObjectNode();

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getUserByIdCall()
        )) {

            cs.setObject(1, id);
            return personResponseFromDB(cs);
        } catch (SQLException e) {
            responseNode.put(responseKey, "Unknown Error");
            return new AbstractMap.SimpleEntry<>(HTTP_INTERNAL_ERROR, responseNode);
        }
    }

    private static Map.Entry<Integer, JsonNode> personResponseFromDB(CallableStatement cs)
            throws SQLException {

        if (!cs.execute()) {
            throw new SQLException();
        }

        ResultSet rs = cs.getResultSet();
        if (!rs.next()) {
            throw new SQLException();
        }

        int responseCode = -1;
        int cCount = rs.getMetaData().getColumnCount();
        ObjectNode responseNode = objectMapper.createObjectNode();


        if (cCount == 1) {

            String message = rs.getString(1);
            responseCode = HTTP_NOT_FOUND;
            responseNode.put(responseKey, message);
        } else if (cCount > 1) {

            Person person = Person.createPerson(rs);
            if (person == null) {
                throw new SQLException();
            }

            PersonDAO personDAO = Person.DAOFromPerson(person);
            JsonNode personNode = objectMapper.valueToTree(personDAO);
            responseCode = HTTP_OK;
            responseNode.set(responseKey, personNode);
        }
        return new AbstractMap.SimpleEntry<>(responseCode, responseNode);
    }
}
