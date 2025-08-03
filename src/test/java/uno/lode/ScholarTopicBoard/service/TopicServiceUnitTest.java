package uno.lode.ScholarTopicBoard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.topic.TopicRepository;
import uno.lode.ScholarTopicBoard.domain.topic.dto.*;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.*;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@ExtendWith(MockitoExtension.class)
class TopicServiceUnitTest {

	@Mock
	private TopicRepository topicRepository;
	@Mock
	private CourseRepository courseRepository;
	@Mock
	private UserAuthorizationService userAuthorizationService;

	@InjectMocks
	private TopicService topicService;

	// Helper methods
	private AuthUser createAuthUser(Long id, String firstName, String lastName, String email) {
		AuthUser authUser = mock(AuthUser.class);
		User user = createUser(id, firstName, lastName, email);
		when(authUser.getUser()).thenReturn(user);
		return authUser;
	}
	
	private AuthUser createAuthUserWithId(Long id) {
		AuthUser authUser = mock(AuthUser.class);
		when(authUser.getId()).thenReturn(id);
		return authUser;
	}

	private User createUser(Long id, String firstName, String lastName, String email) {
		User user = mock(User.class);
	    lenient().when(user.getId()).thenReturn(id);
		when(user.getFirstName()).thenReturn(firstName);
		when(user.getLastName()).thenReturn(lastName);
		when(user.getEmail()).thenReturn(email);
		return user;
	}

	private Course createCourse(Long id, String name) {
		Course course = mock(Course.class);
		when(course.getId()).thenReturn(id);
		lenient().when(course.getName()).thenReturn(name);
		return course;
	}
	
	private Course createCourse(Long id) {
	    return createCourse(id, "Default Course Name");
	}
		
	// Tests
	@Nested
	@DisplayName("createTopic tests")
	class CreateTopicTests {

		@Test
		@DisplayName("Should create topic successfully when title is unique")
		void shouldCreateTopicSuccessfully() {
			// Given
			Long userId = 1L;
			Long courseId = 101L;
			AuthUser authUser = createAuthUser(userId, "John", "Doe", "user@123.com");
			Course course = createCourse(courseId);
			when(course.getName()).thenReturn("Sample course");
			
			TopicRegisterRequestDTO registerDto = new TopicRegisterRequestDTO("New Topic", "Description");
			Topic savedTopic = new Topic(registerDto, authUser.getUser(), course);

			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			doNothing().when(userAuthorizationService).ensureHasCourseAccess(authUser, course);
			when(topicRepository.existsByTitleAndCourseId(registerDto.title(), courseId)).thenReturn(false);
			when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);

			// When
			TopicDetailDTO result = topicService.createTopic(authUser, courseId, registerDto);

			// Then
			assertNotNull(result);
			assertEquals("New Topic", result.title());
			assertEquals("Description", result.body());
			assertEquals(courseId, result.course().id());
			assertEquals("Sample course", result.course().name());
			
			verify(userAuthorizationService).ensureHasCourseAccess(authUser, course);
			verify(topicRepository).existsByTitleAndCourseId(registerDto.title(), courseId);
			verify(topicRepository).save(any(Topic.class));
		}

		@Test
		@DisplayName("Should throw TopicAlreadyExistsException when title already exists")
		void shouldThrowWhenTitleExists() {
			// Given
			Long courseId = 102L;
			Course course = mock(Course.class);
			AuthUser authUser = mock(AuthUser.class);
			
			TopicRegisterRequestDTO registerDto = new TopicRegisterRequestDTO("New Topic", "Description");

			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			doNothing().when(userAuthorizationService).ensureHasCourseAccess(authUser, course);
			when(topicRepository.existsByTitleAndCourseId(registerDto.title(), courseId)).thenReturn(true);

			// When + Then
			assertThrows(TopicAlreadyExistsException.class,
					() -> topicService.createTopic(authUser, courseId, registerDto));
		}

