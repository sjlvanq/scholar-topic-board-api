package uno.lode.ScholarTopicBoard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.topic.TopicRepository;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithAuthorDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithCourseDTO;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicDoesNotBelongToCourseException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.util.ServiceUtil;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private CourseRepository courseRepository;
	@Mock
	private TopicRepository topicRepository;
	@Mock
	private ServiceUtil serviceUtil;

	@InjectMocks
	private TopicService topicService;
	
	private final Long TOPIC_ID = 1L;
	private final String TOPIC_TITLE = "Sample topic";
	private final String TOPIC_BODY = "This is a sample topic";
	private final String UPDATED_TOPIC_BODY = "This is the updated reply body.";;
	private final Boolean TOPIC_CLOSED = false;
	private final Long COURSE_ID = 1L;
	
	@Test
	void shouldDeleteTopicSuccessfullyWhenUserIsAuthorized() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(1L);

	    doNothing().when(serviceUtil).checkAdminModeratorOrAuthor(mockAuthUser, mockTopic);

	    topicService.deleteTopic(mockAuthUser, COURSE_ID, TOPIC_ID);
	    
	    verify(topicRepository).delete(mockTopic);
	    verify(serviceUtil).checkAdminModeratorOrAuthor(mockAuthUser, mockTopic);
	}
	
	@Test
	void shouldReturnListOfTopicsWithCourseDtoWhenListingTopicsByLoggedUser() {
	    AuthUser mockAuthUser = mock(AuthUser.class);
	    Topic mockTopic1 = mock(Topic.class);
	    Topic mockTopic2 = mock(Topic.class);
	    Course mockCourse = mock(Course.class);

	    Long userId = 5L;
	    when(mockAuthUser.getId()).thenReturn(userId);

	    when(topicRepository.findByAuthorIdWithCourse(userId)).thenReturn(java.util.List.of(mockTopic1, mockTopic2));

	    when(mockTopic1.getId()).thenReturn(1L);
	    when(mockTopic2.getId()).thenReturn(2L);
		when(mockTopic1.getCourse()).thenReturn(mockCourse);
		when(mockTopic2.getCourse()).thenReturn(mockCourse);
		
	    var result = topicService.listByLoggedUser(mockAuthUser);
	    
	    assertNotNull(result);
	    assertEquals(2, result.size());
	    assertTrue(result.get(0) instanceof TopicWithCourseDTO);
	    assertTrue(result.get(1) instanceof TopicWithCourseDTO);
	}

	@Test
	void shouldReturnListOfTopicWithAuthorDtoWhenListingTopicsByCourse() {
	    AuthUser mockAuthUser = mock(AuthUser.class);
	    Course mockCourse = mock(Course.class);
	    Topic mockTopic1 = mock(Topic.class);
	    Topic mockTopic2 = mock(Topic.class);
	    User mockUser = mock(User.class);
	    
	    when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));

	    when(topicRepository.findByCourseWithAuthor(mockCourse)).thenReturn(List.of(mockTopic1, mockTopic2));

	    when(mockTopic1.getId()).thenReturn(1L);
	    when(mockTopic2.getId()).thenReturn(2L);
		when(mockTopic1.getAuthor()).thenReturn(mockUser);
		when(mockTopic2.getAuthor()).thenReturn(mockUser);
		when(mockTopic1.getCreationDate()).thenReturn(LocalDateTime.now());
		when(mockTopic2.getCreationDate()).thenReturn(LocalDateTime.now());
		when(mockTopic1.getUpdateDate()).thenReturn(LocalDateTime.now());
		when(mockTopic2.getUpdateDate()).thenReturn(LocalDateTime.now());

	    var result = topicService.listByCourse(mockAuthUser, COURSE_ID);

	    assertNotNull(result);
	    assertEquals(2, result.size());
	    assertTrue(result.get(0) instanceof TopicWithAuthorDTO);
	    assertTrue(result.get(1) instanceof TopicWithAuthorDTO);
	}

	@Test
	void shouldReturnTopicDetailDtoWhenTopicIsSuccessfullyCreated() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);
		User mockUser = mock(User.class);
		
		TopicRegisterRequestDTO registerRequest = new TopicRegisterRequestDTO(TOPIC_TITLE, TOPIC_BODY);
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.existsByTitleAndCourseId(registerRequest.title(), COURSE_ID)).thenReturn(false);
		when(mockAuthUser.getUser()).thenReturn(mockUser);
		
		when(mockTopic.getId()).thenReturn(1L);
		when(mockTopic.getTitle()).thenReturn(TOPIC_TITLE);
		when(mockTopic.getAuthor()).thenReturn(mockUser);
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		
		when(topicRepository.save(ArgumentMatchers.<Topic> any())).thenReturn(mockTopic);
		
		TopicDetailDTO result = topicService.createTopic(mockAuthUser, COURSE_ID, registerRequest);
		assertNotNull(result);
		assertEquals(TOPIC_TITLE, result.title());
	}

	@Test
	void shouldReturnTopicDetailDtoWhenTopicIsSuccessfullyUpdated() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);
		User mockUser = mock(User.class);

		TopicUpdateRequestDTO updateRequest = new TopicUpdateRequestDTO(TOPIC_TITLE, UPDATED_TOPIC_BODY, false);
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		//when(topicRepository.existsByTitleAndCourseId(updateRequest.title(), COURSE_ID)).thenReturn(false);

		when(mockTopic.getId()).thenReturn(1L);
		when(mockTopic.getTitle()).thenReturn(TOPIC_TITLE);
		when(mockTopic.getAuthor()).thenReturn(mockUser);
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(1L);
		
		when(mockTopic.getBody()).thenReturn(TOPIC_BODY);
        doAnswer(invocation -> {
            TopicUpdateRequestDTO passedData = invocation.getArgument(0);
            when(mockTopic.getBody()).thenReturn(passedData.body());
            return null;
        }).when(mockTopic).update(any(TopicUpdateRequestDTO.class));
		
		TopicDetailDTO result = topicService.updateTopic(mockAuthUser, COURSE_ID, TOPIC_ID, updateRequest);
		
		assertNotNull(result);
	    verify(mockTopic).update(updateRequest);
		assertEquals(UPDATED_TOPIC_BODY, result.body());
	}
	
	@Test
	void shouldReturnTopicWithAuthorDtoWhenTopicExistsOnGetTopic() {
		AuthUser mockUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);
		User mockAuthor = mock(User.class);

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(mockCourse.getId()).thenReturn(COURSE_ID);

		when(topicRepository.findByIdWithAuthor(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockTopic.getAuthor()).thenReturn(mockAuthor);
		when(mockAuthor.getFirstName()).thenReturn("Julian");
		when(mockAuthor.getLastName()).thenReturn("Assange");
		when(mockTopic.getCreationDate()).thenReturn(LocalDateTime.now());
		when(mockTopic.getUpdateDate()).thenReturn(LocalDateTime.now());
		
		TopicWithAuthorDTO result = topicService.getTopic(mockUser, COURSE_ID, TOPIC_ID);

		assertNotNull(result);
		assertEquals("Julian", result.author().firstName());
		assertEquals("Assange", result.author().lastName());
	}

	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminOrAuthorOnUpdate() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);
		TopicUpdateRequestDTO updateRequestDTO = mock(TopicUpdateRequestDTO.class);
			
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(COURSE_ID);

		doThrow(new AccessDeniedException("Access denied!"))
	       	.when(serviceUtil).checkAdminModeratorOrAuthor(mockAuthUser, mockTopic);
			
		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
			() -> topicService.updateTopic(mockAuthUser, COURSE_ID, TOPIC_ID, updateRequestDTO));
		assertEquals("Access denied!", ex.getMessage());
	}
	
	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminOrEnrolledOnListByCourse() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		
		doThrow(new AccessDeniedException("Access denied!"))
    		.when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		
		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> topicService.listByCourse(mockAuthUser, COURSE_ID));
		assertEquals("Access denied!", ex.getMessage());
	}
	
	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminOrEnrolledOnCreate() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		TopicRegisterRequestDTO registerRequestDTO = mock(TopicRegisterRequestDTO.class);
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		
		doThrow(new AccessDeniedException("Access denied!"))
    		.when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		
		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> topicService.createTopic(mockAuthUser, COURSE_ID, registerRequestDTO));
		assertEquals("Access denied!", ex.getMessage());
	}
	
	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminModeratorOrAuthorOnDelete() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(COURSE_ID);

		doThrow(new AccessDeniedException("Access denied!"))
	       	.when(serviceUtil).checkAdminModeratorOrAuthor(mockAuthUser, mockTopic);
									
		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> topicService.deleteTopic(mockAuthUser, COURSE_ID, TOPIC_ID));
		assertEquals("Access denied!", ex.getMessage());
	}
	
	@Test
	void shouldThrowExceptionWhenCourseNotFoundOnDelete() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());
		
		CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
				() -> topicService.deleteTopic(mockAuthUser, COURSE_ID, TOPIC_ID));
		assertEquals("Course with id 1 not found!", ex.getMessage());
	}
	
	@Test
	void shouldThrowExceptionWhenCourseNotFoundOnGetTopic() {
		AuthUser mockUser = mock(AuthUser.class);

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

		CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
				() -> topicService.getTopic(mockUser, COURSE_ID, TOPIC_ID));
		assertEquals("Course with id 1 not found!", ex.getMessage());
	}

	@Test
	void shouldThrowExceptionWhenCourseNotFoundOnListByCourse() {
		AuthUser mockUser = mock(AuthUser.class);

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());
		CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
				() -> topicService.listByCourse(mockUser, COURSE_ID));
		assertEquals("Course with id 1 not found!", ex.getMessage());
	}
	
	@Test
	void shouldThrowExceptionWhenCourseNotFoundOnUpdate() {
		AuthUser mockUser = mock(AuthUser.class);
		TopicUpdateRequestDTO updateRequestDTO = mock(TopicUpdateRequestDTO.class);
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

		CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
				() -> topicService.updateTopic(mockUser, COURSE_ID, TOPIC_ID, updateRequestDTO));
		assertEquals("Course with id 1 not found!", ex.getMessage());
	}
	
	@Test
	void shouldThrowExceptionWhenTopicDoesNotBelongToCourseOnDelete() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);
		Long badCourseId = 2L;
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(badCourseId);
		
		TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class,
				() -> topicService.deleteTopic(mockAuthUser, COURSE_ID, TOPIC_ID));
		assertEquals("Topic does not belong to Course", ex.getMessage());
	}


	@Test
	void shouldThrowExceptionWhenTopicDoesNotBelongToCourseOnGetTopic() {
		// TopicWithAuthorDTO getTopic(AuthUser authUser, Long COURSE_ID, Long TOPIC_ID)
		AuthUser mockUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);
		Long badCourseId = 2L;
		
		// -> Course course = validateCourseAccess(authUser, COURSE_ID);
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));

		// -> Topic topic = findTopicWithAuthorOrThrow(TOPIC_ID);
		when(topicRepository.findByIdWithAuthor(TOPIC_ID)).thenReturn(Optional.of(mockTopic));

		// -> if(!topic.getCourse().getId().equals(COURSE_ID)) {
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(badCourseId);

		TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class, () -> {
			topicService.getTopic(mockUser, COURSE_ID, TOPIC_ID);
		});
		// System.out.println(ex.getMessage());
		assertEquals("Topic does not belong to Course", ex.getMessage());
	}
	
	@Test
	void shouldThrowExceptionWhenTopicDoesNotBelongToCourseOnUpdate() {
		// TopicWithAuthorDTO getTopic(AuthUser authUser, Long COURSE_ID, Long TOPIC_ID)
		AuthUser mockUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);
		Long badCourseId = COURSE_ID + 3L;
		TopicUpdateRequestDTO updateRequestDTO = mock(TopicUpdateRequestDTO.class);
				//new TopicUpdateRequestDTO(TOPIC_BODY, TOPIC_BODY, TOPIC_CLOSED);

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(badCourseId);

		TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class, () -> {
			topicService.updateTopic(mockUser, COURSE_ID, TOPIC_ID, updateRequestDTO);
		});
		assertEquals("Topic does not belong to Course", ex.getMessage());
	}
	
	@Test
	void shouldThrowExceptionWhenTopicNotFoundOnDelete() {
		AuthUser mockAuthUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.empty());
		
		TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
				() -> topicService.deleteTopic(mockAuthUser, COURSE_ID, TOPIC_ID));
		assertEquals("Topic with id 1 not found!", ex.getMessage());
	}
	
	@Test
	void shouldThrowExceptionWhenTopicNotFoundOnGetTopic() {
		AuthUser mockUser = mock(AuthUser.class);

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mock(Course.class)));
		when(topicRepository.findByIdWithAuthor(TOPIC_ID)).thenReturn(Optional.empty());

		TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
				() -> topicService.getTopic(mockUser, COURSE_ID, TOPIC_ID));
		assertEquals("Topic with id 1 not found!", ex.getMessage());
	}
	
	@Test
	void shouldThrowExceptionWhenTopicNotFoundOnUpdate() {
		AuthUser mockUser = mock(AuthUser.class);
		TopicUpdateRequestDTO updateRequestDTO = new TopicUpdateRequestDTO(TOPIC_BODY, TOPIC_BODY, TOPIC_CLOSED);

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mock(Course.class)));
		
		// Any method returning an Optional (e.g., findById(...)) that is not explicitly
		// stubbed will default to returning Optional.empty() as long as the MockSettings
		// remain unmodified.
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.empty());

		TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
				() -> topicService.updateTopic(mockUser, COURSE_ID, TOPIC_ID, updateRequestDTO));
		assertEquals("Topic with id 1 not found!", ex.getMessage());
	}
	
	@Test
	void sholdThrowExceptionWhenTopicNameAlreadyExistsOnUpdate() {
		AuthUser mockUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);
		Topic mockTopic = mock(Topic.class);

		TopicUpdateRequestDTO updateRequestDTO = new TopicUpdateRequestDTO("title1", TOPIC_BODY, TOPIC_CLOSED);

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		doNothing().when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockUser, mockCourse);
		when(topicRepository.findById(2L)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(COURSE_ID);
		doNothing().when(serviceUtil).checkAdminModeratorOrAuthor(mockUser, mockTopic);
		when(topicRepository.existsByTitleAndCourseIdAndIdNot("title1", COURSE_ID, 2L)).thenReturn(true);

		TopicAlreadyExistsException ex = assertThrows(TopicAlreadyExistsException.class,
				() -> topicService.updateTopic(mockUser, COURSE_ID, 2L, updateRequestDTO));
		assertEquals("A topic with the value 'title1' already exists!", ex.getMessage()); // TODO: exception method to
																							// get field
		verify(topicRepository).existsByTitleAndCourseIdAndIdNot(eq("title1"), eq(COURSE_ID), eq(2L));

		verifyNoMoreInteractions(courseRepository);
		verifyNoMoreInteractions(topicRepository);
	}

}
