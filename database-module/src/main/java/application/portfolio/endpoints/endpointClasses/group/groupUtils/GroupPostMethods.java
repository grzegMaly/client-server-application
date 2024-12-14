package application.portfolio.endpoints.endpointClasses.group.groupUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.objects.model.Group.Group;
import application.portfolio.objects.model.Group.GroupUtils;
import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.function.Consumer;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;


public class GroupPostMethods {

    private static final List<Map.Entry<String, Integer>> GROUP_PARAMS = List.of(
            Map.entry("id", Types.NVARCHAR),
            Map.entry("groupName", Types.NVARCHAR),
            Map.entry("ownerId", Types.NVARCHAR)
    );

    private static final List<Map.Entry<String, Integer>> USER_GROUP_PARAMS = List.of(
            Map.entry("groupId", Types.NVARCHAR),
            Map.entry("ownerId", Types.NVARCHAR)
    );

    private static final List<Map.Entry<String, Integer>> USER_MOVE_PARAMS = List.of(
            Map.entry("userId", Types.NVARCHAR),
            Map.entry("fromGroup", Types.NVARCHAR),
            Map.entry("toGroup", Types.NVARCHAR)
    );

    public static GroupResponse modifyGroup(JsonNode node) {

        List<Group> groups = GroupUtils.createGroup(node);
        if (groups.isEmpty()) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.modifyGroupById()
            )) {

                SQLServerDataTable groupDataTable = getGroupDataTable(GROUP_PARAMS);
                for (Group g : groups) {
                    groupDataTable.addRow(g.getGroupId(), g.getGroupName(), g.getOwnerId());
                }

                cs.setStructured(1, "GroupData", groupDataTable);
                cs.registerOutParameter(2, Types.INTEGER);
                GroupResponse groupResponse = new GroupResponse()
                        .groupResponseFromDB(cs, 2);

                conn.commit();
                return groupResponse;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }
    }

    public static GroupResponse addGroup(JsonNode node) {

        List<Group> groups = GroupUtils.createGroup(node);

        if (groups.isEmpty()) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        }

        Connection conn = DBConnectionHolder.getConnection();
        try {
            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.addGroup()
            )) {

                SQLServerDataTable groupDataTable = getGroupDataTable(GROUP_PARAMS);
                for (Group g : groups) {
                    groupDataTable.addRow(null, g.getGroupName(), g.getOwnerId());
                }

                cs.setStructured(1, "GroupData", groupDataTable);
                cs.registerOutParameter(2, Types.INTEGER);
                GroupResponse groupResponse = new GroupResponse()
                        .groupResponseFromDB(cs, 2);

                conn.commit();
                return groupResponse;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }
    }

    public static GroupResponse addUserToGroup(JsonNode node) {

        Map<UUID, Set<UUID>> ids = new HashMap<>();
        Consumer<JsonNode> gpConsumer = n -> {
            if (n.size() == 2 && n.hasNonNull("groupId") && n.hasNonNull  ("userId")) {
                UUID groupId = DataParser.parseId(n.get("groupId").asText());
                UUID userId = DataParser.parseId(n.get("userId").asText());

                if (groupId != null && userId != null) {
                    ids.computeIfAbsent(groupId, k -> new HashSet<>()).add(userId);
                }
            }
        };

        if (execConsumer(node, gpConsumer, ids)) return new GroupResponse("Bad Data", HTTP_FORBIDDEN);

        Connection conn = DBConnectionHolder.getConnection();
        try {

            conn.setAutoCommit(false);
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.addUserToGroup()
            )) {

                SQLServerDataTable userGroupDataTable = getGroupDataTable(USER_GROUP_PARAMS);
                
                for (Map.Entry<UUID, Set<UUID>> entry : ids.entrySet()) {
                    UUID groupId = entry.getKey();
                    Set<UUID> set = entry.getValue();
                    for (UUID userId : set) {
                        userGroupDataTable.addRow(groupId, userId);
                    }
                }

                cs.setStructured(1, "UserGroup", userGroupDataTable);
                cs.registerOutParameter(2, Types.INTEGER);

                GroupResponse groupResponse = new GroupResponse()
                        .groupResponseFromDB(cs, 2);

                conn.commit();
                return groupResponse;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }
    }

    public static GroupResponse moveUserToGroup(JsonNode node) {

        Map<UUID, Map<UUID, UUID>> ids = new HashMap<>();

        Consumer<JsonNode> nodeConsumer = n -> {
            if (n.size() == 3 && n.hasNonNull("fromGroup") &&
                    n.hasNonNull("toGroup") && n.hasNonNull("userId")) {

                UUID userId = DataParser.parseId(n.get("userId").asText());
                UUID fromGroup = DataParser.parseId(n.get("fromGroup").asText());
                UUID toGroup = DataParser.parseId(n.get("toGroup").asText());

                if (userId != null && fromGroup != null && toGroup != null) {
                    ids.computeIfAbsent(userId, k -> new HashMap<>()).put(fromGroup, toGroup);
                }
            }
        };

        if (execConsumer(node, nodeConsumer, ids)) return new GroupResponse("Bad Data", HTTP_FORBIDDEN);

        Connection conn = DBConnectionHolder.getConnection();
        try {
            try (SQLServerCallableStatement cs = (SQLServerCallableStatement) conn.prepareCall(
                    DBConnectionHolder.moveUserToGroup()
            )) {

                SQLServerDataTable userMoveTable = getGroupDataTable(USER_MOVE_PARAMS);

                for (Map.Entry<UUID, Map<UUID, UUID>> entry : ids.entrySet()) {
                    UUID userId = entry.getKey();
                    Map<UUID, UUID> map = entry.getValue();
                    for (Map.Entry<UUID, UUID> groupEntry : map.entrySet()) {
                        UUID oGroup = groupEntry.getKey();
                        UUID nGroup = groupEntry.getValue();
                        userMoveTable.addRow(userId, oGroup, nGroup);
                    }
                }

                cs.setStructured(1, "MoveData", userMoveTable);
                cs.registerOutParameter(2, Types.INTEGER);

                GroupResponse groupResponse = new GroupResponse()
                        .groupResponseFromDB(cs, 2);

                conn.commit();
                return groupResponse;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return new GroupResponse("Unknown Error", HTTP_INTERNAL_ERROR);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //Nothing
            }
        }
    }

    private static boolean execConsumer(JsonNode node, Consumer<JsonNode> consumer, Map<UUID, ?> ids) {
        if (node.isArray()) {
            node.forEach(consumer);
        } else if (node.isObject()) {
            consumer.accept(node);
        } else {
            return false;
        }
        return ids.isEmpty();
    }

    private static SQLServerDataTable getGroupDataTable(List<Map.Entry<String, Integer>> params) throws SQLServerException {
        SQLServerDataTable groupDataTable = new SQLServerDataTable();
        for (Map.Entry<String, Integer> entry : params) {
            groupDataTable.addColumnMetadata(entry.getKey(), entry.getValue());
        }
        return groupDataTable;
    }
}
