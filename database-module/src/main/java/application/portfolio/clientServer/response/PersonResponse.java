package application.portfolio.clientServer.response;

import application.portfolio.objects.dao.person.PersonDAO;
import application.portfolio.objects.model.Person.Person;
import application.portfolio.objects.model.Person.PersonUtils;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.*;

public class PersonResponse extends Response<Person, PersonDAO> {

    public PersonResponse() {
    }

    public PersonResponse(String message, int statusCode) {
        super(message, statusCode);
    }

    public PersonResponse personResponseFromDB(CallableStatement cs, Integer outputPosition)
            throws SQLException {

        Response<Person, PersonDAO> response = executeCallable(cs, outputPosition);
        CallableStatement csR = response.getCallableStatement();
        if (csR == null) {
            return new PersonResponse(response.getMessage(), response.getStatusCode());
        }

        try (csR) {
            ResultSet rs = csR.getResultSet();
            handleResultSet(rs);
        }
        return this;
    }

    private void handleResultSet(ResultSet rs) throws SQLException {

        int cCount;
        cCount = rs.getMetaData().getColumnCount();
        if (!rs.next()) {
            throw new SQLException();
        }

        if (cCount == 1) {
            String message = rs.getString(1);
            setMessage(message);
            setStatusCode(HTTP_UNAUTHORIZED);
        } else if (cCount > 1) {
            List<Person> persons = new ArrayList<>();
            do {
                Person person = PersonUtils.createPerson(rs);
                if (person == null) {
                    throw new SQLException();
                }
                persons.add(person);
            } while (rs.next());
            setItems(persons);
            setStatusCode(HTTP_OK);
        }
    }
}
