package application.portfolio.clientServer.response;

import application.portfolio.objects.dao.group.GroupDAO;
import application.portfolio.objects.model.Group.Group;
import application.portfolio.objects.model.Group.GroupUtils;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class GroupResponse extends Response<Group, GroupDAO> {

    public GroupResponse() {

    }

    public GroupResponse(String message, int statusCode) {
        super(message, statusCode);
    }


    public GroupResponse groupResponseFromDB(CallableStatement cs, Integer outputPosition)
            throws SQLException {

        Response<Group, GroupDAO> response = executeCallable(cs, outputPosition);
        CallableStatement csR = response.getCallableStatement();

        if (csR == null) {
            return new GroupResponse(response.getMessage(), response.getStatusCode());
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
            List<Group> groups = new ArrayList<>();
            do {
                Group group = GroupUtils.createGroup(rs);
                if (group == null) {
                    throw new SQLException();
                }
                groups.add(group);
            } while (rs.next());
            setItems(groups);
            setStatusCode(HTTP_OK);
        }
    }
}
