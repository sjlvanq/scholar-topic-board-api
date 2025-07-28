package uno.lode.ScholarTopicBoard.domain.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TopicUpdateRequestDTO(
	@NotBlank @Size(min = 5, max = 50) String title,
	@NotBlank @Size(min = 10) String body,
	Boolean closed
) {}
