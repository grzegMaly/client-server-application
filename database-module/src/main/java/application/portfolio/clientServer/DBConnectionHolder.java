package application.portfolio.clientServer;

import application.portfolio.utils.PropertiesLoader;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class DBConnectionHolder {

    private static final String STATEMENTS_PROPS = "/config/db/statements.properties";
    private static Map<String, String> statements;
    private static Connection connection;

    public static boolean setUpConnection() {
        return loadStatements() && connect();
    }

    private static boolean loadStatements() {

        try {
            statements = PropertiesLoader.getProperties(STATEMENTS_PROPS)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> entry.getValue().toString()
                    ));

            return !statements.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean connect() {
        String connectionProperties = "/config/db/connection.properties";
        Properties props = PropertiesLoader.getProperties(connectionProperties);

        var dataSource = new SQLServerDataSource();
        dataSource.setServerName(props.getProperty("serverName"));
        dataSource.setPortNumber(Integer.parseInt(props.getProperty("port")));
        dataSource.setDatabaseName(props.getProperty("databaseName"));
        dataSource.setApplicationName(props.getProperty("applicationName"));
        dataSource.setEncrypt("false");

        try {
            connection = dataSource.getConnection(
                    props.getProperty("login"),
                    props.getProperty("password")
            );

            System.out.println("Connection success");

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            setUpConnection();
        }
        return connection;
    }

    public static String getUserByParamsCall() {
        return statements.get("getUserByParams");
    }

    public static String getUserByIdCall() {
        return statements.get("getUserById");
    }

    public static String modifyUserById() {
        return statements.get("modifyUserById");
    }

    public static String getStatusInfo() {
        return statements.get("getStatusInfo");
    }

    public static String deleteUserById() {
        return statements.get("deleteUserById");
    }

    public static String getUsers() {
        return statements.get("getUsers");
    }

    public static String addUser() {
        return statements.get("addUser");
    }
}