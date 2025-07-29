package uno.lode.ScholarTopicBoard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

	@Mock
	private CourseRepository courseRepository;
	@InjectMocks
	private CourseService courseService;

	@Test
	void shouldThrowCourseAlreadyExistsExceptionWhenNameAlreadyExistsOnUpdate() {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO("course1", "course desc", false);

		when(courseRepository.findById(2L)).thenReturn(Optional.of(mock(Course.class)));
		when(courseRepository.existsByNameAndIdNot("course1", 2L)).thenReturn(true);

		CourseAlreadyExistsException ex = assertThrows(CourseAlreadyExistsException.class,
				() -> courseService.updateCourse(2L, updateRequestDTO));
		assertEquals("A course with the name 'course1' already exists!", ex.getMessage()); // TODO: exception method to
																							// get field
		verify(courseRepository).existsByNameAndIdNot(eq("course1"), eq(2L));
		verifyNoMoreInteractions(courseRepository);
	}

	@Test
	void shouldThrowExceptionWhenCourseNotFoundOnUpdate() {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO("course1", "course desc", false);

		CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
				() -> courseService.updateCourse(3L, updateRequestDTO));
		assertEquals("Course with id 3 not found!", ex.getMessage());

		verify(courseRepository, never()).existsByNameAndIdNot(any(), any());
	}
}
