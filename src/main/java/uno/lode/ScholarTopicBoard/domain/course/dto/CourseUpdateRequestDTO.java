package uno.lode.ScholarTopicBoard.domain.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseUpdateRequestDTO(
	@NotBlank @Size(min = 5, max = 100) String name,
	@Size(max = 500) String description,
	Boolean closed) {}
