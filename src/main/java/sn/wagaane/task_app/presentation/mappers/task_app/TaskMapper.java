package sn.wagaane.task_app.presentation.mappers.task_app;

import org.mapstruct.Mapper;
import sn.wagaane.task_app.domain.model.task_app.Task;
import sn.wagaane.task_app.presentation.dto.requests.task_app.TaskResponseDTO;

@Mapper
public interface TaskMapper {
    TaskResponseDTO map(Task task);
}
