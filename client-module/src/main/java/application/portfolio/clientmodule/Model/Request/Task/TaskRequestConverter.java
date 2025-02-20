package application.portfolio.clientmodule.Model.Request.Task;

import application.portfolio.clientmodule.Model.Request.Task.TaskRequest.TaskRequest;
import application.portfolio.clientmodule.utils.DataParser;

import java.util.Map;
import java.util.UUID;

public class TaskRequestConverter {

    public static String toQueryLoadParams(TaskRequest taskRequest, String userType) {

        UUID userId = taskRequest.getUserId();
        return DataParser.paramsString(Map.of(userType, userId.toString()));
    }

    public static String toQueryDeleteParams(TaskRequest taskRequest) {

        UUID userId = taskRequest.getUserId();
        UUID taskId = taskRequest.getTaskId();
        return DataParser.paramsString(Map.of(
                "userId", userId.toString(),
                "taskId", taskId.toString()
        ));
    }

    public TaskRequest toUserRequest(UUID userId) {
        return new TaskRequest(userId);
    }

    public TaskRequest toUserTaskRequest(UUID userId, UUID taskId) {
        return new TaskRequest(taskId, userId);
    }
}