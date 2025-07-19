package org.example.baitapthuctap.Service;

import org.example.baitapthuctap.Entity.Users;
import org.example.baitapthuctap.Repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setMatKhau("encodedPassword");
        testUser.setNgayTao(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void testLoadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().isEmpty());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
        verify(usersRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        when(usersRepository.findByUsername("nonexistent")).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nonexistent")
        );

        assertEquals("User not found", exception.getMessage());
        verify(usersRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should handle empty username")
    void testLoadUserByUsername_WithEmptyUsername_ShouldThrowException() {
        // Given
        when(usersRepository.findByUsername("")).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("")
        );

        assertEquals("User not found", exception.getMessage());
        verify(usersRepository).findByUsername("");
    }

    @Test
    @DisplayName("Should handle null username")
    void testLoadUserByUsername_WithNullUsername_ShouldThrowException() {
        // Given
        when(usersRepository.findByUsername(null)).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(null)
        );

        assertEquals("User not found", exception.getMessage());
        verify(usersRepository).findByUsername(null);
    }

    @Test
    @DisplayName("Should handle user with null password")
    void testLoadUserByUsername_WithNullPassword_ShouldReturnUserDetailsWithNullPassword() {
        // Given
        testUser.setMatKhau(null);
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword());
        verify(usersRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should handle user with empty password")
    void testLoadUserByUsername_WithEmptyPassword_ShouldReturnUserDetailsWithEmptyPassword() {
        // Given
        testUser.setMatKhau("");
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("", result.getPassword());
        verify(usersRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should create UserDetails with empty authorities list")
    void testLoadUserByUsername_ShouldCreateUserDetailsWithEmptyAuthorities() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertNotNull(result.getAuthorities());
        assertTrue(result.getAuthorities().isEmpty());
        verify(usersRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should return UserDetails with correct account status")
    void testLoadUserByUsername_ShouldReturnUserDetailsWithCorrectAccountStatus() {
        // Given
        when(usersRepository.findByUsername("testuser")).thenReturn(testUser);

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        verify(usersRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testLoadUserByUsername_WithSpecialCharacters_ShouldWork() {
        // Given
        String specialUsername = "test@user.com";
        testUser.setUsername(specialUsername);
        when(usersRepository.findByUsername(specialUsername)).thenReturn(testUser);

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername(specialUsername);

        // Then
        assertNotNull(result);
        assertEquals(specialUsername, result.getUsername());
        verify(usersRepository).findByUsername(specialUsername);
    }

    @Test
    @DisplayName("Should handle case sensitive username")
    void testLoadUserByUsername_CaseSensitive_ShouldWork() {
        // Given
        String upperCaseUsername = "TESTUSER";
        when(usersRepository.findByUsername(upperCaseUsername)).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(upperCaseUsername)
        );

        assertEquals("User not found", exception.getMessage());
        verify(usersRepository).findByUsername(upperCaseUsername);
    }
}