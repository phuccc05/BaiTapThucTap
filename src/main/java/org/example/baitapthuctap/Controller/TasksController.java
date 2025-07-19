package org.example.baitapthuctap.Controller;

import org.example.baitapthuctap.DTO.TaskDTO;
import org.example.baitapthuctap.DTO.TaskRequest;
import org.example.baitapthuctap.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class TasksController {

    @Autowired
    private TaskService taskService;

    //GET /api/tasks - Lấy tất cả tasks của user hiện tại
    @GetMapping
    public ResponseEntity<?> getAllTasks(
            Authentication authentication,
            @PageableDefault(size = 5, page = 0) Pageable pageable) {
        try {
            String username = authentication.getName();
            Page<TaskDTO> tasksPage = taskService.getAllTasksByUser(username, pageable);
            return ResponseEntity.ok(Map.of(
                    "tasks", tasksPage.getContent(),
                    "currentPage", tasksPage.getNumber(),
                    "totalPages", tasksPage.getTotalPages(),
                    "totalItems", tasksPage.getTotalElements()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy danh sách task"));
        }
    }

    //GET /api/tasks/{id} - Lấy task theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Integer id, Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<TaskDTO> task = taskService.getTaskById(id, username);

            if (task.isPresent()) {
                return ResponseEntity.ok(task.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Task không tồn tại hoặc không thuộc về bạn"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy task"));
        }
    }

    //POST /api/tasks - Tạo task mới
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            TaskDTO createdTask = taskService.createTask(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tạo task"));
        }
    }

    //PUT /api/tasks/{id} - Cập nhật task
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Integer id,
                                        @Valid @RequestBody TaskRequest request,
                                        Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<TaskDTO> updatedTask = taskService.updateTask(id, request, username);

            if (updatedTask.isPresent()) {
                return ResponseEntity.ok(updatedTask.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Task không tồn tại hoặc không thuộc về bạn"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi cập nhật task"));
        }
    }

    //DELETE /api/tasks/{id} - Xóa task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id, Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean deleted = taskService.deleteTask(id, username);

            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Xóa task thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Task không tồn tại hoặc không thuộc về bạn"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi xóa task"));
        }
    }

    //GET /api/tasks/status/{status} - Lấy tasks theo trạng thái
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable Boolean status, Authentication authentication) {
        try {
            String username = authentication.getName();
            List<TaskDTO> tasks = taskService.getTasksByStatus(status, username);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //GET /api/tasks/search?title={title} - Tìm kiếm tasks
    @GetMapping("/search")
    public ResponseEntity<List<TaskDTO>> searchTasks(@RequestParam String title, Authentication authentication) {
        try {
            String username = authentication.getName();
            List<TaskDTO> tasks = taskService.searchTasks(title, username);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //GET /api/tasks/statistics - Thống kê tasks
    @GetMapping("/statistics")
    public ResponseEntity<?> getTaskStatistics(Authentication authentication) {
        try {
            String username = authentication.getName();
            TaskService.TaskStatistics stats = taskService.getTaskStatistics(username);
            return ResponseEntity.ok(Map.of(
                    "completedTasks", stats.getCompletedTasks(),
                    "pendingTasks", stats.getPendingTasks(),
                    "totalTasks", stats.getTotalTasks()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy thống kê"));
        }
    }
}