package uno.lode.ScholarTopicBoard.domain.topic.dto;

import java.time.LocalDateTime;

import uno.lode.ScholarTopicBoard.domain.course.dto.CourseSummaryDTO;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;

public record TopicWithCourseDTO(
		Long id,
		CourseSummaryDTO course,
		String title,
		LocalDateTime creationDate,
		LocalDateTime updateDate,
		Boolean closed) {
	public TopicWithCourseDTO(Topic topic) {
		this(
			topic.getId(),
			new CourseSummaryDTO(topic.getCourse()),
			topic.getTitle(),
			topic.getCreationDate(),
			topic.getUpdateDate(),
			topic.isClosed()
		);
	}
}
