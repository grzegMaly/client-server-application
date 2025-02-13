package application.portfolio.clientServer.response;

import application.portfolio.objects.dao.Task.TaskDAO;
import application.portfolio.objects.model.Task.Task;
import application.portfolio.objects.model.Task.TaskUtils;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class TaskResponse extends Response<Task, TaskDAO> {

    public TaskResponse() {
    }

    public TaskResponse(String message, int statusCode) {
        super(message, statusCode);
    }

    public TaskResponse taskResponseFromDB(CallableStatement cs, Integer outputPosition)
            throws SQLException {

        Response<Task, TaskDAO> response = executeCallable(cs, outputPosition);
        CallableStatement csR = response.getCallableStatement();
        if (csR == null) {
            return new TaskResponse(response.getMessage(), response.getStatusCode());
        }

        try (csR) {
            ResultSet rs = csR.getResultSet();
            handleResultSet(rs);
        }
        return this;
    }

    private void handleResultSet(ResultSet rs) throws SQLException {

        int cCount = rs.getMetaData().getColumnCount();
        if (!rs.next()) {
            setStatusCode(HTTP_OK);
            setItems(Collections.emptyList());
            return;
        }

        if (cCount == 1) {
            String message = rs.getString(1);
            setMessage(message);
            setStatusCode(HTTP_UNAUTHORIZED);
        } else if (cCount > 1) {
            List<Task> tasks = new ArrayList<>();
            do {
                Task task = TaskUtils.createTask(rs);
                if (task == null) {
                    throw new SQLException();
                }
                tasks.add(task);
            } while (rs.next());
            setItems(tasks);
            setStatusCode(HTTP_OK);
        }
    }
}
