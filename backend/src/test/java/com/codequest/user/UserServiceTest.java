package com.codequest.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.user.dto.UserProfileResponse;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getCurrentUserProfile_shouldReturnProfile_whenUserExists() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        UserProfileResponse expectedResponse = mock(UserProfileResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserProfileResponse(user)).thenReturn(expectedResponse);

        // When
        UserProfileResponse result = userService.getCurrentUserProfile(userId);

        // Then
        assertEquals(expectedResponse, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toUserProfileResponse(user);
    }

    @Test
    void getCurrentUserProfile_shouldThrowApiException_whenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> userService.getCurrentUserProfile(userId));
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("User not found.", exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }
}