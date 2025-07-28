package uno.lode.ScholarTopicBoard.domain.course.dto;

import uno.lode.ScholarTopicBoard.domain.course.Course;

public record CourseSummaryDTO(
		Long id,
		String name,
		Boolean closed) {
	public CourseSummaryDTO(Course course) {
		this(course.getId(), course.getName(), course.getClosed());
	}
}
