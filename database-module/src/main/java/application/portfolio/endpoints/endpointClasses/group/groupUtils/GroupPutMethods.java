package application.portfolio.endpoints.endpointClasses.group.groupUtils;

import application.portfolio.clientServer.DBConnectionHolder;
import application.portfolio.clientServer.response.GroupResponse;
import application.portfolio.objects.model.Group.Group;
import application.portfolio.objects.model.Group.GroupUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class GroupPutMethods {

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

                SQLServerDataTable groupDataTable = GroupPostMethods.getGroupDataTable(GroupPostMethods.GROUP_PARAMS);
                for (Group g : groups) {
                    groupDataTable.addRow(g.getGroupId(), g.getGroupName(), g.getOwnerId());
                }

                cs.setStructured(1, "GroupData", groupDataTable);
                GroupResponse groupResponse = new GroupResponse()
                        .groupResponseFromDB(cs, null);

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
}
