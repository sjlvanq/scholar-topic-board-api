package uno.lode.ScholarTopicBoard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.reply.Reply;
import uno.lode.ScholarTopicBoard.domain.reply.ReplyRepository;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyDetailDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.topic.TopicRepository;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ParentReplyDoesNotBelongToTopicException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDepthLimitExceededException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDoesNotBelongToTopicException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicDoesNotBelongToCourseException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.util.ServiceUtil;

@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {
	@Mock
	private CourseRepository courseRepository;
	@Mock
	private TopicRepository topicRepository;
	@Mock
	private ReplyRepository replyRepository;
	@Mock
	private ServiceUtil serviceUtil;
	@InjectMocks
	private ReplyService replyService;

	@Mock private AuthUser mockAuthUser;
	@Mock private User mockAuthor;
	@Mock private Course mockCourse;
	@Mock private Topic mockTopic;
	@Mock private Reply mockReply;
	
	private final Long USER_ID = 1L;
	private final Long COURSE_ID = 2L;
	private final Long TOPIC_ID = 3L;
	private final Long REPLY_PARENT_ID = 4L;
	private final Long REPLY_ID = 5L;
	private final String REPLY_BODY = "This is a sample reply body";
	private final String UPDATED_REPLY_BODY = "This is the updated reply body.";
	
	@Test
	void shouldCreateRootReplySuccessfully() {
		Reply mockSavedReply = mock(Reply.class);
		ReplyRegisterRequestDTO registerRequestDTO = new ReplyRegisterRequestDTO(REPLY_BODY);

		setUpBasicAccessValidation();
		when(replyRepository.save(any(Reply.class))).thenReturn(mockSavedReply);

		when(mockSavedReply.getId()).thenReturn(REPLY_ID);
		when(mockSavedReply.getAuthor()).thenReturn(mockAuthor);
		when(mockSavedReply.getBody()).thenReturn(REPLY_BODY);
		when(mockAuthor.getFirstName()).thenReturn("Test");
		when(mockAuthor.getLastName()).thenReturn("Author");
		// when(mockSavedReply.getCreationDate()).thenReturn(LocalDateTime.now());
		// when(mockSavedReply.getUpdateDate()).thenReturn(LocalDateTime.now());

		ReplyDetailDTO result = replyService.createReply(mockAuthUser, COURSE_ID, TOPIC_ID, null, registerRequestDTO);
		
		assertNotNull(result);
		assertEquals(REPLY_ID, result.id());
		assertEquals(REPLY_BODY, result.body());
		assertEquals("Test Author", result.authorName());
		assertTrue(result.children().isEmpty());

	    setUpBasicAccessValidationVerify();
		verify(courseRepository).findById(COURSE_ID);
		verify(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		verify(topicRepository).findById(TOPIC_ID);
		verify(replyRepository).save(any(Reply.class));
	}
	
	@Test
	void shouldDeleteReplySuccessfullyWhenUserIsAdmin() {
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));
		when(mockReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		
		when(mockAuthUser.isAdmin()).thenReturn(true);
		//when(mockAuthUser.isModerator()).thenReturn(false);
		//when(mockAuthUser.getId()).thenReturn(USER_ID);
		//when(mockReply.isAuthoredBy(USER_ID)).thenReturn(false);
		
		replyService.deleteReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID);
	    setUpBasicAccessValidationVerify();
		verify(replyRepository).delete(mockReply);
	}

	@Test
	void shouldDeleteReplySuccessfullyWhenUserIsModerator() {
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));
		when(mockReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		
		when(mockAuthUser.isAdmin()).thenReturn(false);
		when(mockAuthUser.isModerator()).thenReturn(true);
		when(mockAuthUser.getId()).thenReturn(USER_ID);
		when(mockReply.isAuthoredBy(USER_ID)).thenReturn(false);
		
		replyService.deleteReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID);
	    setUpBasicAccessValidationVerify();
		verify(replyRepository).delete(mockReply);
	}
	
	@Test
	void shouldDeleteReplySuccessfullyWhenUserIsAuthor() {
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));
		when(mockReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		
		//when(mockAuthUser.isAdmin()).thenReturn(false);
		//when(mockAuthUser.isModerator()).thenReturn(false);
		when(mockAuthUser.getId()).thenReturn(USER_ID);
		when(mockReply.isAuthoredBy(USER_ID)).thenReturn(true);
		
		replyService.deleteReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID);
	    setUpBasicAccessValidationVerify();
		verify(replyRepository).delete(mockReply);
	}
	
	@Test
	void shouldReturnListOfReplyDetailDTO() {
		Reply mockReply1 = mock(Reply.class);
		Reply mockReply2 = mock(Reply.class);
		
		setUpBasicAccessValidation();
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(replyRepository.findByTopicIdAndParentIsNull(TOPIC_ID)).thenReturn(Set.of(mockReply1, mockReply2));
		
		when(mockReply1.getId()).thenReturn(1L);
		when(mockReply2.getId()).thenReturn(2L);
		when(mockReply1.getAuthor()).thenReturn(mockAuthor);
		when(mockReply2.getAuthor()).thenReturn(mockAuthor);
		when(mockAuthor.getFirstName()).thenReturn("Johnny");
		when(mockAuthor.getLastName()).thenReturn("Cage");

		var result = replyService.getRootReplies(mockAuthUser, COURSE_ID, TOPIC_ID);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.stream().allMatch(r -> r instanceof ReplyDetailDTO));
		assertTrue(result.stream().allMatch(r -> r.authorName().equals("Johnny Cage")));
		assertEquals(Set.of(1L, 2L), result.stream().map(ReplyDetailDTO::id).collect(Collectors.toSet()));
		
	    setUpBasicAccessValidationVerify();
	}
	
	@Test
	void shouldReturnReplyDetailDTO() {
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));

	    when(mockReply.getId()).thenReturn(REPLY_ID);
	    when(mockReply.getAuthor()).thenReturn(mockAuthor);
	    when(mockAuthor.getFirstName()).thenReturn("Johnny");
	    when(mockAuthor.getLastName()).thenReturn("Cage");
	    when(mockReply.getTopic()).thenReturn(mockTopic);
	    when(mockTopic.getId()).thenReturn(TOPIC_ID);
	    when(mockReply.getBody()).thenReturn(REPLY_BODY);
	    
	    var result = replyService.getReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID);

	    assertNotNull(result);
	    assertEquals(REPLY_ID, result.id());
	    assertEquals("Johnny Cage", result.authorName());
	    assertEquals(REPLY_BODY, result.body());
	    
	    setUpBasicAccessValidationVerify();
	}
	
	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminModeratorOrAuthorOnDeleteReply() {
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));
		when(mockReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		
		when(mockAuthUser.isAdmin()).thenReturn(false);
		when(mockAuthUser.isModerator()).thenReturn(false);
		when(mockAuthUser.getId()).thenReturn(USER_ID);
		when(mockReply.isAuthoredBy(USER_ID)).thenReturn(false);
		
		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> replyService.deleteReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID));
		assertEquals("Access denied!", ex.getMessage());
	    setUpBasicAccessValidationVerify();
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminOrEnrolledOnGetReply() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		doThrow(new AccessDeniedException("Access denied!"))
			.when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);

		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> replyService.getReply(mockAuthUser, COURSE_ID, null, null));
		assertEquals("Access denied!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminOrEnrolledOnListRootReplies() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		doThrow(new AccessDeniedException("Access denied!"))
			.when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);

		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> replyService.getRootReplies(mockAuthUser, COURSE_ID, null));
		assertEquals("Access denied!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminOrEnrolledOnUpdate() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		doThrow(new AccessDeniedException("Access denied!"))
			.when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);

		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> replyService.updateReply(mockAuthUser, COURSE_ID, null, null, null));
		assertEquals("Access denied!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowCourseNotFoundOnDeleteReply() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());
		CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
				() -> replyService.deleteReply(mockAuthUser, COURSE_ID, null, null));
		assertEquals("Course with id " + COURSE_ID + " not found!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowCourseNotFoundOnGetReply() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());
		CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
				() -> replyService.getReply(mockAuthUser, COURSE_ID, null, null));
		assertEquals("Course with id " + COURSE_ID + " not found!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowCourseNotFoundOnListRootReplies() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());
		CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
				() -> replyService.getRootReplies(mockAuthUser, COURSE_ID, null));
		assertEquals("Course with id " + COURSE_ID + " not found!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowParentReplyDoesNotBelongToTopicExceptionWhenParentIdIsInvalidOnCreate() {
		Reply 	mockParentReply = mock(Reply.class);
		Topic 	mockParentTopic = mock(Topic.class);
		Long 	badTopicId = 666L;

		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		doNothing().when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		when(topicRepository.findById(badTopicId)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(COURSE_ID);
		when(mockAuthUser.getUser()).thenReturn(mockAuthor);
		when(replyRepository.findById(REPLY_PARENT_ID)).thenReturn(Optional.of(mockParentReply));
		when(mockParentReply.getTopic()).thenReturn(mockParentTopic);
		when(mockParentTopic.getId()).thenReturn(TOPIC_ID);
		
		ParentReplyDoesNotBelongToTopicException ex = assertThrows(ParentReplyDoesNotBelongToTopicException.class,
				() -> 	replyService.createReply(mockAuthUser, COURSE_ID, badTopicId, REPLY_PARENT_ID, new ReplyRegisterRequestDTO(REPLY_BODY)));
		assertEquals("Parent reply does not belong to topic", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldCreateReplyWhenParentIsAtMaxAllowedDepth() {
		Reply mockParentReply = mock(Reply.class);
		ReplyRegisterRequestDTO registerRequestDTO = new ReplyRegisterRequestDTO(REPLY_BODY);

		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_PARENT_ID)).thenReturn(Optional.of(mockParentReply));
		when(replyRepository.save(any(Reply.class))).thenReturn(mockReply);

		when(mockParentReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		Reply mockGrandparentReply = mock(Reply.class); // Lvl 2 - OK

		when(mockParentReply.getParent()).thenReturn(mockGrandparentReply);
		when(mockGrandparentReply.getParent()).thenReturn(null);

		when(mockReply.getId()).thenReturn(REPLY_ID);
		when(mockReply.getBody()).thenReturn(REPLY_BODY);
		when(mockReply.getAuthor()).thenReturn(mockAuthor);
		when(mockAuthor.getFirstName()).thenReturn("Test");
		when(mockAuthor.getLastName()).thenReturn("Author");

		ReplyDetailDTO result = replyService.createReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_PARENT_ID, registerRequestDTO);
		
		assertNotNull(result);
		assertEquals(REPLY_ID, result.id());
		assertEquals(REPLY_BODY, result.body());
		assertEquals("Test Author", result.authorName());

	    setUpBasicAccessValidationVerify();
		verify(mockParentReply).getParent();
		verify(mockGrandparentReply).getParent();
		verify(replyRepository).save(any(Reply.class));
	}
	
	@Test
	void shouldThrowReplyDepthLimitExceededWhenParentIsAtDepthLimit() {
		Reply mockParentReply = mock(Reply.class);
		ReplyRegisterRequestDTO registerRequestDTO = new ReplyRegisterRequestDTO(REPLY_BODY);

		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_PARENT_ID)).thenReturn(Optional.of(mockParentReply));

		when(mockParentReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		Reply mockGrandparentReply = mock(Reply.class);
		Reply mockGreatGrandparentReply = mock(Reply.class); // Lvl 3

		when(mockParentReply.getParent()).thenReturn(mockGrandparentReply);
		when(mockGrandparentReply.getParent()).thenReturn(mockGreatGrandparentReply);
		when(mockGreatGrandparentReply.getParent()).thenReturn(null);

		//ReplyDepthLimitExceededException ex = 
		assertThrows(ReplyDepthLimitExceededException.class,
				() -> replyService.createReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_PARENT_ID, registerRequestDTO));

	    setUpBasicAccessValidationVerify();
		verify(mockParentReply).getParent();
		verify(mockGrandparentReply).getParent();
		verify(mockGreatGrandparentReply).getParent();
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowReplyDepthLimitExceededWhenParentIsWellBeyondDepthLimit() {
		Reply mockParentReply = mock(Reply.class);
		ReplyRegisterRequestDTO registerRequestDTO = new ReplyRegisterRequestDTO(REPLY_BODY);

		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_PARENT_ID)).thenReturn(Optional.of(mockParentReply));

		when(mockParentReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		Reply mockGrandparentReply = mock(Reply.class);
		Reply mockGreatGrandparentReply = mock(Reply.class); // Lvl 3
		Reply mockGreatGreatGrandparentReply = mock(Reply.class); // Lvl 4 - ###

		when(mockParentReply.getParent()).thenReturn(mockGrandparentReply);
		when(mockGrandparentReply.getParent()).thenReturn(mockGreatGrandparentReply);
		when(mockGreatGrandparentReply.getParent()).thenReturn(mockGreatGreatGrandparentReply);
		when(mockGreatGreatGrandparentReply.getParent()).thenReturn(null);

		//ReplyDepthLimitExceededException ex = 
		assertThrows(ReplyDepthLimitExceededException.class,
				() -> replyService.createReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_PARENT_ID, registerRequestDTO));

	    setUpBasicAccessValidationVerify();
		verify(mockParentReply).getParent();
		verify(mockGrandparentReply).getParent();
		verify(mockGreatGrandparentReply).getParent();
		verify(mockGreatGrandparentReply).getParent();
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowReplyDoesNotBelongToTopicOnGetReply() {
		Long actualReplyTopicId = TOPIC_ID;
		Topic mockActualReplyTopic = mock(Topic.class);
		Long requestedTopicId = TOPIC_ID + 2L;
		Topic mockRequestedTopic = mock(Topic.class);
		
		doNothing().when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(requestedTopicId)).thenReturn(Optional.of(mockRequestedTopic));
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));
		
		when(mockRequestedTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(COURSE_ID);
		
		when(mockReply.getTopic()).thenReturn(mockActualReplyTopic);
		when(mockActualReplyTopic.getId()).thenReturn(actualReplyTopicId);
		
		ReplyDoesNotBelongToTopicException ex = assertThrows(ReplyDoesNotBelongToTopicException.class,
				() -> replyService.getReply(mockAuthUser, COURSE_ID, requestedTopicId, REPLY_ID));
		assertEquals("Reply does not belong to Topic", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowReplyNotFoundExceptionWhenParentReplyNotFoundOnCreate() {
		setUpBasicAccessValidation();
		when(mockAuthUser.getUser()).thenReturn(mockAuthor);
		when(replyRepository.findById(REPLY_PARENT_ID)).thenReturn(Optional.empty());
		
		ReplyNotFoundException ex = assertThrows(ReplyNotFoundException.class,
				() -> 	replyService.createReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_PARENT_ID, new ReplyRegisterRequestDTO(REPLY_BODY)));
		assertEquals("Reply with id " + REPLY_PARENT_ID + " not found!", ex.getMessage());
	    setUpBasicAccessValidationVerify();
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowReplyNotFoundOnDeleteReply() {
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.empty());
		ReplyNotFoundException ex = 
			assertThrows(ReplyNotFoundException.class,
				() -> replyService.deleteReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID));
		assertEquals("Reply with id " + REPLY_ID + " not found!", ex.getMessage());
	    setUpBasicAccessValidationVerify();
		verifyNoMoreInteractions(replyRepository);
	}

	@Test
	void shouldThrowReplyNotFoundOnGetReply() {
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.empty());
		ReplyNotFoundException ex = assertThrows(ReplyNotFoundException.class,
				() -> replyService.getReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID));
		assertEquals("Reply with id " + REPLY_ID + " not found!", ex.getMessage());
	    setUpBasicAccessValidationVerify();
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowTopicDoesNotBelongToCourseExceptionWhenTopicIdIsInvalidOnCreate() {
		Long actualCourseIdOfTopic = COURSE_ID;
		Long requestedCourseId = 12L;
		ReplyRegisterRequestDTO registerRequestDTO = new ReplyRegisterRequestDTO(REPLY_BODY);

		when(courseRepository.findById(requestedCourseId)).thenReturn(Optional.of(mockCourse));
		doNothing().when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(actualCourseIdOfTopic);

		TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class,
				() -> replyService.createReply(mockAuthUser, requestedCourseId, TOPIC_ID, null, registerRequestDTO));
		assertEquals("Topic does not belong to Course", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowTopicDoesNotBelongToCourseOnDeleteReply() {
		Long badCourseId = COURSE_ID + 2L;

		setUpBasicAccessValidation();
		when(mockCourse.getId()).thenReturn(badCourseId);
				
		TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class,
				() -> replyService.deleteReply(mockAuthUser, COURSE_ID, TOPIC_ID, null));
		assertEquals("Topic does not belong to Course", ex.getMessage());
	    setUpBasicAccessValidationVerify();
		verifyNoMoreInteractions(replyRepository);
	}
		
	@Test
	void shouldThrowTopicDoesNotBelongToCourseOnGetReply() {
		Long badCourseId = COURSE_ID + 2L;
		
		setUpBasicAccessValidation();
		when(mockCourse.getId()).thenReturn(badCourseId);
				
		TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class,
				() -> replyService.getReply(mockAuthUser, COURSE_ID, TOPIC_ID, null));
		assertEquals("Topic does not belong to Course", ex.getMessage());
	    setUpBasicAccessValidationVerify();
		verifyNoMoreInteractions(replyRepository);
	}
		
	@Test
	void shouldThrowTopicDoesNotBelongToCourseOnListRootReplies() {
		Course mockTopicCourse = mock(Course.class);
		Long badCourseId = COURSE_ID+2L;
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockTopicCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockTopicCourse);
		when(mockTopicCourse.getId()).thenReturn(badCourseId);

		TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class,
				() -> replyService.getRootReplies(mockAuthUser, COURSE_ID, TOPIC_ID));
		assertEquals("Topic does not belong to Course", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowTopicNotFoundOnDeleteReply() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.empty());
		TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
				() -> replyService.deleteReply(mockAuthUser, COURSE_ID, TOPIC_ID, null));
		assertEquals("Topic with id " + TOPIC_ID + " not found!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowTopicNotFoundOnGetReply() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.empty());
		TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
				() -> replyService.getReply(mockAuthUser, COURSE_ID, TOPIC_ID, null));
		assertEquals("Topic with id " + TOPIC_ID + " not found!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowTopicNotFoundOnListRootReplies() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.empty());
		TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
				() -> replyService.getRootReplies(mockAuthUser, COURSE_ID, TOPIC_ID));
		assertEquals("Topic with id " + TOPIC_ID + " not found!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldUpdateReplyAndReturnReplyDetailDTOWhenUserIsAdmin() {
		ReplyUpdateRequestDTO updateRequestDTO = new ReplyUpdateRequestDTO(UPDATED_REPLY_BODY);

		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));

		when(mockReply.getId()).thenReturn(REPLY_ID);
		when(mockReply.getAuthor()).thenReturn(mockAuthor);
		when(mockReply.getTopic()).thenReturn(mockTopic);
	    when(mockTopic.getId()).thenReturn(TOPIC_ID);
		
	    when(mockAuthUser.isAdmin()).thenReturn(true);
	    
		when(mockReply.getBody()).thenReturn(REPLY_BODY);
        doAnswer(invocation -> {
            ReplyUpdateRequestDTO passedData = invocation.getArgument(0);
            when(mockReply.getBody()).thenReturn(passedData.body());
            return null;
        }).when(mockReply).update(any(ReplyUpdateRequestDTO.class));
        
	    var result = replyService.updateReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID, updateRequestDTO);
	    
	    assertNotNull(result);
	    assertEquals(REPLY_ID, result.id());
	    assertEquals(UPDATED_REPLY_BODY, result.body());
	    
	    verify(mockReply).update(updateRequestDTO);
	    setUpBasicAccessValidationVerify();
	}
	
	@Test
	void shouldUpdateReplyAndReturnReplyDetailDTOWhenUserIsModerator() {
		ReplyUpdateRequestDTO updateRequestDTO = new ReplyUpdateRequestDTO(UPDATED_REPLY_BODY);
		
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));

		when(mockReply.getId()).thenReturn(REPLY_ID);
		when(mockReply.getAuthor()).thenReturn(mockAuthor);
		when(mockReply.getTopic()).thenReturn(mockTopic);
	    when(mockTopic.getId()).thenReturn(TOPIC_ID);
		
	    when(mockAuthUser.isModerator()).thenReturn(true);
	    
		when(mockReply.getBody()).thenReturn(REPLY_BODY);
        doAnswer(invocation -> {
            ReplyUpdateRequestDTO passedData = invocation.getArgument(0);
            when(mockReply.getBody()).thenReturn(passedData.body());
            return null;
        }).when(mockReply).update(any(ReplyUpdateRequestDTO.class));
        
	    var result = replyService.updateReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID, updateRequestDTO);
	    
	    assertNotNull(result);
	    assertEquals(REPLY_ID, result.id());
	    assertEquals(UPDATED_REPLY_BODY, result.body());
	    
	    verify(mockReply).update(updateRequestDTO);
	    setUpBasicAccessValidationVerify();
	}
	
	@Test
	void shouldUpdateReplyAndReturnReplyDetailDTOWhenUserIsAuthor() {
		Long authorId = 1L;
		ReplyUpdateRequestDTO updateRequestDTO = new ReplyUpdateRequestDTO(UPDATED_REPLY_BODY);
		
		setUpBasicAccessValidation();
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));

		when(mockReply.getId()).thenReturn(REPLY_ID);
		when(mockReply.getAuthor()).thenReturn(mockAuthor);
		when(mockReply.getTopic()).thenReturn(mockTopic);
	    when(mockTopic.getId()).thenReturn(TOPIC_ID);
	    
		when(mockAuthUser.getId()).thenReturn(authorId);
	    when(mockReply.isAuthoredBy(authorId)).thenReturn(true);
	    
		when(mockReply.getBody()).thenReturn(REPLY_BODY);
        doAnswer(invocation -> {
            ReplyUpdateRequestDTO passedData = invocation.getArgument(0);
            when(mockReply.getBody()).thenReturn(passedData.body());
            return null;
        }).when(mockReply).update(any(ReplyUpdateRequestDTO.class));
        
	    var result = replyService.updateReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID, updateRequestDTO);

	    assertNotNull(result);
	    assertEquals(REPLY_ID, result.id());
	    assertEquals(UPDATED_REPLY_BODY, result.body());
	    
	    verify(mockReply).update(updateRequestDTO);
	    setUpBasicAccessValidationVerify();
	}
	
	@Test
	void shouldCreateChildReplySuccessfully() {
		Reply mockParentReply = mock(Reply.class);
		Long parentReplyId = 12345L;
		Reply mockSavedReply = mock(Reply.class);
		ReplyRegisterRequestDTO registerRequestDTO = new ReplyRegisterRequestDTO(REPLY_BODY);

		setUpBasicAccessValidation();
		
		// validateParentReply
		when(replyRepository.findById(parentReplyId)).thenReturn(Optional.of(mockParentReply));
		when(mockParentReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		
		// getDepth
		when(mockParentReply.getParent()).thenReturn(null);
		
		when(replyRepository.save(any(Reply.class))).thenReturn(mockSavedReply);

		when(mockSavedReply.getId()).thenReturn(REPLY_ID);
		when(mockSavedReply.getAuthor()).thenReturn(mockAuthor);
		when(mockSavedReply.getBody()).thenReturn(REPLY_BODY);
		when(mockAuthor.getFirstName()).thenReturn("Test");
		when(mockAuthor.getLastName()).thenReturn("Author");
		// when(mockSavedReply.getCreationDate()).thenReturn(LocalDateTime.now());
		// when(mockSavedReply.getUpdateDate()).thenReturn(LocalDateTime.now());

		ReplyDetailDTO result = replyService.createReply(mockAuthUser, COURSE_ID, TOPIC_ID, parentReplyId, registerRequestDTO);
		
		assertNotNull(result);
		assertEquals(REPLY_ID, result.id());
		assertEquals(REPLY_BODY, result.body());
		assertEquals("Test Author", result.authorName());
		assertTrue(result.children().isEmpty());

		setUpBasicAccessValidationVerify();
		verify(replyRepository).save(any(Reply.class));
	}
	
	@Test
	void shouldThrowAccessDeniedWhenUserNotAdminOrEnrolledOnCreate() {	
		ReplyRegisterRequestDTO registerRequestDTO = new ReplyRegisterRequestDTO(REPLY_BODY);
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		
		doThrow(new AccessDeniedException("Access denied!"))
			.when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);

		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> replyService.createReply(mockAuthUser, COURSE_ID, TOPIC_ID, null, registerRequestDTO));

		verify(courseRepository).findById(COURSE_ID);
		verify(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		assertEquals("Access denied!", ex.getMessage());
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowReplyDoesNotBelongToTopicExceptionOnUpdate() {
		Long  paramBasedTopicId = TOPIC_ID + 3L;
		Topic paramBasedTopic = mock(Topic.class);
		Topic replyInternalTopic = mock(Topic.class);
		Long  replyInternalTopicId = TOPIC_ID;

		ReplyUpdateRequestDTO updateRequestDTO = new ReplyUpdateRequestDTO(REPLY_BODY);
		
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		doNothing().when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		when(topicRepository.findById(paramBasedTopicId)).thenReturn(Optional.of(paramBasedTopic));
		when(paramBasedTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(COURSE_ID);
		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));
		when(mockReply.getTopic()).thenReturn(replyInternalTopic);
		when(replyInternalTopic.getId()).thenReturn(replyInternalTopicId);
		
		//ReplyDoesNotBelongToTopicException ex = 
		assertThrows(ReplyDoesNotBelongToTopicException.class,
				() -> replyService.updateReply(mockAuthUser, COURSE_ID, paramBasedTopicId, REPLY_ID, updateRequestDTO));
		verifyNoMoreInteractions(replyRepository);
	}
	
	@Test
	void shouldThrowAccessDeniedExceptionWhenUserNotAdminModeratorOrAuthorOnUpdate() {
		Long userId = 13974L;
		
		ReplyUpdateRequestDTO updateRequestDTO = new ReplyUpdateRequestDTO(REPLY_BODY);
		
		setUpBasicAccessValidation();

		when(replyRepository.findById(REPLY_ID)).thenReturn(Optional.of(mockReply));
		when(mockReply.getTopic()).thenReturn(mockTopic);
		when(mockTopic.getId()).thenReturn(TOPIC_ID);
		
		when(mockAuthUser.isAdmin()).thenReturn(false);
		when(mockAuthUser.isModerator()).thenReturn(false);
		when(mockAuthUser.getId()).thenReturn(userId);
		when(mockReply.isAuthoredBy(userId)).thenReturn(false);

		AccessDeniedException ex = assertThrows(AccessDeniedException.class,
				() -> replyService.updateReply(mockAuthUser, COURSE_ID, TOPIC_ID, REPLY_ID, updateRequestDTO));

		assertEquals("Access denied!", ex.getMessage());
		setUpBasicAccessValidationVerify();
		verifyNoMoreInteractions(replyRepository);
	}
	
	private void setUpBasicAccessValidation() {
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(mockCourse));
		doNothing().when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		when(topicRepository.findById(TOPIC_ID)).thenReturn(Optional.of(mockTopic));
		when(mockTopic.getCourse()).thenReturn(mockCourse);
		when(mockCourse.getId()).thenReturn(COURSE_ID);
	}
	
	private void setUpBasicAccessValidationVerify() {
		verify(courseRepository).findById(COURSE_ID);
		verify(serviceUtil).checkAdminCoordinatorOrEnrolled(mockAuthUser, mockCourse);
		verify(topicRepository).findById(TOPIC_ID);
	}
	
}
