package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.clientServer.response.PersonResponse;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class UserDeleteMethod {

    public static PersonResponse deletePerson(UUID userId) {

        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall(
                    DBConnectionHolder.deleteUserById()
            )) {
                cs.setObject(1, userId);
                cs.registerOutParameter(2, Types.INTEGER);

                PersonResponse personResponse = new PersonResponse()
                        .personResponseFromDB(cs, 2);

                conn.commit();
                return personResponse;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }
    }
}
