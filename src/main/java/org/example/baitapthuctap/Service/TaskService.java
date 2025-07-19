package org.example.baitapthuctap.Service;

import org.example.baitapthuctap.DTO.TaskDTO;
import org.example.baitapthuctap.DTO.TaskRequest;
import org.example.baitapthuctap.Entity.Tasks;
import org.example.baitapthuctap.Entity.Users;
import org.example.baitapthuctap.Repository.TasksRepository;
import org.example.baitapthuctap.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private UsersRepository usersRepository;

    // Lấy user hiện tại theo username
    public Users getCurrentUser(String username) {
        return usersRepository.findByUsername(username);
    }

    // Chuyển đổi Task entity sang TaskDTO
    private TaskDTO convertToDTO(Tasks task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setMoTa(task.getMoTa());
        dto.setTrangThai(task.getTrangThai());
        dto.setNgayTao(task.getNgayTao());
        dto.setNgaySua(task.getNgaySua());
        dto.setUsernameOwner(task.getUsers().getUsername());
        return dto;
    }

    public Page<TaskDTO> getAllTasksByUser(String username, Pageable pageable) {
        Users user = getCurrentUser(username);
        Page<Tasks> taskPage = tasksRepository.findByUsersId(user.getId(), pageable);
        return taskPage.map(this::convertToDTO);
    }

    // Lấy tất cả tasks của user
//    public List<TaskDTO> getAllTasksByUser(String username) {
//        Users user = getCurrentUser(username);
//        List<Tasks> tasks = tasksRepository.findByUsersId(user.getId());
//        return tasks.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }


    // Lấy task theo ID (chỉ nếu thuộc về user)
    public Optional<TaskDTO> getTaskById(Integer id, String username) {
        Users user = getCurrentUser(username);
        Optional<Tasks> task = tasksRepository.findByIdAndUsersId(id, user.getId());
        return task.map(this::convertToDTO);
    }

    // Tạo task mới
    public TaskDTO createTask(TaskRequest request, String username) {
        Users user = getCurrentUser(username);

        Tasks task = new Tasks();
        task.setTitle(request.getTitle());
        task.setMoTa(request.getMoTa());
        task.setTrangThai(request.getTrangThai());
        task.setUsers(user);
        task.setNgayTao(LocalDateTime.now());

        Tasks savedTask = tasksRepository.save(task);
        return convertToDTO(savedTask);
    }

    // Cập nhật task
    public Optional<TaskDTO> updateTask(Integer id, TaskRequest request, String username) {
        Users user = getCurrentUser(username);
        Optional<Tasks> taskOptional = tasksRepository.findByIdAndUsersId(id, user.getId());

        if (taskOptional.isPresent()) {
            Tasks task = taskOptional.get();
            task.setTitle(request.getTitle());
            task.setMoTa(request.getMoTa());
            task.setTrangThai(request.getTrangThai());
            task.setNgaySua(LocalDateTime.now());

            Tasks updatedTask = tasksRepository.save(task);
            return Optional.of(convertToDTO(updatedTask));
        }
        return Optional.empty();
    }

    // Xóa task
    public boolean deleteTask(Integer id, String username) {
        Users user = getCurrentUser(username);
        Optional<Tasks> taskOptional = tasksRepository.findByIdAndUsersId(id, user.getId());

        if (taskOptional.isPresent()) {
            tasksRepository.delete(taskOptional.get());
            return true;
        }
        return false;
    }

    // Lấy tasks theo trạng thái
    public List<TaskDTO> getTasksByStatus(Boolean status, String username) {
        Users user = getCurrentUser(username);
        List<Tasks> tasks = tasksRepository.findByUsersIdAndTrangThai(user.getId(), status);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Tìm kiếm tasks theo title
    public List<TaskDTO> searchTasks(String title, String username) {
        Users user = getCurrentUser(username);
        List<Tasks> tasks = tasksRepository.findByUsersIdAndTitleContaining(user.getId(), title);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Thống kê tasks
    public TaskStatistics getTaskStatistics(String username) {
        Users user = getCurrentUser(username);
        Long completedTasks = tasksRepository.countCompletedTasksByUser(user.getId());
        Long pendingTasks = tasksRepository.countPendingTasksByUser(user.getId());
        return new TaskStatistics(completedTasks, pendingTasks);
    }

    // Inner class cho thống kê
    public static class TaskStatistics {
        private Long completedTasks;
        private Long pendingTasks;

        public TaskStatistics(Long completedTasks, Long pendingTasks) {
            this.completedTasks = completedTasks;
            this.pendingTasks = pendingTasks;
        }

        public Long getCompletedTasks() { return completedTasks; }
        public Long getPendingTasks() { return pendingTasks; }
        public Long getTotalTasks() { return completedTasks + pendingTasks; }
    }
}