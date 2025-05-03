package sn.wagaane.task_app.presentation.web.tasks;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.wagaane.task_app.application.interfaces.tasks.ITaskService;
import sn.wagaane.task_app.presentation.dto.requests.task_app.TaskRequest;
import sn.wagaane.task_app.presentation.dto.responses.Response;

import javax.ws.rs.DELETE;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;


    @PostMapping("/add")
    @Operation(summary = "ajouter une tâche.")
    public ResponseEntity<Response<Object>> addTask(@RequestBody TaskRequest taskRequest) {
         return ResponseEntity.ok(taskService.addTask(taskRequest));
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "editer une tâche.")
    public ResponseEntity<Response<Object>> editTask(@RequestBody TaskRequest taskRequest,@PathVariable long id) {
         return ResponseEntity.ok(taskService.editTask(taskRequest,id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "supprimer une tâche.")
    public ResponseEntity<Response<Object>> deleteTask(@PathVariable long id) {
         return ResponseEntity.ok(taskService.deleteTask(id));
    }



    @GetMapping("/list")
    @Operation(summary = "Liste des tâches.")
    public ResponseEntity<Response<Object>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "filter", defaultValue = "") String filter,
            @RequestParam(name = "title", defaultValue = "") String title,
            @RequestParam(name = "description", defaultValue = "") String description
    ) {
        return ResponseEntity.ok(taskService.listTask(page, size, filter, title, description));
    }

    @GetMapping("/delete-list-tasks/{taskIds}")
    @Operation(summary = "Supprimer liste taches.")
    public ResponseEntity<Response<Object>> list(@PathVariable String taskIds
    ) {
        return ResponseEntity.ok(taskService.deleteListTask(taskIds));
    }

}
