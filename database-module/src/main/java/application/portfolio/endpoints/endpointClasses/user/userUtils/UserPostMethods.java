package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.objects.model.Person.Person;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class UserPostMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static PersonResponse addPerson(List<Person> persons) {

        Connection conn = DBConnectionHolder.getConnection();
        PersonResponse personResponse;
        boolean result;

        try {
            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.addUser()
            )) {

                conn.setAutoCommit(false);
                SQLServerDataTable userDataTable = getUserDataTable();

                for (Person p : persons) {
                    userDataTable.addRow(null, p.getFirstName(), p.getLastName(),
                            p.getRole().getValue(), p.getEmail(), p.getPassword());
                }
                cs.setStructured(1, "UserData", userDataTable);
                personResponse = new PersonResponse()
                        .personResponseFromDB(cs, null);

                conn.commit();
                result = true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            personResponse = new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
            result = false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }

        if (result) {
            List<Person> people = personResponse.getItems();
            if (people != null && !people.isEmpty()) {
                Set<UUID> ids = people.stream().map(Person::getId).collect(Collectors.toSet());
                CompletableFuture.runAsync(() -> sendCreateResourceRequest(ids));
            }
        }
        return personResponse;
    }

    private static void sendCreateResourceRequest(Set<UUID> ids) {

        Map<String, String> fData = Infrastructure.getFileServerData();
        ObjectNode node = objectMapper.createObjectNode();

        ids.forEach(i -> node.put("userId", i.toString()));
        byte[] data;

        try {
            data = objectMapper.writeValueAsBytes(node);
        } catch (JsonProcessingException e) {
            return;
        }

        String params = Infrastructure.uriSpecificPart(fData, "creteResource");
        URI baseUri = Infrastructure.getBaseUri(fData).resolve(params);

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .timeout(Duration.ofSeconds(10))
                .build();
        ClientHolder.getClient().sendAsync(request, HttpResponse.BodyHandlers.discarding());
    }

    protected static SQLServerDataTable getUserDataTable() throws SQLServerException {

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
