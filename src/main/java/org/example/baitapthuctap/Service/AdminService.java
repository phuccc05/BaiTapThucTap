package org.example.baitapthuctap.Service;

import org.example.baitapthuctap.Entity.Users;
import org.example.baitapthuctap.Repository.TasksRepository;
import org.example.baitapthuctap.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Kiểm tra xem user có phải admin không (id = 1)
    public boolean isAdmin(String username) {
        Users user = usersRepository.findByUsername(username);
        return user != null && user.getId().equals(1);
    }

    // Kiểm tra xem user có phải admin không (theo ID)
    public boolean isAdminById(Integer userId) {
        return userId != null && userId.equals(1);
    }

    // ===== QUẢN LÝ USER =====

    // Lấy tất cả users
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    // Lấy user theo ID
    public Optional<Users> getUserById(Integer id) {
        return usersRepository.findById(id);
    }

    // Xóa user
    public boolean deleteUser(Integer id) {
        if (usersRepository.existsById(id)) {
            // Xóa tất cả tasks của user trước
            tasksRepository.findByUsersId(id).forEach(task -> {
                tasksRepository.delete(task);
            });

            // Sau đó xóa user
            usersRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Reset mật khẩu user về 123456
    public boolean resetUserPassword(Integer id) {
        Optional<Users> userOptional = usersRepository.findById(id);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setMatKhau(passwordEncoder.encode("123456"));
            user.setNgaySua(LocalDateTime.now());
            usersRepository.save(user);
            return true;
        }
        return false;
    }

    // Cập nhật thông tin user
    public Optional<Users> updateUser(Integer id, Users userUpdate) {
        Optional<Users> userOptional = usersRepository.findById(id);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            // Cập nhật các trường (trừ mật khẩu)
            if (userUpdate.getUsername() != null) {
                user.setUsername(userUpdate.getUsername());
            }
            if (userUpdate.getEmail() != null) {
                user.setEmail(userUpdate.getEmail());
            }
            if (userUpdate.getGioiTinh() != null) {
                user.setGioiTinh(userUpdate.getGioiTinh());
            }

            user.setNgaySua(LocalDateTime.now());
            Users savedUser = usersRepository.save(user);
            return Optional.of(savedUser);
        }
        return Optional.empty();
    }

    // ===== THỐNG KÊ =====

    // Lấy thống kê tổng quan
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Thống kê users
        long totalUsers = usersRepository.count();
        statistics.put("totalUsers", totalUsers);

        // Thống kê tasks
        long totalTasks = tasksRepository.count();
        long completedTasks = tasksRepository.countByTrangThai(true);
        long pendingTasks = tasksRepository.countByTrangThai(false);

        statistics.put("totalTasks", totalTasks);
        statistics.put("completedTasks", completedTasks);
        statistics.put("pendingTasks", pendingTasks);

        // Tỷ lệ hoàn thành
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        statistics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        // Thống kê theo user
        List<Users> users = usersRepository.findAll();
        Map<String, Object> userStats = new HashMap<>();

        for (Users user : users) {
            Map<String, Object> userStat = new HashMap<>();
            long userTotalTasks = tasksRepository.findByUsersId(user.getId()).size();
            long userCompletedTasks = tasksRepository.countCompletedTasksByUser(user.getId());
            long userPendingTasks = tasksRepository.countPendingTasksByUser(user.getId());

            userStat.put("totalTasks", userTotalTasks);
            userStat.put("completedTasks", userCompletedTasks);
            userStat.put("pendingTasks", userPendingTasks);
            userStat.put("username", user.getUsername());
            userStat.put("email", user.getEmail());

            userStats.put("user_" + user.getId(), userStat);
        }

        statistics.put("userStatistics", userStats);

        return statistics;
    }

    // Lấy thống kê của user cụ thể
    public Map<String, Object> getUserStatistics(Integer userId) {
        Map<String, Object> statistics = new HashMap<>();

        Optional<Users> userOptional = usersRepository.findById(userId);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            long totalTasks = tasksRepository.findByUsersId(userId).size();
            long completedTasks = tasksRepository.countCompletedTasksByUser(userId);
            long pendingTasks = tasksRepository.countPendingTasksByUser(userId);

            statistics.put("userId", userId);
            statistics.put("username", user.getUsername());
            statistics.put("email", user.getEmail());
            statistics.put("totalTasks", totalTasks);
            statistics.put("completedTasks", completedTasks);
            statistics.put("pendingTasks", pendingTasks);

            double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
            statistics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

            statistics.put("createdAt", user.getNgayTao());
            statistics.put("updatedAt", user.getNgaySua());
        }

        return statistics;
    }

    // Tìm kiếm users
    public List<Users> searchUsers(String keyword) {
        // Có thể thêm method này vào UsersRepository
        return usersRepository.findAll().stream()
                .filter(user ->
                        user.getUsername().toLowerCase().contains(keyword.toLowerCase()) ||
                                user.getEmail().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();
    }

    // Kiểm tra xem username có tồn tại không (trừ user hiện tại)
    public boolean isUsernameExists(String username, Integer excludeUserId) {
        Users user = usersRepository.findByUsername(username);
        return user != null && !user.getId().equals(excludeUserId);
    }

    // Kiểm tra xem email có tồn tại không (trừ user hiện tại)
    public boolean isEmailExists(String email, Integer excludeUserId) {
        Optional<Users> user = usersRepository.findByEmail(email);
        return user.isPresent() && !user.get().getId().equals(excludeUserId);
    }
}