package org.example.baitapthuctap.Service;

import org.example.baitapthuctap.DTO.TaskDTO;
import org.example.baitapthuctap.DTO.TaskRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private TaskService taskService;

    private Users testUser;
    private Tasks testTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new Users();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setMatKhau("password");
        testUser.setNgayTao(LocalDateTime.now());

        testTask = new Tasks();
        testTask.setId(1);
        testTask.setTitle("Test Task");
        testTask.setMoTa("Test Description");
        testTask.setTrangThai(false);
        testTask.setUsers(testUser);
        testTask.setNgayTao(LocalDateTime.now());

        taskRequest = new TaskRequest();
        taskRequest.setTitle("New Task");
        taskRequest.setMoTa("New Description");
        taskRequest.setTrangThai(false);
    }

    @Test
    @DisplayName("Should get current user by username")
    void testGetCurrentUser_WhenUserExists_ShouldReturnUser() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);

        // When
        Users result = taskService.getCurrentUser("testuser");

        // Then
        assertEquals(testUser, result);
        verify(usersRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should get all tasks by user with pagination")
    void testGetAllTasksByUser_WithPagination_ShouldReturnPagedTasks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Tasks> tasks = Arrays.asList(testTask);
        Page<Tasks> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByUsersId(1, pageable)).thenReturn(taskPage);

        // When
        Page<TaskDTO> result = taskService.getAllTasksByUser("testuser", pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
        assertEquals("testuser", result.getContent().get(0).getUsernameOwner());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByUsersId(1, pageable);
    }

    @Test
    @DisplayName("Should get task by id when task belongs to user")
    void testGetTaskById_WhenTaskBelongsToUser_ShouldReturnTask() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByIdAndUsersId(1, 1)).thenReturn(Optional.of(testTask));

        // When
        Optional<TaskDTO> result = taskService.getTaskById(1, "testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Task", result.get().getTitle());
        assertEquals("testuser", result.get().getUsernameOwner());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByIdAndUsersId(1, 1);
    }

    @Test
    @DisplayName("Should return empty when task does not belong to user")
    void testGetTaskById_WhenTaskDoesNotBelongToUser_ShouldReturnEmpty() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByIdAndUsersId(1, 1)).thenReturn(Optional.empty());

        // When
        Optional<TaskDTO> result = taskService.getTaskById(1, "testuser");

        // Then
        assertFalse(result.isPresent());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByIdAndUsersId(1, 1);
    }

    @Test
    @DisplayName("Should create task successfully")
    void testCreateTask_ShouldCreateTaskSuccessfully() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.save(any(Tasks.class))).thenReturn(testTask);

        // When
        TaskDTO result = taskService.createTask(taskRequest, "testuser");

        // Then
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals("testuser", result.getUsernameOwner());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).save(any(Tasks.class));
    }

    @Test
    @DisplayName("Should update task successfully when task belongs to user")
    void testUpdateTask_WhenTaskBelongsToUser_ShouldUpdateSuccessfully() {
        // Given
        taskRequest.setTitle("Updated Task");
        taskRequest.setMoTa("Updated Description");
        taskRequest.setTrangThai(true);

        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByIdAndUsersId(1, 1)).thenReturn(Optional.of(testTask));
        when(tasksRepository.save(any(Tasks.class))).thenReturn(testTask);

        // When
        Optional<TaskDTO> result = taskService.updateTask(1, taskRequest, "testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Updated Task", testTask.getTitle());
        assertEquals("Updated Description", testTask.getMoTa());
        assertTrue(testTask.getTrangThai());
        assertNotNull(testTask.getNgaySua());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByIdAndUsersId(1, 1);
        verify(tasksRepository).save(testTask);
    }

    @Test
    @DisplayName("Should return empty when trying to update task that does not belong to user")
    void testUpdateTask_WhenTaskDoesNotBelongToUser_ShouldReturnEmpty() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByIdAndUsersId(1, 1)).thenReturn(Optional.empty());

        // When
        Optional<TaskDTO> result = taskService.updateTask(1, taskRequest, "testuser");

        // Then
        assertFalse(result.isPresent());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByIdAndUsersId(1, 1);
        verify(tasksRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete task successfully when task belongs to user")
    void testDeleteTask_WhenTaskBelongsToUser_ShouldDeleteSuccessfully() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByIdAndUsersId(1, 1)).thenReturn(Optional.of(testTask));

        // When
        boolean result = taskService.deleteTask(1, "testuser");

        // Then
        assertTrue(result);
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByIdAndUsersId(1, 1);
        verify(tasksRepository).delete(testTask);
    }

    @Test
    @DisplayName("Should return false when trying to delete task that does not belong to user")
    void testDeleteTask_WhenTaskDoesNotBelongToUser_ShouldReturnFalse() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByIdAndUsersId(1, 1)).thenReturn(Optional.empty());

        // When
        boolean result = taskService.deleteTask(1, "testuser");

        // Then
        assertFalse(result);
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByIdAndUsersId(1, 1);
        verify(tasksRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should get tasks by status")
    void testGetTasksByStatus_ShouldReturnTasksWithSpecificStatus() {
        // Given
        List<Tasks> completedTasks = Arrays.asList(testTask);
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByUsersIdAndTrangThai(1, true)).thenReturn(completedTasks);

        // When
        List<TaskDTO> result = taskService.getTasksByStatus(true, "testuser");

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByUsersIdAndTrangThai(1, true);
    }

    @Test
    @DisplayName("Should search tasks by title")
    void testSearchTasks_ShouldReturnMatchingTasks() {
        // Given
        List<Tasks> matchingTasks = Arrays.asList(testTask);
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByUsersIdAndTitleContaining(1, "Test")).thenReturn(matchingTasks);

        // When
        List<TaskDTO> result = taskService.searchTasks("Test", "testuser");

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByUsersIdAndTitleContaining(1, "Test");
    }

    @Test
    @DisplayName("Should get task statistics")
    void testGetTaskStatistics_ShouldReturnCorrectStatistics() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.countCompletedTasksByUser(1)).thenReturn(3L);
        when(tasksRepository.countPendingTasksByUser(1)).thenReturn(2L);

        // When
        TaskService.TaskStatistics result = taskService.getTaskStatistics("testuser");

        // Then
        assertEquals(3L, result.getCompletedTasks());
        assertEquals(2L, result.getPendingTasks());
        assertEquals(5L, result.getTotalTasks());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).countCompletedTasksByUser(1);
        verify(tasksRepository).countPendingTasksByUser(1);
    }

    @Test
    @DisplayName("Should handle null user in getCurrentUser")
    void testGetCurrentUser_WhenUserDoesNotExist_ShouldReturnNull() {
        // Given
        when(usersRepository.findByUsername("nonexistent")).thenReturn(null);

        // When
        Users result = taskService.getCurrentUser("nonexistent");

        // Then
        assertNull(result);
        verify(usersRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should handle empty search results")
    void testSearchTasks_WhenNoMatchingTasks_ShouldReturnEmptyList() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByUsersIdAndTitleContaining(1, "NonExistent")).thenReturn(Collections.emptyList());

        // When
        List<TaskDTO> result = taskService.searchTasks("NonExistent", "testuser");

        // Then
        assertTrue(result.isEmpty());
        verify(usersRepository).findByUsername("testuser");
        verify(tasksRepository).findByUsersIdAndTitleContaining(1, "NonExistent");
    }

    @Test
    @DisplayName("Should handle tasks with null values in DTO conversion")
    void testConvertToDTO_WithNullValues_ShouldHandleGracefully() {
        // Given
        Tasks taskWithNulls = new Tasks();
        taskWithNulls.setId(1);
        taskWithNulls.setTitle("Task with nulls");
        taskWithNulls.setMoTa(null);
        taskWithNulls.setTrangThai(false);
        taskWithNulls.setUsers(testUser);
        taskWithNulls.setNgayTao(LocalDateTime.now());
        taskWithNulls.setNgaySua(null);

        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);
        when(tasksRepository.findByIdAndUsersId(1, 1)).thenReturn(Optional.of(taskWithNulls));

        // When
        Optional<TaskDTO> result = taskService.getTaskById(1, "testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Task with nulls", result.get().getTitle());
        assertNull(result.get().getMoTa());
        assertNull(result.get().getNgaySua());
        assertEquals("testuser", result.get().getUsernameOwner());
    }
}