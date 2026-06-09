package com.codequest.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.leaderboard.dto.LeaderboardResponse;
import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private UserRepository userRepository;

    private LeaderboardService leaderboardService;

    @BeforeEach
    void setUp() {
        leaderboardService = new LeaderboardService(userRepository);
    }

    @Test
    void returnsUsersSortedByXpDescending() {
        UUID currentUserId = UUID.randomUUID();
        User first = createUser(UUID.randomUUID(), "Alpha", 500, UserRank.CODER, 2);
        User second = createUser(currentUserId, "Bravo", 300, UserRank.BEGINNER, 1);
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(first, second)));
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(second));
        when(userRepository.countUsersRankedAhead(300, "Bravo", currentUserId)).thenReturn(1L);

        LeaderboardResponse response = leaderboardService.getLeaderboard(currentUserId, 0, 50, "ALL_TIME");

        assertEquals(2, response.items().size());
        assertEquals(500, response.items().get(0).xp());
        assertEquals(300, response.items().get(1).xp());
        assertEquals(1, response.items().get(0).rankPosition());
        assertEquals(2, response.items().get(1).rankPosition());
    }

    @Test
    void tieBreakerIsDeterministic() {
        UUID currentUserId = UUID.randomUUID();
        User alpha = createUser(UUID.randomUUID(), "Alpha", 500, UserRank.CODER, 2);
        User beta = createUser(currentUserId, "Beta", 500, UserRank.CODER, 3);
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(alpha, beta)));
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(beta));
        when(userRepository.countUsersRankedAhead(500, "Beta", currentUserId)).thenReturn(1L);

        leaderboardService.getLeaderboard(currentUserId, 0, 50, "ALL_TIME");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAllBy(pageableCaptor.capture());
        assertEquals("xp: DESC,name: ASC,id: ASC", pageableCaptor.getValue().getSort().toString());
    }

    @Test
    void rankPositionIsOneBasedAndGlobal() {
        UUID currentUserId = UUID.randomUUID();
        User currentUser = createUser(currentUserId, "Charlie", 200, UserRank.BEGINNER, 0);
        User pageUser = createUser(UUID.randomUUID(), "Delta", 100, UserRank.BEGINNER, 0);
        Page<User> page = new PageImpl<>(List.of(pageUser), org.springframework.data.domain.PageRequest.of(1, 1), 2);
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(page);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.countUsersRankedAhead(200, "Charlie", currentUserId)).thenReturn(0L);

        LeaderboardResponse response = leaderboardService.getLeaderboard(currentUserId, 1, 1, "ALL_TIME");

        assertEquals(2, response.items().get(0).rankPosition());
        assertEquals(1, response.currentUser().rankPosition());
    }

    @Test
    void paginationWorksWithPageAndSize() {
        UUID currentUserId = UUID.randomUUID();
        User currentUser = createUser(currentUserId, "User", 150, UserRank.BEGINNER, 1);
        Page<User> page = new PageImpl<>(
                List.of(createUser(UUID.randomUUID(), "Other", 100, UserRank.BEGINNER, 0)),
                org.springframework.data.domain.PageRequest.of(1, 1),
                2
        );
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(page);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.countUsersRankedAhead(150, "User", currentUserId)).thenReturn(0L);

        LeaderboardResponse response = leaderboardService.getLeaderboard(currentUserId, 1, 1, "ALL_TIME");

        assertEquals(1, response.page());
        assertEquals(1, response.size());
        assertEquals(2, response.totalItems());
        assertEquals(2, response.totalPages());
    }

    @Test
    void currentUserIsIncludedEvenWhenNotOnRequestedPage() {
        UUID currentUserId = UUID.randomUUID();
        User currentUser = createUser(currentUserId, "Current", 50, UserRank.BEGINNER, 1);
        User topUser = createUser(UUID.randomUUID(), "Top", 500, UserRank.LEGEND, 9);
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(topUser)));
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.countUsersRankedAhead(50, "Current", currentUserId)).thenReturn(9L);

        LeaderboardResponse response = leaderboardService.getLeaderboard(currentUserId, 0, 50, "ALL_TIME");

        assertEquals(currentUserId, response.currentUser().userId());
        assertEquals(10, response.currentUser().rankPosition());
        assertFalse(response.items().stream().anyMatch(item -> item.userId().equals(currentUserId)));
    }

    @Test
    void currentUserRankPositionIsCorrect() {
        UUID currentUserId = UUID.randomUUID();
        User currentUser = createUser(currentUserId, "Current", 300, UserRank.CODER, 4);
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(currentUser)));
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.countUsersRankedAhead(300, "Current", currentUserId)).thenReturn(4L);

        LeaderboardResponse response = leaderboardService.getLeaderboard(currentUserId, 0, 50, "ALL_TIME");

        assertEquals(5, response.currentUser().rankPosition());
        verify(userRepository).countUsersRankedAhead(300, "Current", currentUserId);
    }

    @Test
    void emptyOrLowDataLeaderboardWorksSafely() {
        UUID currentUserId = UUID.randomUUID();
        User currentUser = createUser(currentUserId, "Solo", 30, UserRank.BEGINNER, 1);
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(1, 50), 0)
        );
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.countUsersRankedAhead(30, "Solo", currentUserId)).thenReturn(0L);

        LeaderboardResponse response = leaderboardService.getLeaderboard(currentUserId, 1, 50, "ALL_TIME");

        assertTrue(response.items().isEmpty());
        assertEquals(0, response.totalItems());
        assertEquals(0, response.totalPages());
        assertEquals(1, response.currentUser().rankPosition());
    }

    @Test
    void invalidPageReturnsBadRequest() {
        ApiException exception = assertThrows(ApiException.class,
                () -> leaderboardService.getLeaderboard(UUID.randomUUID(), -1, 50, "ALL_TIME"));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Page must be greater than or equal to 0.", exception.getMessage());
        verify(userRepository, never()).findAllBy(any(Pageable.class));
    }

    @Test
    void invalidSizeZeroReturnsBadRequest() {
        ApiException exception = assertThrows(ApiException.class,
                () -> leaderboardService.getLeaderboard(UUID.randomUUID(), 0, 0, "ALL_TIME"));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Size must be at least 1.", exception.getMessage());
        verify(userRepository, never()).findAllBy(any(Pageable.class));
    }

    @Test
    void invalidSizeFiftyOneReturnsBadRequest() {
        ApiException exception = assertThrows(ApiException.class,
                () -> leaderboardService.getLeaderboard(UUID.randomUUID(), 0, 51, "ALL_TIME"));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Size must be less than or equal to 50.", exception.getMessage());
        verify(userRepository, never()).findAllBy(any(Pageable.class));
    }

    @Test
    void invalidPeriodReturnsBadRequest() {
        ApiException exception = assertThrows(ApiException.class,
                () -> leaderboardService.getLeaderboard(UUID.randomUUID(), 0, 50, "WEEKLY"));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Period must be ALL_TIME.", exception.getMessage());
        verify(userRepository, never()).findAllBy(any(Pageable.class));
    }

    @Test
    void responseMappingExcludesSensitiveFields() {
        UUID currentUserId = UUID.randomUUID();
        User currentUser = createUser(currentUserId, "Current", 300, UserRank.CODER, 4);
        when(userRepository.findAllBy(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(currentUser)));
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.countUsersRankedAhead(300, "Current", currentUserId)).thenReturn(0L);

        LeaderboardResponse response = leaderboardService.getLeaderboard(currentUserId, 0, 50, "ALL_TIME");

        assertEquals("Current", response.items().get(0).name());
        assertEquals(300, response.items().get(0).xp());
        assertEquals(UserRank.CODER, response.items().get(0).rank());
        assertEquals(4, response.items().get(0).streak());
        assertFalse(response.toString().contains("password"));
        assertFalse(response.toString().contains("email"));
        assertFalse(response.toString().contains("role"));
        assertFalse(response.toString().contains("token"));
    }

    private User createUser(UUID id, String name, int xp, UserRank rank, int streak) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(name.toLowerCase() + "@example.com");
        user.setPasswordHash("hash");
        user.setXp(xp);
        user.setRank(rank);
        user.setStreak(streak);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }
}
