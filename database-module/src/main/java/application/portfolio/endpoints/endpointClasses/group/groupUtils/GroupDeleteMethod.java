package application.portfolio.endpoints.endpointClasses.group.groupUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.clientServer.response.IResponse;
import application.portfolio.utils.DataParser;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class GroupDeleteMethod {

    public static GroupResponse deleteGroup(String id) {

        UUID uId = DataParser.parseId(id);
        if (uId == null) {
            return new GroupResponse("Bad Data", HTTP_FORBIDDEN);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall(
                    DBConnectionHolder.deleteGroupById()
            )) {

                cs.setObject(1, uId);
                cs.registerOutParameter(2, Types.INTEGER);

                GroupResponse groupResponse = new GroupResponse()
                        .groupResponseFromDB(cs, 2);

                conn.commit();
                return groupResponse;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }
    }

    public static IResponse deleteUserFromGroup(String userId, String groupId) {

        UUID uId = DataParser.parseId(userId);
        UUID gId = DataParser.parseId(groupId);
        if (uId == null || gId == null) {
            return new GroupResponse("Bad Data", HTTP_FORBIDDEN);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall(
                    DBConnectionHolder.deleteUserFromGroup()
            )) {

                cs.setObject(1, gId);
                cs.setObject(2, uId);
                cs.registerOutParameter(3, Types.INTEGER);

                return new GroupResponse()
                        .groupResponseFromDB(cs, 3);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }
    }
}