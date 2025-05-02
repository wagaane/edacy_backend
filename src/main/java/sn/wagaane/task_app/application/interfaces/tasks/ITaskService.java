package sn.wagaane.task_app.application.interfaces.tasks;

import sn.wagaane.task_app.presentation.dto.requests.task_app.TaskRequest;
import sn.wagaane.task_app.presentation.dto.responses.Response;

public interface ITask {
    Response<Object> addTask(TaskRequest taskRequest);
    Response<Object> deleteTask(long id);
    Response<Object> editTask(TaskRequest taskRequest, long id);
    Response<Object> listTask(int page, int size, String filter, String title, String description);
}
