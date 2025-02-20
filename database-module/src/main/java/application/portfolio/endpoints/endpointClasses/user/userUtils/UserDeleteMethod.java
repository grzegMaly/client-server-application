package application.portfolio.endpoints.endpointClasses.user.userUtils;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.utils.DataParser;
import application.portfolio.utils.Infrastructure;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class UserDeleteMethod {

    public static PersonResponse deletePerson(UUID userId) {

        Connection conn = DBConnectionHolder.getConnection();
        PersonResponse personResponse;
        boolean result;
        try {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall(
                    DBConnectionHolder.deleteUserById()
            )) {
                cs.setObject(1, userId);
                cs.registerOutParameter(2, Types.INTEGER);

                personResponse = new PersonResponse()
                        .personResponseFromDB(cs, 2);

                conn.commit();
                result = true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            result = false;
            personResponse = new PersonResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }

        if (result) {
            CompletableFuture.runAsync(() -> sendDeleteUserResourceRequest(userId));
        }
        return personResponse;
    }

    private static void sendDeleteUserResourceRequest(UUID userId) {

        Map<String, String> fData = Infrastructure.getFileServerData();

        String spec = DataParser.paramsString(Map.of("userId", userId.toString()));
        String params = Infrastructure.uriSpecificPart(fData, "deleteResource", spec);
        URI baseUri = Infrastructure.getBaseUri(fData).resolve(params);

        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .DELETE()
                .timeout(Duration.ofSeconds(10))
                .build();
        ClientHolder.getClient().sendAsync(request, HttpResponse.BodyHandlers.discarding());
    }
}
