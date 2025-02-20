package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.*;

import static java.net.HttpURLConnection.*;

public class UserGetMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static PersonResponse getPersonFromDatabase(JsonNode node) {

        String email, password;

        try {
            email = DataParser.parseElement(node, "email");
            password = DataParser.parseElement(node, "password");
        } catch (IllegalArgumentException e) {
            return new PersonResponse(e.getMessage(), HTTP_UNAUTHORIZED);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getUserByParams()
        )) {

            cs.setString(1, email);
            cs.setString(2, password);

            return new PersonResponse()
                    .personResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static PersonResponse getPersonFromDatabase(UUID id) {

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getUserById()
        )) {

            cs.setObject(1, id);
            return new PersonResponse()
                    .personResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static PersonResponse getPersonsFromDatabase(int offset, int limit) {

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getUsers()
        )) {

            cs.setInt(1, offset);
            cs.setInt(2, limit);

            return new PersonResponse()
                    .personResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static PersonResponse getUsersFriends(String userId) {

        UUID uId = DataParser.parseId(userId);
        if (uId == null) {
            return new PersonResponse("Not Found", HTTP_FORBIDDEN);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getUserColleagues()
        )) {

            cs.setObject(1, uId);
            return new PersonResponse()
                    .personResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
     }

    public static PersonResponse getAllUsers() {
        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getAllUsers()
        )) {

            return new PersonResponse()
                    .personResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
