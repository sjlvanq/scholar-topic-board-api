package uno.lode.ScholarTopicBoard.domain.user.dto;
import java.util.Set;

public record UserCoursesUpdateRequestDTO(
	Set<Long> courseIds) {
}
