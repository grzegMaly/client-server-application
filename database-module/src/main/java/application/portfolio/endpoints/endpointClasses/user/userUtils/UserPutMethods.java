package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.objects.model.Person.Person;
import application.portfolio.objects.model.Person.PersonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class UserPutMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static PersonResponse modifyPerson(byte[] data) {

        JsonNode node;
        List<Person> persons;
        try {
            node = objectMapper.readTree(data);
            persons = PersonUtils.createPerson(node);

            if (persons.isEmpty()) {
                throw new IOException();
            }
        } catch (IOException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.modifyUser()
            )) {

                SQLServerDataTable userDataTable = UserPostMethods.getUserDataTable();
                for (Person p : persons) {
                    userDataTable.addRow(p.getId(), p.getFirstName(),
                            p.getLastName(), p.getRole().getValue(), "", "");
                }

                cs.setStructured(1, "UserData", userDataTable);
                PersonResponse personResponse = new PersonResponse()
                        .personResponseFromDB(cs, null);

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