		@Test
		@DisplayName("Should throw CourseNotFoundException if course does not exist")
		void shouldThrowWhenCourseNotFound() {
			// Given
			Long courseId = 103L;
			when(courseRepository.findById(courseId)).thenReturn(Optional.empty());
			// When + Then
			assertThrows(CourseNotFoundException.class,
					() -> topicService.createTopic(mock(AuthUser.class), courseId, new TopicRegisterRequestDTO(null, null)));
		}
	}

	@Nested
	@DisplayName("updateTopic tests")
	class UpdateTopicTests {

		@Test
		@DisplayName("Should update topic successfully when data is valid")
		void shouldUpdateTopicSuccessfully() {
			// Given
			Long userId = 2L;
			Long courseId = 104L;
			Long topicId = 201L;
			AuthUser authUser = createAuthUser(userId, "John", "Doe", "user@123.com");
			TopicUpdateRequestDTO updateDto = new TopicUpdateRequestDTO("Updated Topic", "Description", false);
			
			Course course = createCourse(courseId);
			when(course.getName()).thenReturn("Sample Course");
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			
			Topic topic = new Topic(
					new TopicRegisterRequestDTO("Old Title", "Old Body"),
					authUser.getUser(),
					course
				);

			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
			
			doNothing().when(userAuthorizationService).ensureCanAccessAuthorable(authUser, topic);
			when(topicRepository.existsByTitleIgnoreCaseAndCourseIdAndIdNot(updateDto.title(), courseId, topicId)).thenReturn(false);
			
			// When
			TopicDetailDTO result = topicService.updateTopic(authUser, courseId, topicId, updateDto);

			//Then
			assertNotNull(result);
			assertEquals(updateDto.title(), result.title());
			assertEquals(updateDto.body(), result.body());
			assertEquals(courseId, result.course().id());
			assertEquals(course.getName(), result.course().name());
			
			verify(userAuthorizationService).ensureHasCourseAccess(authUser, course);
			verify(userAuthorizationService).ensureCanAccessAuthorable(authUser, topic);
		}

		@Test
		@DisplayName("Should throw TopicAlreadyExistsException if title is duplicated")
		void shouldThrowWhenDuplicateTitle() {
			// Given
			Long courseId = 105L;
			Long topicId = 202L;
			AuthUser authUser = mock(AuthUser.class);
			TopicUpdateRequestDTO updateDto = new TopicUpdateRequestDTO("Updated Topic", "Description", false);
			
			Course course = createCourse(courseId); //mock(Course.class);
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			
			Topic topic = mock(Topic.class);
			when(topic.getCourse()).thenReturn(course);
			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
			
			doNothing().when(userAuthorizationService).ensureCanAccessAuthorable(authUser, topic);
			when(topicRepository.existsByTitleIgnoreCaseAndCourseIdAndIdNot(updateDto.title(), courseId, topicId)).thenReturn(true);

			// When + Then
			assertThrows(TopicAlreadyExistsException.class,
					() -> topicService.updateTopic(authUser, courseId, topicId, updateDto));
			
		}

		@Test
		@DisplayName("Should throw TopicNotFoundException if topic doesn't exist")
		void shouldThrowWhenTopicNotFound() {
			// Given
			Long courseId = 106L;
			Long topicId = 203L;
			AuthUser authUser = mock(AuthUser.class);
			TopicUpdateRequestDTO updateDto = new TopicUpdateRequestDTO("Updated Topic", "Description", false);
			
			Course course = mock(Course.class);
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));			
			when(topicRepository.findById(topicId)).thenReturn(Optional.empty());
			
			// When + Then
			assertThrows(TopicNotFoundException.class,
					() -> topicService.updateTopic(authUser, courseId, topicId, updateDto));
		}

		@Test
		@DisplayName("Should throw CourseNotFoundException if course doesn't exist")
		void shouldThrowWhenCourseNotFound() {
			// Given
			Long courseId = 107L;
			AuthUser authUser = mock(AuthUser.class);
			when(courseRepository.findById(courseId)).thenReturn(Optional.empty());
			
			// When + Then
			assertThrows(CourseNotFoundException.class,
					() -> topicService.updateTopic(authUser, courseId, null, null));
		}

		@Test
		@DisplayName("Should throw TopicDoesNotBelongToCourseException if topic is not from course")
		void shouldThrowWhenTopicNotInCourse() {
			// Given
			Long inParamCourseId = 108L;
			Long realCourseId = 109L;
			
			Long topicId = 204L;
			
			AuthUser authUser = mock(AuthUser.class);
			
			Course byParamCourse = mock(Course.class);
			Course realCourse = mock(Course.class);
			Topic topic = mock(Topic.class);
			
			when(courseRepository.findById(inParamCourseId)).thenReturn(Optional.of(byParamCourse));

			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
			when(topic.getCourse()).thenReturn(realCourse);
			when(realCourse.getId()).thenReturn(realCourseId);
			
			// When + Then
			assertThrows(TopicDoesNotBelongToCourseException.class,
					() -> topicService.updateTopic(authUser, inParamCourseId, topicId, null));
		}
	}

	@Nested
	@DisplayName("listByCourse tests")
	class ListByCourseTests {

		@Test
		@DisplayName("Should return list of topics with authors when user has access")
		void shouldReturnListOfTopicsWithAuthors() {
			// Given
			Long userId = 3L;
			Long courseId = 110L;
			
			AuthUser authUser = createAuthUser(userId, "John", "Doe", "user@123.com");
			Course course = mock(Course.class);
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

			doNothing().when(userAuthorizationService).ensureHasCourseAccess(authUser, course);

			Topic topic1 = mock(Topic.class);
			Topic topic2 = mock(Topic.class);
			
			User user = authUser.getUser();
			when(topic1.getAuthor()).thenReturn(user);
			when(topic2.getAuthor()).thenReturn(user);
			
			when(topic1.getCreationDate()).thenReturn(LocalDateTime.now());
			when(topic1.getUpdateDate()).thenReturn(LocalDateTime.now());
			when(topic2.getCreationDate()).thenReturn(LocalDateTime.now());
			when(topic2.getUpdateDate()).thenReturn(LocalDateTime.now());
			
			List<Topic> topics = List.of(topic1, topic2);
			
			when(topicRepository.findByCourseWithAuthor(course)).thenReturn(topics);

			// When
			List<TopicWithAuthorDTO> result = topicService.listByCourse(authUser, courseId);

			// Then
			assertThat(result).hasSize(2);
			assertThat(result.get(0)).isInstanceOf(TopicWithAuthorDTO.class);
			assertThat(result.get(1)).isInstanceOf(TopicWithAuthorDTO.class);
			verify(userAuthorizationService).ensureHasCourseAccess(authUser, course);
			verify(topicRepository).findByCourseWithAuthor(course);
		}

		@Test
		@DisplayName("Should throw CourseNotFoundException when course not found")
		void shouldThrowWhenCourseNotFound() {
			// Given
			Long courseId = 111L;
			AuthUser authUser = mock(AuthUser.class);
			when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

			// When + Then
			assertThrows(CourseNotFoundException.class,
					() -> topicService.listByCourse(authUser, courseId));
		}
	}

	@Nested
	@DisplayName("getTopic tests") 
	class GetTopicTests {

		@Test
		@DisplayName("Should return topic with author when valid")
		void shouldReturnTopicWithAuthor() {
			// Given
			Long userId = 4L;
			Long courseId = 112L;
			Long topicId = 205L;
			
			AuthUser authUser = createAuthUser(userId, "John", "Doe", "user@123.com");
			Course course = createCourse(courseId); //mock(Course.class);
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			
			Topic topic = new Topic(
					topicId,
					authUser.getUser(),
					course,
					"Sample Topic",
					LocalDateTime.now(),
					LocalDateTime.now(),
					"Body",
					false
				);
			
			when(topicRepository.findByIdWithAuthor(topicId)).thenReturn(Optional.of(topic));
			
			// When
			TopicWithAuthorDTO result = topicService.getTopic(authUser, courseId, topicId);
			
			// Then
			assertNotNull(result);
			assertEquals(topicId, result.id());
			assertEquals("John", result.author().firstName());
			assertEquals("Doe", result.author().lastName());
			assertEquals("user@123.com", result.author().email());
			assertEquals("Sample Topic", result.title());
			verify(userAuthorizationService).ensureHasCourseAccess(eq(authUser), eq(course));
		}

		@Test
		@DisplayName("Should throw TopicNotFoundException if topic doesn't exist")
		void shouldThrowWhenTopicNotFound() {
			// Given
			Long courseId = 113L;
			Long topicId = 206L;
			
			AuthUser authUser = mock(AuthUser.class);
			Course course = mock(Course.class);
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			
			when(topicRepository.findByIdWithAuthor(topicId)).thenReturn(Optional.empty());
			
			// When + Then
			assertThrows(TopicNotFoundException.class,
					() -> topicService.getTopic(authUser, courseId, topicId));

			verify(userAuthorizationService).ensureHasCourseAccess(eq(authUser), eq(course));
		}

		@Test
		@DisplayName("Should throw CourseNotFoundException if course doesn't exist")
		void shouldThrowWhenCourseNotFound() {
			// Given
			Long courseId = 114L;
			Long topicId = 207L;
			
			when(courseRepository.findById(courseId)).thenReturn(Optional.empty());
			
			// When + Then
			assertThrows(CourseNotFoundException.class,
					() -> topicService.getTopic(mock(AuthUser.class), courseId, topicId));
		}

		@Test
		@DisplayName("Should throw TopicDoesNotBelongToCourseException if topic not in course")
		void shouldThrowWhenTopicNotInCourse() {
			// Given
			Long inParamCourseId = 115L;
			Long realCourseId = 116L;
			Long topicId = 208L;
			
			AuthUser authUser = mock(AuthUser.class);
			
			Course byParamCourse = mock(Course.class);
			Course realCourse = mock(Course.class);
			Topic topic = mock(Topic.class);
			
			when(courseRepository.findById(inParamCourseId)).thenReturn(Optional.of(byParamCourse));
			
			when(topicRepository.findByIdWithAuthor(topicId)).thenReturn(Optional.of(topic));
			when(topic.getCourse()).thenReturn(realCourse);
			when(realCourse.getId()).thenReturn(realCourseId);
			
			// When + Then
			assertThrows(TopicDoesNotBelongToCourseException.class,
					() -> topicService.getTopic(authUser, inParamCourseId, topicId));
		}
	}

	@Nested
	@DisplayName("listByLoggedUser tests")
	class ListByLoggedUserTests {

		@Test
		@DisplayName("Should return list of topics authored by logged user")
		void shouldReturnTopicsOfLoggedUser() {
			// Given
			Long userId = 5L;
			AuthUser authUser = createAuthUserWithId(userId);

			Topic topic1 = mock(Topic.class);
			Topic topic2 = mock(Topic.class);
			when(topic1.getCourse()).thenReturn(mock(Course.class));
			when(topic2.getCourse()).thenReturn(mock(Course.class));
			when(topic1.getCreationDate()).thenReturn(LocalDateTime.now());
			when(topic1.getUpdateDate()).thenReturn(LocalDateTime.now());
			when(topic2.getCreationDate()).thenReturn(LocalDateTime.now());
			when(topic2.getUpdateDate()).thenReturn(LocalDateTime.now());

			List<Topic> topics = List.of(topic1, topic2);

			when(topicRepository.findByAuthorIdWithCourse(userId)).thenReturn(topics);

			// When
			List<TopicWithCourseDTO> result = topicService.listByLoggedUser(authUser);

			// Then
			assertThat(result).hasSize(2);
			assertThat(result.get(0)).isInstanceOf(TopicWithCourseDTO.class);
			assertThat(result.get(1)).isInstanceOf(TopicWithCourseDTO.class);
			verify(topicRepository).findByAuthorIdWithCourse(userId);
		}

		@Test
		@DisplayName("Should return empty list when user has no topics")
		void shouldReturnEmptyListIfNoTopics() {
			// Given
			Long userId = 6L;
			AuthUser authUser = createAuthUserWithId(userId);

			when(topicRepository.findByAuthorIdWithCourse(userId)).thenReturn(List.of());

			// When
			List<TopicWithCourseDTO> result = topicService.listByLoggedUser(authUser);

			// Then
			assertThat(result).hasSize(0);
			assertThat(result).isInstanceOf(List.class);
			verify(topicRepository).findByAuthorIdWithCourse(userId);
		}
	}

	@Nested
	@DisplayName("deleteTopic tests")
	class DeleteTopicTests {

		@Test
		@DisplayName("Should delete topic when user is authorized")
		void shouldDeleteTopicSuccessfully() {
			// Given
			Long courseId = 117L;
			Long topicId = 209L;
			AuthUser user = mock(AuthUser.class);
			
			Course course = createCourse(courseId);
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			
			Topic topic = mock(Topic.class);
			when(topic.getCourse()).thenReturn(course);

			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
			doNothing().when(userAuthorizationService).ensureCanAccessAuthorable(user, topic);
			
			// When
			topicService.deleteTopic(user, courseId, topicId);
			
			// Then
			verify(userAuthorizationService).ensureCanAccessAuthorable(user, topic);
			verify(topicRepository).delete(topic);
		}

		@Test
		@DisplayName("Should throw TopicNotFoundException when topic doesn't exist")
		void shouldThrowWhenTopicNotFound() {
			// Given
			Long courseId = 118L;
			Long topicId = 210L;
			AuthUser authUser = mock(AuthUser.class);
			
			Course course = mock(Course.class);
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			
			when(topicRepository.findById(topicId)).thenReturn(Optional.empty());
			
			// When + Then
			assertThrows(TopicNotFoundException.class,
					() -> topicService.deleteTopic(authUser, courseId, topicId));
		}

		@Test
		@DisplayName("Should throw CourseNotFoundException when course doesn't exist")
		void shouldThrowWhenCourseNotFound() {
			// Given
			Long courseId = 119L;
			Long topicId = 211L;
			AuthUser authUser = mock(AuthUser.class);
			
			when(courseRepository.findById(courseId)).thenReturn(Optional.empty());
			
			// When + Then
			assertThrows(CourseNotFoundException.class,
					() -> topicService.deleteTopic(authUser, courseId, topicId));
		}

		@Test
		@DisplayName("Should throw TopicDoesNotBelongToCourseException if topic not in course")
		void shouldThrowWhenTopicNotInCourse() {
			// Given
			Long inParamCourseId = 120L;
			Long realCourseId = 121L;
			
			Long topicId = 212L;
			
			AuthUser authUser = mock(AuthUser.class);
			
			Course byParamCourse = mock(Course.class);
			Course realCourse = mock(Course.class);
			Topic topic = mock(Topic.class);
			
			when(courseRepository.findById(inParamCourseId)).thenReturn(Optional.of(byParamCourse));
			
			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
			when(topic.getCourse()).thenReturn(realCourse);
			when(realCourse.getId()).thenReturn(realCourseId);
			
			// When + Then
			assertThrows(TopicDoesNotBelongToCourseException.class,
					() -> topicService.deleteTopic(authUser, inParamCourseId, topicId));
		}
	}
}
