package sn.wagaane.task_app.application.implement.tasks;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import sn.wagaane.task_app.application.interfaces.tasks.ITask;
import sn.wagaane.task_app.domain.model.task_app.QTask;
import sn.wagaane.task_app.domain.model.task_app.Task;
import sn.wagaane.task_app.domain.model.utilisateur.Profile;
import sn.wagaane.task_app.domain.model.utilisateur.QUtilisateur;
import sn.wagaane.task_app.domain.repository.task_app.ITaskRepository;
import sn.wagaane.task_app.presentation.dto.requests.task_app.TaskRequest;
import sn.wagaane.task_app.presentation.dto.requests.task_app.TaskResponseDTO;
import sn.wagaane.task_app.presentation.dto.responses.Response;
import sn.wagaane.task_app.presentation.mappers.task_app.TaskMapper;

import java.util.Objects;
import java.util.Optional;
@RequiredArgsConstructor
public class TaskImp implements ITask {
    private final ITaskRepository taskRepository;
    private final TaskMapper taskMapper;
    @Override
    public Response<Object> addTask(TaskRequest taskRequest) {
        Task newTask = new Task();
        newTask.setDescription(taskRequest.description());
        newTask.setTitle(taskRequest.title());
        return Response.ok().setMessage("Tâche créee avec succès.");
    }

    @Override
    public Response<Object> deleteTask(long id) {
        if (id <= 0) {
            return Response.exception().setMessage("ID invalide.");
        }


        Optional<Task> optionalTask = taskRepository.findByIdAndDeletedFalse(id);
        if (optionalTask.isPresent()) {
            optionalTask.get().setDeleted(true);
            taskRepository.save(optionalTask.get());
            return Response.ok().setMessage("Tâche supprimée.");
        }
        return Response.exception().setMessage("Tâche inexistente.");
    }

    @Override
    public Response<Object> editTask(TaskRequest taskRequest, long id) {
        if (id <= 0) {
            return Response.exception().setMessage("ID invalide.");
        }


        Optional<Task> optionalTask = taskRepository.findByIdAndDeletedFalse(id);
        if (optionalTask.isPresent()) {
            optionalTask.get().setDescription(taskRequest.description());
            optionalTask.get().setTitle(taskRequest.title());
            taskRepository.save(optionalTask.get());
            return Response.ok().setMessage("Tâche modifiée avec succès.");
        }
        return Response.exception().setMessage("Tâche inexistente.");
    }

    @Override
    public Response<Object> listTask(int page, int size, String filter, String title, String description) {
        Page<TaskResponseDTO> taskResponseDTOS;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.isNotBlank(filter)){
            builder.andAnyOf(
                    QTask.task.description.likeIgnoreCase("%" + filter + "%"),
                    QTask.task.title.likeIgnoreCase("%" + filter + "%")
            );
        }

        if(StringUtils.isNotBlank(title)){
            builder.and(
                    QTask.task.title.likeIgnoreCase("%"+ title+ "%")
            );
        }

        if(StringUtils.isNotBlank(description)){
            builder.and(
                    QTask.task.description.likeIgnoreCase("%" + description + "%")
            );
        }



        taskResponseDTOS = Objects.nonNull(builder.getValue()) ?
                taskRepository.findAll(builder.getValue(), PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "id"))).map(taskMapper::map)
                :
                taskRepository.findAll(PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "id"))).map(taskMapper::map);

        Response.PageMetadata pageMetadata = Response.PageMetadata.builder()
                .size(taskResponseDTOS.getSize())
                .number(taskResponseDTOS.getNumber())
                .totalElements(taskResponseDTOS.getTotalElements())
                .totalPages(taskResponseDTOS.getTotalPages())
                .build();
        return Response.ok().setPayload(taskResponseDTOS.getContent()).setMetadata(pageMetadata).setMessage("Liste des Utilisateurs");
    }
}
