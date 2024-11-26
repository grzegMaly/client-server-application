package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.objects.model.Person.PersonResponse;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class UserDeleteMethod {

    public static PersonResponse deletePerson(UUID userId) {

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs = conn.prepareCall(
                DBConnectionHolder.deleteUserById()
        )) {
            cs.setObject(1, userId);
            cs.registerOutParameter(2, Types.INTEGER);

            return PersonResponse.personResponseFromDB(cs, 2);
        } catch (SQLException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }
}
