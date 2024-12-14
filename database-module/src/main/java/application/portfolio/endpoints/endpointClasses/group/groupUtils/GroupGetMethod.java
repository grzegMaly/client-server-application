package application.portfolio.endpoints.endpointClasses.group.groupUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.utils.DataParser;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class GroupGetMethod {

    public static GroupResponse getGroupFromDatabase(String id) {

        UUID userId = DataParser.parseId(id);
        if (userId == null) {
            return new GroupResponse("Not Found", HTTP_FORBIDDEN);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getGroupById()
        )) {
            cs.setObject(1, userId);
            return new GroupResponse()
                    .groupResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static GroupResponse getGroupFromDatabase(String offset, String limit) {

        int[] validatedParams = validatePaginationParams(offset, limit);
        if (validatedParams == null) {
            return new GroupResponse("Bad Data", HTTP_FORBIDDEN);
        }

        int iOffset = validatedParams[0];
        int iLimit = validatedParams[1];

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getGroups()
        )) {

            cs.setInt(1, iOffset);
            cs.setInt(2, iLimit);

            return new GroupResponse()
                    .groupResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static PersonResponse getGroupMembers(String groupId, String offset, String limit) {

        int[] validatedParams = validatePaginationParams(offset, limit);
        if (validatedParams == null) {
            return new PersonResponse("Bad Data", HTTP_FORBIDDEN);
        }

        UUID userId = DataParser.parseId(groupId);
        if (userId == null) {
            return new PersonResponse("Not Found", HTTP_FORBIDDEN);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getGroupMembers()
        )) {

            int iOffset = validatedParams[0];
            int iLimit = validatedParams[1];

            cs.setObject(1, userId);
            cs.setInt(2, iOffset);
            cs.setInt(3, iLimit);

            return new PersonResponse()
                    .personResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new PersonResponse( "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static GroupResponse getUserGroups(String userId) {

        UUID uId = DataParser.parseId(userId);
        if (uId == null) {
            return new GroupResponse("Not Found", HTTP_FORBIDDEN);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.getUserGroups()
        )) {

            cs.setObject(1, uId);
            return new GroupResponse()
                    .groupResponseFromDB(cs, null);
        } catch (SQLException e) {
            return new GroupResponse( "Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static int[] validatePaginationParams(String offset, String limit) {
        int iLimit, iOffset;
        try {
            iLimit = Integer.parseInt(limit);
            iOffset = Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            return null;
        }

        if (iOffset < 0 || iLimit <= 0) {
            return null;
        }

        if (iLimit > 50) {
            iLimit = 10;
        }

        return new int[]{iOffset, iLimit};
    }
}
