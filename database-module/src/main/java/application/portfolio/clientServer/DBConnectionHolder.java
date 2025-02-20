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

    public static String getStatusInfo() {
        return statements.get("getStatusInfo");
    }

    //--------------------------------------------------------------------

    public static String getUserByParams() {
        return statements.get("getUserByParams");
    }

    public static String getUserById() {
        return statements.get("getUserById");
    }

    public static String getAllUsers() {
        return statements.get("getAllUsers");
    }

    public static String modifyUser() {
        return statements.get("modifyUser");
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

    //--------------------------------------------------------------------

    public static String getGroupById() {
        return statements.get("getGroupById");
    }

    public static String getGroups() {
        return statements.get("getGroups");
    }

    public static String getAllGroups() {
        return statements.get("getAllGroups");
    }

    public static String modifyGroupById() {
        return statements.get("modifyGroupById");
    }

    public static String addGroup() {
        return statements.get("addGroup");
    }

    public static String deleteGroupById() {
        return statements.get("deleteGroupById");
    }

    //--------------------------------------------------------------------

    public static String getGroupMembers() {
        return statements.get("getGroupMembers");
    }

    public static String addUserToGroup() {
        return statements.get("addUserToGroup");
    }

    public static String getUserGroups() {
        return statements.get("getUserGroups");
    }

    public static String deleteUserFromGroup() {
        return statements.get("deleteUserFromGroup");
    }

    public static String getUserColleagues() {
        return statements.get("getUserColleagues");
    }

    public static String moveUserToGroup() {
        return statements.get("moveUserToGroup");
    }

    public static String getChatHistory() {
        return statements.get("getChatHistory");
    }

    public static String saveMessage() {
        return statements.get("saveMessage");
    }

    //--------------------------------------------------------------------

    public static String loadReceivedTasks() {
        return statements.get("loadReceivedTasks");
    }

    public static String loadCreatedTasks() {
        return statements.get("loadCreatedTasks");
    }

    public static String createTask() {
        return statements.get("createTask");
    }

    public static String updateTask() {
        return statements.get("updateTask");
    }

    public static String deleteTask() {
        return statements.get("deleteTask");
    }
}