package com.codequest.leaderboard;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.leaderboard.dto.CurrentUserLeaderboardResponse;
import com.codequest.leaderboard.dto.LeaderboardItemResponse;
import com.codequest.leaderboard.dto.LeaderboardResponse;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class LeaderboardService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 50;
    private static final int MAX_SIZE = 50;
    private static final String PERIOD_ALL_TIME = "ALL_TIME";

    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public LeaderboardResponse getLeaderboard(UUID currentUserId, int page, int size, String period) {
        validatePage(page);
        validateSize(size);
        validatePeriod(period);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("xp"),
                        Sort.Order.asc("name"),
                        Sort.Order.asc("id")
                )
        );

        Page<User> userPage = userRepository.findAllBy(pageable);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "User not found."));

        long currentUserRankPosition = userRepository.countUsersRankedAhead(
                currentUser.getXp(),
                currentUser.getName(),
                currentUser.getId()
        ) + 1;

        long startRankPosition = ((long) page * size) + 1;
        List<LeaderboardItemResponse> items = java.util.stream.IntStream.range(0, userPage.getContent().size())
                .mapToObj(index -> toLeaderboardItemResponse(userPage.getContent().get(index), startRankPosition + index))
                .toList();

        return new LeaderboardResponse(
                page,
                size,
                PERIOD_ALL_TIME,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                items,
                new CurrentUserLeaderboardResponse(
                        currentUserRankPosition,
                        currentUser.getId(),
                        currentUser.getXp(),
                        currentUser.getRank()
                )
        );
    }

    private void validatePage(int page) {
        if (page < DEFAULT_PAGE) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Page must be greater than or equal to 0.");
        }
    }

    private void validateSize(int size) {
        if (size < 1) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Size must be at least 1.");
        }
        if (size > MAX_SIZE) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Size must be less than or equal to 50.");
        }
    }

    private void validatePeriod(String period) {
        String normalizedPeriod = period == null ? PERIOD_ALL_TIME : period.trim();
        if (!PERIOD_ALL_TIME.equals(normalizedPeriod)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Period must be ALL_TIME.");
        }
    }

    private LeaderboardItemResponse toLeaderboardItemResponse(User user, long rankPosition) {
        return new LeaderboardItemResponse(
                rankPosition,
                user.getId(),
                user.getName(),
                user.getXp(),
                user.getRank(),
                user.getStreak()
        );
    }
}
