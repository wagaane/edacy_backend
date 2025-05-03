package sn.wagaane.task_app.application.implement.tasks;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.wagaane.task_app.application.interfaces.authentication.AuthenticationService;
import sn.wagaane.task_app.application.interfaces.tasks.ITaskService;
import sn.wagaane.task_app.domain.model.task_app.QTask;
import sn.wagaane.task_app.domain.model.task_app.Task;
import sn.wagaane.task_app.domain.repository.task_app.ITaskRepository;
import sn.wagaane.task_app.presentation.dto.requests.task_app.TaskRequest;
import sn.wagaane.task_app.presentation.dto.requests.task_app.TaskResponseDTO;
import sn.wagaane.task_app.presentation.dto.responses.Response;
import sn.wagaane.task_app.presentation.mappers.task_app.TaskMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImp implements ITaskService {
    private final ITaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AuthenticationService authenticationService;
    @Override
    public Response<Object> addTask(TaskRequest taskRequest) {
        Task newTask = new Task();
        newTask.setDescription(taskRequest.description());
        newTask.setTitle(taskRequest.title());
        newTask.setUtilisateur(authenticationService.getCurrentConnectedUser());
        taskRepository.save(newTask);
        return Response.ok().setMessage("Tâche créee avec succès.");
    }

    @Override
    @Transactional
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

        builder.and(
                QTask.task.utilisateur.email.likeIgnoreCase("%"+ authenticationService.getCurrentConnectedUser().getEmail()+ "%")
        );
        builder.and(
                QTask.task.deleted.isFalse()
        );

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

    @Override
    @Transactional
    public Response<Object> deleteListTask(String taskIds) {
        System.out.println(taskIds);
        List<Long> taskIds_ = Arrays.stream(taskIds.split(",")).map(Long::parseLong).toList();
        System.out.println(taskIds_);
        for (Long taskId : taskIds_) {
            Optional<Task> optionalTask = taskRepository.findByIdAndDeletedFalse(taskId);
            if (optionalTask.isPresent()) {
                optionalTask.get().setDeleted(true);
                taskRepository.save(optionalTask.get());
            }
        }
        return Response.ok().setMessage("Tâches supprimées avec succès.");
    }
}
