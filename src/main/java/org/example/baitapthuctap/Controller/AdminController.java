package org.example.baitapthuctap.Controller;

import org.example.baitapthuctap.DTO.AdminUserRequest;
import org.example.baitapthuctap.DTO.TaskDTO;
import org.example.baitapthuctap.Entity.Users;
import org.example.baitapthuctap.Service.AdminService;
import org.example.baitapthuctap.Service.AdminTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminTaskService adminTaskService;

    // Kiểm tra quyền admin
    private boolean isAdmin(Authentication authentication) {
        return adminService.isAdmin(authentication.getName());
    }

    // ===== QUẢN LÝ USER =====

    // GET /api/admin/users - Lấy tất cả users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            List<Users> users = adminService.getAllUsers();
            return ResponseEntity.ok(Map.of(
                    "users", users,
                    "total", users.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy danh sách user"));
        }
    }

    // GET /api/admin/users/{id} - Lấy user theo ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            Optional<Users> user = adminService.getUserById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy user"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy thông tin user"));
        }
    }

    // DELETE /api/admin/users/{id} - Xóa user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            // Không cho phép xóa chính mình (admin)
            if (id == 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Không thể xóa tài khoản admin"));
            }

            boolean deleted = adminService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Xóa user thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy user"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi xóa user"));
        }
    }

    // PUT /api/admin/users/{id}/reset-password - Reset mật khẩu user
    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable Integer id, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            boolean reset = adminService.resetUserPassword(id);
            if (reset) {
                return ResponseEntity.ok(Map.of("message", "Reset mật khẩu thành công", "newPassword", "123456"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy user"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi reset mật khẩu"));
        }
    }

    // PUT /api/admin/users/{id} - Cập nhật thông tin user
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id,
                                        @RequestBody AdminUserRequest request,
                                        Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }
            if (id == 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Không thể cập nhật tài khoản admin"));
            }
            // validate username
            if (adminService.isUsernameExists(request.getUsername(), id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Username đã tồn tại"));
            }

            // validate email
            if (adminService.isEmailExists(request.getEmail(), id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Email đã tồn tại"));
            }

            Users userUpdate = new Users();
            userUpdate.setUsername(request.getUsername());
            userUpdate.setEmail(request.getEmail());
            userUpdate.setGioiTinh(request.getGioiTinh());

            Optional<Users> updatedUser = adminService.updateUser(id, userUpdate);
            if (updatedUser.isPresent()) {
                return ResponseEntity.ok(Map.of("message", "Cập nhật user thành công", "user", updatedUser.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy user"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi cập nhật user"));
        }
    }

    // GET /api/admin/users/search?keyword={keyword} - tìm kiếm users
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            List<Users> users = adminService.searchUsers(keyword);
            return ResponseEntity.ok(Map.of(
                    "users", users,
                    "total", users.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tìm kiếm users"));
        }
    }

    // GET /api/admin/users/{id}/statistics - lấy thống kê user
    @GetMapping("/users/{id}/statistics")
    public ResponseEntity<?> getUserStatistics(@PathVariable Integer id, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            Map<String, Object> statistics = adminService.getUserStatistics(id);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy thống kê user"));
        }
    }

    // ===== QUẢN LÝ TASK =====

    // GET /api/admin/tasks - lấy tất cả tasks
    @GetMapping("/tasks")
    public ResponseEntity<?> getAllTasks(Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            List<TaskDTO> tasks = adminTaskService.getAllTasks();
            return ResponseEntity.ok(Map.of(
                    "tasks", tasks,
                    "total", tasks.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy danh sách task"));
        }
    }

    // GET /api/admin/tasks/user/{userId} - lấy tasks của user
    @GetMapping("/tasks/user/{userId}")
    public ResponseEntity<?> getTasksByUser(@PathVariable Integer userId, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            List<TaskDTO> tasks = adminTaskService.getTasksByUser(userId);
            return ResponseEntity.ok(Map.of(
                    "tasks", tasks,
                    "total", tasks.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy tasks của user"));
        }
    }

    // GET /api/admin/tasks/{id} - lấy task theo ID
    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Integer id, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            Optional<TaskDTO> task = adminTaskService.getTaskById(id);
            if (task.isPresent()) {
                return ResponseEntity.ok(task.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy task"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy task"));
        }
    }

    // DELETE /api/admin/tasks/{id} - xóa task
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            boolean deleted = adminTaskService.deleteTask(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Xóa task thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy task"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi xóa task"));
        }
    }

    // GET /api/admin/tasks/status/{status} - lấy tasks theo trạng thái
    @GetMapping("/tasks/status/{status}")
    public ResponseEntity<?> getTasksByStatus(@PathVariable Boolean status, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            List<TaskDTO> tasks = adminTaskService.getTasksByStatus(status);
            return ResponseEntity.ok(Map.of(
                    "tasks", tasks,
                    "total", tasks.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy tasks theo trạng thái"));
        }
    }

    // GET /api/admin/tasks/search?title={title} - tìm kiếm task
    @GetMapping("/tasks/search")
    public ResponseEntity<?> searchTasks(@RequestParam String title, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            List<TaskDTO> tasks = adminTaskService.searchTasks(title);
            return ResponseEntity.ok(Map.of(
                    "tasks", tasks,
                    "total", tasks.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tìm kiếm tasks"));
        }
    }

    // ===== THỐNG KÊ =====

    // GET /api/admin/statistics - thống kê
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics(Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền truy cập"));
            }

            Map<String, Object> statistics = adminService.getStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy thống kê"));
        }
    }
}