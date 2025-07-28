package uno.lode.ScholarTopicBoard.domain.topic.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserAuthorDTO;

public record TopicDetailDTO(
		Long id,
		UserAuthorDTO author,
		CourseDetailDTO course,
		@Schema(example = "An example topic") // TODO: use constants
		String title,
		@Schema(example = "2025-07-05 20:10:03")
		LocalDateTime creationDate,
		@Schema(example = "2025-07-05 22:00:23")
		LocalDateTime updateDate,
		String body,
		@Schema(example = "false")
		Boolean closed) {
	public TopicDetailDTO(Topic topic) {
		this(
			topic.getId(),
			new UserAuthorDTO(topic.getAuthor()),
			new CourseDetailDTO(topic.getCourse()),
			topic.getTitle(),
			topic.getCreationDate(),
			topic.getUpdateDate(),
			topic.getBody(),
			topic.isClosed()
		);
	}
}
