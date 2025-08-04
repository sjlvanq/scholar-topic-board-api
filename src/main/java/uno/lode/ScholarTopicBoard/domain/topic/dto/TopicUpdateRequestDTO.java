package uno.lode.ScholarTopicBoard.domain.topic.dto;

import jakarta.validation.constraints.Size;

public record TopicUpdateRequestDTO(
	@Size(min = 5, max = 50) String title,
	@Size(min = 10) String body,
	Boolean closed
) {}
