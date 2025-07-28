package uno.lode.ScholarTopicBoard.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uno.lode.ScholarTopicBoard.domain.course.Course;

public record CourseDetailDTO(
		@Schema(example = "2345")
		Long id,
		@Schema(example = "Course 1")
		String name,
		@Schema(example = "An example course.")
		String description,
		@Schema(example = "false")
		Boolean closed) {
	public CourseDetailDTO(Course course) {
		this(course.getId(), course.getName(), course.getDescription(), course.getClosed());
	}
}
