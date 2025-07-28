package uno.lode.ScholarTopicBoard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

	@Mock
	private CourseRepository courseRepository;
	@InjectMocks
	private CourseService courseService;

	@Test
	void shouldThrowCourseAlreadyExistsExceptionWhenNameAlreadyExistsOnUpdate() {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO("course1", "course desc", false);

		when(courseRepository.existsByNameAndIdNot("course1", 2L)).thenReturn(true);

		CourseAlreadyExistsException ex = assertThrows(CourseAlreadyExistsException.class,
				() -> courseService.updateCourse(2L, updateRequestDTO));
		assertEquals("A course with the name 'course1' already exists!", ex.getMessage()); // TODO: exception method to
																							// get field

		verify(courseRepository).existsByNameAndIdNot(eq("course1"), eq(2L));
		verifyNoMoreInteractions(courseRepository);
	}
}
