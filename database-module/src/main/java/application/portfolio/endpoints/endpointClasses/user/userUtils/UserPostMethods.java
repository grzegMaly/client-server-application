package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.objects.model.Person.Person;
import application.portfolio.objects.model.Person.PersonResponse;
import application.portfolio.objects.model.Person.PersonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class UserPostMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static PersonResponse modifyPerson(UUID userId, byte[] data) {

        JsonNode node;
        Person person;
        try {
            node = objectMapper.readTree(data);
            person = PersonUtils.createPerson(node).get(0);

            if (person == null) {
                throw new IOException();
            }
        } catch (IOException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try (CallableStatement cs1 = conn.prepareCall(DBConnectionHolder.modifyUserById())) {

            cs1.setObject(1, person.getId());
            cs1.setString(2, person.getFirstName());
            cs1.setString(3, person.getLastName());
            cs1.setInt(4, person.getRole());

            cs1.registerOutParameter(5, Types.INTEGER);
            PersonResponse personResponse = PersonResponse.personResponseFromDB(cs1, 5);
            int statusCode = personResponse.getStatusCode();

            if (statusCode == 200) {
                return UserGetMethods.getPersonFromDatabase(userId);
            }
            return personResponse;
        } catch (SQLException e) {
            return new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }
    }

    public static PersonResponse addPerson(List<Person> persons) {

        Connection conn = DBConnectionHolder.getConnection();
        try {

            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.addUser()
            )) {

                conn.setAutoCommit(false);
                SQLServerDataTable userDataTable = new SQLServerDataTable();
                userDataTable.addColumnMetadata("fName", Types.NVARCHAR);
                userDataTable.addColumnMetadata("lName", Types.NVARCHAR);
                userDataTable.addColumnMetadata("role", Types.INTEGER);
                userDataTable.addColumnMetadata("login", Types.NVARCHAR);
                userDataTable.addColumnMetadata("password", Types.NVARCHAR);

                for (Person p : persons) {
                    userDataTable.addRow(p.getFirstName(), p.getLastName(), p.getRole(), p.getEmail(), p.getPassword());
                }
                cs.setStructured(1, "UserData", userDataTable);
                cs.registerOutParameter(2, Types.INTEGER);
                PersonResponse personResponse = PersonResponse.personResponseFromDB(cs, 2);

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
