package application.portfolio.objects.model.Person;

import application.portfolio.objects.dao.Person.Role;
import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class PersonUtils {

    private static final List<String> existingPersonKeys = List.of("userId", "firstName", "lastName", "role");
    private static final List<String> newPersonKeys = List.of("firstName", "lastName", "role", "email", "password");

    public static Person createPerson(ResultSet rs) {
        try {
            String id = rs.getString(1);
            UUID uId = UUID.fromString(id);
            String fName = rs.getString(2);
            String lName = rs.getString(3);
            int role = rs.getInt(4);
            return new Person(uId, fName, lName, Role.fromValue(role));
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Person> createPerson(JsonNode node) {

        List<Person> validatedPersons = Collections.synchronizedList(new ArrayList<>());
        BlockingQueue<Person> queue = new LinkedBlockingQueue<>();
        Object lock = new Object();

        Consumer<JsonNode> jNodeLambda = n -> {
            String[] keysArr = DataParser.getNodeKeys(n);
            Person person = createPerson(n, keysArr);
            if (person != null) {
                queue.add(person);
            }
        };

        final boolean[] isProducerRunning = {true};
        Thread producer = new Thread(() -> {
            try {
                if (node.isArray()) {
                    for (JsonNode personNode : node) {
                        jNodeLambda.accept(personNode);
                    }
                } else {
                    jNodeLambda.accept(node);
                }
            } catch (Exception e) {
                throw new RuntimeException();
            } finally {
                synchronized (lock) {
                    isProducerRunning[0] = false;
                    lock.notifyAll();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    synchronized (lock) {
                        if (queue.isEmpty() && !isProducerRunning[0]) {
                            break;
                        }
                    }

                    Person person = queue.poll();
                    if (person != null && validatePerson(person)) {
                        validatedPersons.add(person);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return validatedPersons;
    }

    private static boolean validatePerson(Person person) {

        if (person.getId() == null) {
            if (!DataParser.isNullOrEmpty(person.getEmail()) && !DataParser.isNullOrEmpty(person.getPassword())) {

                boolean emailPattern = Pattern.compile("([\\w.-]+)@teamLink.com")
                        .matcher(person.getEmail()).matches();
                if (!emailPattern) {
                    return false;
                }

                String password = person.getPassword();
                if (password.length() < 8 || password.length() > 20) {
                    return false;
                }
                boolean lowerPattern = Pattern.compile(".*[a-z].*").matcher(password).matches();
                boolean upperPattern = Pattern.compile(".*[a-z].*").matcher(password).matches();
                boolean numberPattern = Pattern.compile(".*[0-9].*").matcher(password).matches();
                boolean specialCharactersPattern = Pattern.compile(".*[!@#$%^&*()_].*").matcher(password).matches();

                if (!lowerPattern || !upperPattern || !numberPattern || !specialCharactersPattern) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (DataParser.isNullOrEmpty(person.getFirstName()) || DataParser.isNullOrEmpty(person.getLastName())) {
            return false;
        }
        return person.getRole().getValue() <= 2 && person.getRole().getValue() >= 0;
    }

    private static Person createPerson(JsonNode personNode, String[] keysArr) {

        Person person = null;
        try {
            Set<String> keys = new HashSet<>(Arrays.asList(keysArr));
            String[] data = DataParser.parseElements(personNode, keysArr);
            if (keys.size() == existingPersonKeys.size()) {
                if (keys.containsAll(existingPersonKeys)) {
                    person = createExistingPerson(data);
                }
            } else if (keys.size() == newPersonKeys.size()) {
                if (keys.containsAll(newPersonKeys)) {
                    person = createNewPerson(data);
                }
            }
        } catch (IllegalArgumentException ignored) {
        }
        return person;
    }

    private static Person createExistingPerson(String[] data) {
        String id = data[0].trim();
        String fName = data[1].trim();
        String lName = data[2].trim();
        String role = data[3].trim();

        UUID uId = UUID.fromString(id);
        int iRole = Integer.parseInt(role);
        return new Person(uId, fName, lName, Role.fromValue(iRole));
    }

    private static Person createNewPerson(String[] data) {
        String fName = data[0].trim();
        String lName = data[1].trim();
        String role = data[2].trim();
        String email = data[3].trim();
        String password = data[4].trim();

        int iRole = Integer.parseInt(role);

        return new Person(fName, lName, Role.fromValue(iRole), email, password);
    }
}
