package org.example.baitapthuctap.Service;

import org.example.baitapthuctap.DTO.TaskDTO;
import org.example.baitapthuctap.Entity.Tasks;
import org.example.baitapthuctap.Repository.TasksRepository;
import org.example.baitapthuctap.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminTaskService {

    @Autowired
    private TasksRepository tasksRepository;

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

    // Lấy tất cả tasks của tất cả users
    public List<TaskDTO> getAllTasks() {
        List<Tasks> tasks = tasksRepository.findAll();
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy tất cả tasks của user cụ thể
    public List<TaskDTO> getTasksByUser(Integer userId) {
        List<Tasks> tasks = tasksRepository.findByUsersId(userId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy task theo ID (không cần kiểm tra owner)
    public Optional<TaskDTO> getTaskById(Integer id) {
        Optional<Tasks> task = tasksRepository.findById(id);
        return task.map(this::convertToDTO);
    }

    // Xóa task bất kỳ
    public boolean deleteTask(Integer id) {
        if (tasksRepository.existsById(id)) {
            tasksRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Lấy tasks theo trạng thái
    public List<TaskDTO> getTasksByStatus(Boolean status) {
        List<Tasks> tasks = tasksRepository.findByTrangThai(status);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Tìm kiếm tasks theo title
    public List<TaskDTO> searchTasks(String title) {
        List<Tasks> tasks = tasksRepository.findByTitleContainingIgnoreCase(title);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
//
//    // Thống kê tasks tổng quan
//    public TaskStatistics getTaskStatistics() {
//        Long totalTasks = tasksRepository.count();
//        Long completedTasks = tasksRepository.countByTrangThai(true);
//        Long pendingTasks = tasksRepository.countByTrangThai(false);
//
//        return new TaskStatistics(totalTasks, completedTasks, pendingTasks);
//    }
}

// Thống k