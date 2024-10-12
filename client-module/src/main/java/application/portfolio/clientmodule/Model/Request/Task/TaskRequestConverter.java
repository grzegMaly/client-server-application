package application.portfolio.clientmodule.Model.Request.Task;

import application.portfolio.clientmodule.Model.Request.Task.TaskRequest.TaskRequest;

public class TaskRequestConverter {

    public TaskRequest toTaskRequest(TaskRequestViewModel viewModel) {
        return new TaskRequest(
                viewModel.getTitle(),
                viewModel.getAssignedToId(),
                viewModel.getAssignedById(),
                viewModel.getCreatedDate(),
                viewModel.getDeadline(),
                viewModel.getDescription()
        );
    }
}