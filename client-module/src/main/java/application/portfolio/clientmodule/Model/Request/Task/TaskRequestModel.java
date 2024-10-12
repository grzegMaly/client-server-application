package application.portfolio.clientmodule.Model.Request.Task;

import application.portfolio.clientmodule.Model.Request.Task.TaskRequest.TaskRequest;

public class TaskRequestModel {

    public void save(TaskRequest req) {
        System.out.println("Saving " + req);
    }
}