package org.example.baitapthuctap.Service;

import org.example.baitapthuctap.DTO.TaskDTO;
import org.example.baitapthuctap.Entity.Tasks;
import org.example.baitapthuctap.Entity.Users;
import org.example.baitapthuctap.Repository.TasksRepository;
import org.example.baitapthuctap.Repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private Users adminUser;
    private Users normalUser;
    private Tasks sampleTask;

    @BeforeEach
    void setUp() {
        // Setup test data
        adminUser = new Users();
        adminUser.setId(1);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setMatKhau("encodedPassword");
        adminUser.setNgayTao(LocalDateTime.now());

        normalUser = new Users();
        normalUser.setId(2);
        normalUser.setUsername("user");
        normalUser.setEmail("user@example.com");
        normalUser.setMatKhau("encodedPassword");
        normalUser.setNgayTao(LocalDateTime.now());

        sampleTask = new Tasks();
        sampleTask.setId(1);
        sampleTask.setTitle("Test Task");
        sampleTask.setMoTa("Test Description");
        sampleTask.setTrangThai(false);
        sampleTask.setUsers(normalUser);
        sampleTask.setNgayTao(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return true when user is admin")
    void testIsAdmin_WhenUserIsAdmin_ShouldReturnTrue() {
        // Given
        when(usersRepository.findByUsername("admin")).thenReturn(adminUser);

        // When
        boolean result = adminService.isAdmin("admin");

        // Then
        assertTrue(result);
        verify(usersRepository).findByUsername("admin");
    }

    @Test
    @DisplayName("Should return false when user is not admin")
    void testIsAdmin_WhenUserIsNotAdmin_ShouldReturnFalse() {
        // Given
        when(usersRepository.findByUsername("user")).thenReturn(normalUser);

        // When
        boolean result = adminService.isAdmin("user");

        // Then
        assertFalse(result);
        verify(usersRepository).findByUsername("user");
    }

    @Test
    @DisplayName("Should return false when user does not exist")
    void testIsAdmin_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        when(usersRepository.findByUsername("nonexistent")).thenReturn(null);

        // When
        boolean result = adminService.isAdmin("nonexistent");

        // Then
        assertFalse(result);
        verify(usersRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should return true when userId is 1")
    void testIsAdminById_WhenUserIdIs1_ShouldReturnTrue() {
        // When
        boolean result = adminService.isAdminById(1);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when userId is not 1")
    void testIsAdminById_WhenUserIdIsNot1_ShouldReturnFalse() {
        // When
        boolean result = adminService.isAdminById(2);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when userId is null")
    void testIsAdminById_WhenUserIdIsNull_ShouldReturnFalse() {
        // When
        boolean result = adminService.isAdminById(null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return all users")
    void testGetAllUsers_ShouldReturnAllUsers() {
        // Given
        List<Users> users = Arrays.asList(adminUser, normalUser);
        when(usersRepository.findAll()).thenReturn(users);

        // When
        List<Users> result = adminService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(adminUser));
        assertTrue(result.contains(normalUser));
        verify(usersRepository).findAll();
    }

    @Test
    @DisplayName("Should return user by id when exists")
    void testGetUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        when(usersRepository.findById(1)).thenReturn(Optional.of(adminUser));

        // When
        Optional<Users> result = adminService.getUserById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(adminUser, result.get());
        verify(usersRepository).findById(1);
    }

    @Test
    @DisplayName("Should return empty when user does not exist")
    void testGetUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(usersRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Users> result = adminService.getUserById(999);

        // Then
        assertFalse(result.isPresent());
        verify(usersRepository).findById(999);
    }

    @Test
    @DisplayName("Should delete user and associated tasks successfully")
    void testDeleteUser_WhenUserExists_ShouldDeleteSuccessfully() {
        // Given
        List<Tasks> userTasks = Arrays.asList(sampleTask);
        when(usersRepository.existsById(2)).thenReturn(true);
        when(tasksRepository.findByUsersId(2)).thenReturn(userTasks);

        // When
        boolean result = adminService.deleteUser(2);

        // Then
        assertTrue(result);
        verify(usersRepository).existsById(2);
        verify(tasksRepository).findByUsersId(2);
        verify(tasksRepository).delete(sampleTask);
        verify(usersRepository).deleteById(2);
    }

    @Test
    @DisplayName("Should return false when trying to delete non-existent user")
    void testDeleteUser_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        when(usersRepository.existsById(999)).thenReturn(false);

        // When
        boolean result = adminService.deleteUser(999);

        // Then
        assertFalse(result);
        verify(usersRepository).existsById(999);
        verify(tasksRepository, never()).findByUsersId(any());
        verify(usersRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should reset user password successfully")
    void testResetUserPassword_WhenUserExists_ShouldResetPassword() {
        // Given
        when(usersRepository.findById(2)).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.encode("123456")).thenReturn("encodedNewPassword");
        when(usersRepository.save(any(Users.class))).thenReturn(normalUser);

        // When
        boolean result = adminService.resetUserPassword(2);

        // Then
        assertTrue(result);
        verify(usersRepository).findById(2);
        verify(passwordEncoder).encode("123456");
        verify(usersRepository).save(normalUser);
        assertEquals("encodedNewPassword", normalUser.getMatKhau());
        assertNotNull(normalUser.getNgaySua());
    }

    @Test
    @DisplayName("Should return false when trying to reset password for non-existent user")
    void testResetUserPassword_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        when(usersRepository.findById(999)).thenReturn(Optional.empty());

        // When
        boolean result = adminService.resetUserPassword(999);

        // Then
        assertFalse(result);
        verify(usersRepository).findById(999);
        verify(passwordEncoder, never()).encode(any());
        verify(usersRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_WhenUserExists_ShouldUpdateSuccessfully() {
        // Given
        Users userUpdate = new Users();
        userUpdate.setUsername("updatedUsername");
        userUpdate.setEmail("updated@example.com");
        userUpdate.setGioiTinh("Nam");

        when(usersRepository.findById(2)).thenReturn(Optional.of(normalUser));
        when(usersRepository.save(any(Users.class))).thenReturn(normalUser);

        // When
        Optional<Users> result = adminService.updateUser(2, userUpdate);

        // Then
        assertTrue(result.isPresent());
        assertEquals("updatedUsername", result.get().getUsername());
        assertEquals("updated@example.com", result.get().getEmail());
        assertEquals("Nam", result.get().getGioiTinh());
        assertNotNull(result.get().getNgaySua());
        verify(usersRepository).findById(2);
        verify(usersRepository).save(normalUser);
    }

    @Test
    @DisplayName("Should return empty when trying to update non-existent user")
    void testUpdateUser_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Given
        Users userUpdate = new Users();
        userUpdate.setUsername("updatedUsername");
        when(usersRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Users> result = adminService.updateUser(999, userUpdate);

        // Then
        assertFalse(result.isPresent());
        verify(usersRepository).findById(999);
        verify(usersRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return statistics correctly")
    void testGetStatistics_ShouldReturnCorrectStatistics() {
        // Given
        List<Users> users = Arrays.asList(adminUser, normalUser);
        when(usersRepository.count()).thenReturn(2L);
        when(usersRepository.findAll()).thenReturn(users);
        when(tasksRepository.count()).thenReturn(5L);
        when(tasksRepository.countByTrangThai(true)).thenReturn(3L);
        when(tasksRepository.countByTrangThai(false)).thenReturn(2L);
        when(tasksRepository.findByUsersId(1)).thenReturn(Arrays.asList(sampleTask));
        when(tasksRepository.findByUsersId(2)).thenReturn(Arrays.asList(sampleTask));
        when(tasksRepository.countCompletedTasksByUser(1)).thenReturn(1L);
        when(tasksRepository.countPendingTasksByUser(1)).thenReturn(0L);
        when(tasksRepository.countCompletedTasksByUser(2)).thenReturn(2L);
        when(tasksRepository.countPendingTasksByUser(2)).thenReturn(1L);

        // When
        Map<String, Object> result = adminService.getStatistics();

        // Then
        assertEquals(2L, result.get("totalUsers"));
        assertEquals(5L, result.get("totalTasks"));
        assertEquals(3L, result.get("completedTasks"));
        assertEquals(2L, result.get("pendingTasks"));
        assertEquals(60.0, result.get("completionRate"));
        assertNotNull(result.get("userStatistics"));

        @SuppressWarnings("unchecked")
        Map<String, Object> userStats = (Map<String, Object>) result.get("userStatistics");
        assertNotNull(userStats.get("user_1"));
        assertNotNull(userStats.get("user_2"));
    }

    @Test
    @DisplayName("Should return user statistics correctly")
    void testGetUserStatistics_WhenUserExists_ShouldReturnStatistics() {
        // Given
        when(usersRepository.findById(2)).thenReturn(Optional.of(normalUser));
        when(tasksRepository.findByUsersId(2)).thenReturn(Arrays.asList(sampleTask));
        when(tasksRepository.countCompletedTasksByUser(2)).thenReturn(1L);
        when(tasksRepository.countPendingTasksByUser(2)).thenReturn(0L);

        // When
        Map<String, Object> result = adminService.getUserStatistics(2);

        // Then
        assertEquals(2, result.get("userId"));
        assertEquals("user", result.get("username"));
        assertEquals("user@example.com", result.get("email"));
        assertEquals(1L, result.get("totalTasks"));
        assertEquals(1L, result.get("completedTasks"));
        assertEquals(0L, result.get("pendingTasks"));
        assertEquals(100.0, result.get("completionRate"));
        assertNotNull(result.get("createdAt"));
    }

    @Test
    @DisplayName("Should search users by keyword")
    void testSearchUsers_ShouldReturnMatchingUsers() {
        // Given
        List<Users> allUsers = Arrays.asList(adminUser, normalUser);
        when(usersRepository.findAll()).thenReturn(allUsers);

        // When
        List<Users> result = adminService.searchUsers("admin");

        // Then
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsername());
        verify(usersRepository).findAll();
    }

    @Test
    @DisplayName("Should check if username exists excluding current user")
    void testIsUsernameExists_WhenUsernameExistsButNotCurrentUser_ShouldReturnFalse() {
        // Given
        when(usersRepository.findByUsername("admin")).thenReturn(adminUser);

        // When
        boolean result = adminService.isUsernameExists("admin", 1);

        // Then
        assertFalse(result);
        verify(usersRepository).findByUsername("admin");
    }

    @Test
    @DisplayName("Should check if username exists for different user")
    void testIsUsernameExists_WhenUsernameExistsForDifferentUser_ShouldReturnTrue() {
        // Given
        when(usersRepository.findByUsername("admin")).thenReturn(adminUser);

        // When
        boolean result = adminService.isUsernameExists("admin", 2);

        // Then
        assertTrue(result);
        verify(usersRepository).findByUsername("admin");
    }

    @Test
    @DisplayName("Should check if email exists excluding current user")
    void testIsEmailExists_WhenEmailExistsButNotCurrentUser_ShouldReturnFalse() {
        // Given
        when(usersRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // When
        boolean result = adminService.isEmailExists("admin@example.com", 1);

        // Then
        assertFalse(result);
        verify(usersRepository).findByEmail("admin@example.com");
    }

    @Test
    @DisplayName("Should check if email exists for different user")
    void testIsEmailExists_WhenEmailExistsForDifferentUser_ShouldReturnTrue() {
        // Given
        when(usersRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // When
        boolean result = adminService.isEmailExists("admin@example.com", 2);

        // Then
        assertTrue(result);
        verify(usersRepository).findByEmail("admin@example.com");
    }
}