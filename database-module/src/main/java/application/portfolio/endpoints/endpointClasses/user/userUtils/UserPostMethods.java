package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.objects.model.Person.Person;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.objects.model.Person.PersonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class UserPostMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

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

                SQLServerDataTable userDataTable = getUserDataTable();
                for (Person p : persons) {
                    userDataTable.addRow(p.getId(), p.getFirstName(),
                            p.getLastName(), p.getRole(), p.getEmail(), p.getPassword());
                }

                cs.setStructured(1, "UserData", userDataTable);
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

    public static PersonResponse addPerson(List<Person> persons) {

        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.addUser()
            )) {

                conn.setAutoCommit(false);
                SQLServerDataTable userDataTable = getUserDataTable();

                for (Person p : persons) {
                    userDataTable.addRow(null, p.getFirstName(), p.getLastName(), p.getRole(), p.getEmail(), p.getPassword());
                }
                cs.setStructured(1, "UserData", userDataTable);
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

    private static SQLServerDataTable getUserDataTable() throws SQLServerException {

        SQLServerDataTable userDataTable = new SQLServerDataTable();

        userDataTable.addColumnMetadata("id", Types.NVARCHAR);
        userDataTable.addColumnMetadata("fName", Types.NVARCHAR);
        userDataTable.addColumnMetadata("lName", Types.NVARCHAR);
        userDataTable.addColumnMetadata("role", Types.INTEGER);
        userDataTable.addColumnMetadata("login", Types.NVARCHAR);
        userDataTable.addColumnMetadata("password", Types.NVARCHAR);

        return userDataTable;
    }
}
