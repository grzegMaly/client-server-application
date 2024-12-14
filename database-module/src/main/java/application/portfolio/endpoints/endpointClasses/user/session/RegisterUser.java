package application.portfolio.endpoints.endpointClasses.user.session;

import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.endpoints.endpointClasses.user.userUtils.UserPostMethods;
import application.portfolio.objects.model.Person.Person;
import application.portfolio.clientServer.response.PersonResponse;
import application.portfolio.objects.model.Person.PersonUtils;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/user/register")
public class RegisterUser implements EndpointHandler, HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            if (!"POST".equals(exchange.getRequestMethod())) {
                ResponseHandler.handleError(exchange, "Invalid request method used", HTTP_BAD_REQUEST);
                return;
            }

            if (!validateRequest()) {
                ResponseHandler.handleError(exchange, "Invalid Credentials", HTTP_UNAUTHORIZED);
                return;
            }

            var body = exchange.getRequestBody();
            JsonNode node = objectMapper.readTree(body);
            PersonResponse personResponse = handleAdding(node);

            Map.Entry<Integer, JsonNode> entry = personResponse.toJsonResponse();
            ResponseHandler.sendResponse(exchange, entry);
        } catch (IOException e) {
            exchange.sendResponseHeaders(HTTP_INTERNAL_ERROR, -1);
        }
    }

    private PersonResponse handleAdding(JsonNode node) {

        List<Person> persons = PersonUtils.createPerson(node);
        if (persons.isEmpty()) {
            return new PersonResponse("Bad Data", HTTP_BAD_REQUEST);
        }
        return UserPostMethods.addPerson(persons);
    }

    //TODO: TEMP
    private boolean validateRequest() {
        return true;
    }
}
