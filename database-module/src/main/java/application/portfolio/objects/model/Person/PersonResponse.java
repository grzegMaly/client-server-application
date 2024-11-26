package application.portfolio.objects.model.Person;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.objects.dao.person.PersonDAO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class PersonResponse {

    private String message;
    private int statusCode;
    private List<Person> persons;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String responseKey = "response";

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    public PersonResponse() {
    }

    public PersonResponse(Person person, int statusCode) {
        this(statusCode);
        persons = new ArrayList<>(persons);
    }

    public PersonResponse(String message, int statusCode) {
        this(statusCode);
        this.message = message;
    }

    public PersonResponse(List<Person> persons, int statusCode) {
        this(statusCode);
        this.persons = persons;
    }

    public PersonResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean personExists() {
        return persons != null && !persons.isEmpty();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Person getPerson() {
        return persons.get(0);
    }

    public List<Person> getPersons() {
        return new ArrayList<>(persons);
    }

    public void setPerson(Person person) {
        if (persons == null) {
            persons = new ArrayList<>();
        }
        persons.add(person);
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public static PersonResponse personResponseFromDB(CallableStatement cs, Integer outputPosition)
            throws SQLException {

        if (!cs.execute()) {
            int statusCode = -1;
            try {
                if (outputPosition == null) {
                    throw new SQLException();
                }
                statusCode = cs.getInt(outputPosition);
                if (statusCode == -1) {
                    throw new SQLException();
                } else if (statusCode == 0) {
                    return new PersonResponse("OK", HTTP_OK);
                } else {
                    Connection conn = DBConnectionHolder.getConnection();
                    try (CallableStatement cs2 = conn.prepareCall(DBConnectionHolder.getStatusInfo())) {
                        cs2.setInt(1, statusCode);
                        return PersonResponse.personResponseFromDB(cs2, null);
                    }
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        }

        ResultSet rs = cs.getResultSet();
        if (!rs.next()) {
            throw new SQLException();
        }

        int cCount = rs.getMetaData().getColumnCount();
        PersonResponse personResponse = null;

        if (cCount == 1) {
            String message = rs.getString(1);
            personResponse = new PersonResponse(message, HTTP_UNAUTHORIZED);
        } else if (cCount > 1) {

            List<Person> persons = new ArrayList<>();
            do {
                Person person = PersonUtils.createPerson(rs);
                if (person == null) {
                    throw new SQLException();
                }
                persons.add(person);
            } while (rs.next());
            personResponse = new PersonResponse(persons, HTTP_OK);
        }
        return personResponse;
    }

    public static Map.Entry<Integer, JsonNode> toPersonJsonResponse(PersonResponse response) {

        int responseCode = response.getStatusCode();
        ObjectNode responseNode = objectMapper.createObjectNode();

        if (response.personExists()) {

            JsonNode personNode;
            List<Person> persons = response.getPersons();

            if (persons.size() > 1) {
                List<PersonDAO> DAOs = persons.stream()
                        .map(Person::DAOFromPerson)
                        .toList();
                personNode = objectMapper.valueToTree(DAOs);
            } else {
                Person person = persons.get(0);
                PersonDAO personDAO = Person.DAOFromPerson(person);
                personNode = objectMapper.valueToTree(personDAO);
            }
            responseNode.set(responseKey, personNode);
        } else {
            String message = response.getMessage();
            responseNode.put(responseKey, message);
        }
        return new AbstractMap.SimpleEntry<>(responseCode, responseNode);
    }
}
