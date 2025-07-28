package uno.lode.ScholarTopicBoard.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserBanStatusUpdateRequestDTO(@NotNull Boolean status) {}
