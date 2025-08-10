package uno.lode.ScholarTopicBoard.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.topic.TopicRepository;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ParentReplyDoesNotBelongToTopicException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDoesNotBelongToTopicException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicDoesNotBelongToCourseException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@ExtendWith(MockitoExtension.class)
class DomainValidationServiceUnitTest {

	@Mock
	private TopicRepository topicRepository;

	@Mock
	private CourseRepository courseRepository;

	@Mock
	private ReplyRepository replyRepository;

	@Mock
	private UserAuthorizationService userAuthorizationService;

	@InjectMocks
	private DomainValidationService domainValidationService;

	// Helper methods
	private Course createCourse(Long courseId) {
		Course course = mock(Course.class);
		when(course.getId()).thenReturn(courseId);
		return course;
	}

	private Topic createTopic(Long topicId, Long courseId) {
		Topic topic = mock(Topic.class);
		Course course = createCourse(courseId);
		lenient().when(topic.getId()).thenReturn(topicId);
		when(topic.getCourse()).thenReturn(course);
		return topic;
	}

	private Reply createReplyWithFlatTopic(Long replyId, Long topicId) {
		Reply reply = mock(Reply.class);
		Topic topic = mock(Topic.class);
		when(reply.getTopic()).thenReturn(topic);
		when(topic.getId()).thenReturn(topicId);
		return reply;
	}

	@Nested
	@DisplayName("findCourseOrThrow tests")
	class FindCourseOrThrowTests {

		@Test
		@DisplayName("Should return course when course exists")
		void shouldReturnCourseWhenCourseExists() {
			// Given
			Long courseId = 101L;
			Course course = createCourse(courseId);
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

			// When
			Course result = domainValidationService.findCourseOrThrow(courseId);

			// Then
			assertEquals(courseId, result.getId());
			assertEquals(course, result);
			verify(courseRepository).findById(courseId);
		}

		@Test
		@DisplayName("Should throw CourseNotFoundException when course does not exist")
		void shouldThrowCourseNotFoundExceptionWhenCourseDoesNotExist() {
			// Given
			Long courseId = 102L;
			when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

			// When
			CourseNotFoundException ex = assertThrows(CourseNotFoundException.class, () -> domainValidationService.findCourseOrThrow(courseId));

			// Then
			assertEquals(EntityDomain.COURSE, ex.getAffectedEntity());
			assertEquals(courseId, ex.getMissingValue());
			verify(courseRepository).findById(courseId);
		}
	}

	@Nested
	@DisplayName("findTopicOrThrow tests")
	class FindTopicOrThrowTests {

		@Test
		@DisplayName("Should return topic when topic exists")
		void shouldReturnTopicWhenTopicExists() {
			// Given
			Long topicId = 223L;
			Topic topic = mock(Topic.class);
			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

			// When
			Topic result = domainValidationService.findTopicOrThrow(topicId);

			// Then
			assertEquals(topic, result);
			verify(topicRepository).findById(topicId);
		}

		@Test
		@DisplayName("Should throw TopicNotFoundException when topic does not exist")
		void shouldThrowTopicNotFoundExceptionWhenTopicDoesNotExist() {
			// Given
			Long topicId = 224L;
			when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

			// When
			TopicNotFoundException ex = assertThrows(TopicNotFoundException.class, () -> domainValidationService.findTopicOrThrow(topicId));

			// Then
			assertEquals(EntityDomain.TOPIC, ex.getAffectedEntity());
			assertEquals(topicId, ex.getMissingValue());
			verify(topicRepository).findById(topicId);
		}
	}

	@Nested
	@DisplayName("findTopicWithAuthorOrThrow tests")
	class FindTopicWithAuthorOrThrowTests {

		@Test
		@DisplayName("Should return topic with author when topic exists")
		void shouldReturnTopicWithAuthorWhenTopicExists() {
			// Given
			Long topicId = 203L;
			Topic topic = mock(Topic.class);
			when(topicRepository.findByIdWithAuthor(topicId)).thenReturn(Optional.of(topic));
			when(topic.getAuthor()).thenReturn(mock(User.class));

			// When
			Topic result = domainValidationService.findTopicWithAuthorOrThrow(topicId);

			// Then
			assertNotNull(result.getAuthor());
			assertEquals(topic, result);
			verify(topicRepository).findByIdWithAuthor(topicId);
		}

		@Test
		@DisplayName("Should throw TopicNotFoundException when topic does not exist")
		void shouldThrowTopicNotFoundExceptionWhenTopicDoesNotExist() {
			// Given
			Long topicId = 204L;
			when(topicRepository.findByIdWithAuthor(topicId)).thenReturn(Optional.empty());

			// When
			TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
					() -> domainValidationService.findTopicWithAuthorOrThrow(topicId));

			// Then
			assertEquals(EntityDomain.TOPIC, ex.getAffectedEntity());
			assertEquals(topicId, ex.getMissingValue());
			verify(topicRepository).findByIdWithAuthor(topicId);
		}
	}

	@Nested
	@DisplayName("findReplyOrThrow tests")
	class FindReplyOrThrowTests {

		@Test
		@DisplayName("Should return reply when reply exists")
		void shouldReturnReplyWhenReplyExists() {
			// Given
			Long replyId = 301L;
			Reply reply = mock(Reply.class);
			when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));

			// When
			Reply result = domainValidationService.findReplyOrThrow(replyId);

			// Then
			assertEquals(reply, result);
			verify(replyRepository).findById(replyId);
		}

		@Test
		@DisplayName("Should throw ReplyNotFoundException when reply does not exist")
		void shouldThrowReplyNotFoundExceptionWhenReplyDoesNotExist() {
			// Given
			Long replyId = 302L;
			when(replyRepository.findById(replyId)).thenReturn(Optional.empty());

			// When
			ReplyNotFoundException ex = assertThrows(ReplyNotFoundException.class, () -> domainValidationService.findReplyOrThrow(replyId));

			// Then
			assertEquals(EntityDomain.REPLY, ex.getAffectedEntity());
			assertEquals(replyId, ex.getMissingValue());
			verify(replyRepository).findById(replyId);
		}
	}

	@Nested
	@DisplayName("ensureCourseHasAccess tests")
	class EnsureCourseHasAccessTests {

		@Test
		@DisplayName("Should not throw when user has access to course")
		void shouldNotThrowWhenUserHasAccessToCourse() {
			// Given
			AuthUser authUser = mock(AuthUser.class);
			Long courseId = 103L;
			Course course = mock(Course.class);

			// When
			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			doNothing().when(userAuthorizationService).ensureHasCourseAccess(authUser, course);

			// Then
			assertDoesNotThrow(() -> domainValidationService.ensureCourseHasAccess(authUser, courseId));
			verify(courseRepository).findById(courseId);
		}

		@Test
		@DisplayName("Should throw when course does not exist")
		void shouldThrowWhenCourseDoesNotExist() {
			// Given
			Long courseId = 104L;
			AuthUser authUser = mock(AuthUser.class);
			// When
			CourseNotFoundException ex = assertThrows(CourseNotFoundException.class,
					() -> domainValidationService.ensureCourseHasAccess(authUser, courseId));

			// Then
			assertEquals(EntityDomain.COURSE, ex.getAffectedEntity());
			assertEquals(courseId, ex.getMissingValue());

			verify(courseRepository).findById(courseId);
		}

		@Test
		@DisplayName("Should delegate access validation to UserAuthorizationService")
		void shouldDelegateAccessValidationToUserAuthorizationService() {
			// Given
			Long courseId = 105L;
			Course course = mock(Course.class);
			AuthUser authUser = mock(AuthUser.class);

			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

			// When
			domainValidationService.ensureCourseHasAccess(authUser, courseId);

			// Then
			verify(userAuthorizationService).ensureHasCourseAccess(authUser, course);
			verify(courseRepository).findById(courseId);
		}

		@Test
		@DisplayName("Should throw AccessDeniedException when user does not have access to course")
		void shouldThrowAccessDeniedExceptionWhenUserDoesNotHaveAccessToCourse() {
			// Given
			Long courseId = 106L;
			AuthUser authUser = mock(AuthUser.class);
			Course course = mock(Course.class);

			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			doThrow(new AccessDeniedException("Access denied")).when(userAuthorizationService)
					.ensureHasCourseAccess(authUser, course);

			// When + Then
			assertThrows(AccessDeniedException.class,
					() -> domainValidationService.ensureCourseHasAccess(authUser, courseId));
			verify(courseRepository).findById(courseId);
		}
	}

	@Nested
	@DisplayName("ensureTopicBelongToCourse tests")
	class EnsureTopicBelongToCourseTests {

		@Test
		@DisplayName("Should not throw when topic belongs to course")
		void shouldNotThrowWhenTopicBelongsToCourse() {
			// Given
			Long courseId = 107L;
			Long topicId = 221L;
			Topic topic = createTopic(topicId, courseId);

			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

			// When + Then
			assertDoesNotThrow(() -> domainValidationService.ensureTopicBelongToCourse(topicId, courseId));
			verify(topicRepository).findById(topicId);
		}

		@Test
		@DisplayName("Should throw TopicDoesNotBelongToCourseException when topic belongs to different course")
		void shouldThrowTopicDoesNotBelongToCourseExceptionWhenTopicBelongsToDifferentCourse() {
			// Given
			Long paramCourseId = 108L;
			Long realCourseId = 140L;
			Long topicId = 210L;
			Topic topic = createTopic(topicId, realCourseId);
			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

			// When
			TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class,
					() -> domainValidationService.ensureTopicBelongToCourse(topicId, paramCourseId));

			// Then
			assertEquals(EntityDomain.TOPIC, ex.getAffectedEntity());
			assertEquals(EntityDomain.COURSE, ex.getParentEntity());
			verify(topicRepository).findById(topicId);
		}

		@Test
		@DisplayName("Should throw TopicNotFoundException when topic does not exist")
		void shouldThrowTopicNotFoundExceptionWhenTopicDoesNotExist() {
			// Given
			Long courseId = 109L;
			Long topicId = 206L;
			// When
			TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
					() -> domainValidationService.ensureTopicBelongToCourse(topicId, courseId));

			// Then
			assertEquals(EntityDomain.TOPIC, ex.getAffectedEntity());
			assertEquals(topicId, ex.getMissingValue());
			verify(topicRepository).findById(topicId);
		}
	}

	@Nested
	@DisplayName("findCourseWithAccess tests")
	class FindCourseWithAccessTests {

		@Test
		@DisplayName("Should return course when user has access")
		void shouldReturnCourseWhenUserHasAccess() {
			// Given
			Long courseId = 110L;
			AuthUser authUser = mock(AuthUser.class);
			Course course = createCourse(courseId);

			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			doNothing().when(userAuthorizationService).ensureHasCourseAccess(authUser, course);

			// When
			Course result = domainValidationService.findCourseWithAccess(authUser, courseId);

			// Then
			assertEquals(course, result);
			assertEquals(courseId, result.getId());
			verify(courseRepository).findById(courseId);
			verify(userAuthorizationService).ensureHasCourseAccess(authUser, course);
		}

		@Test
		@DisplayName("Should throw when course does not exist")
		void shouldThrowWhenCourseDoesNotExist() {
			// Given
			Long courseId = 111L;
			AuthUser authUser = mock(AuthUser.class);
			// When + Then
			assertThrows(CourseNotFoundException.class,
					() -> domainValidationService.findCourseWithAccess(authUser, courseId));
		}

		@Test
		@DisplayName("Should throw when user does not have access")
		void shouldThrowWhenUserDoesNotHaveAccess() {
			// Given
			Long courseId = 112L;
			Course course = mock(Course.class);
			AuthUser authUser = mock(AuthUser.class);

			when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
			doThrow(new AccessDeniedException("No access")).when(userAuthorizationService)
					.ensureHasCourseAccess(authUser, course);

			// When + Then
			assertThrows(AccessDeniedException.class, () -> {
				domainValidationService.findCourseWithAccess(authUser, courseId);
			});
			verify(courseRepository).findById(courseId);
			verify(userAuthorizationService).ensureHasCourseAccess(authUser, course);
		}
	}
	
	@Nested
	@DisplayName("findTopicInCourse tests")
	class FindTopicInCourseTests {

		@Test
		@DisplayName("Should return topic when topic belongs to course")
		void shouldReturnTopicWhenTopicBelongsToCourse() {
			// Given
			Long courseId = 113L;
			Long topicId = 213L;
			Topic topic = createTopic(topicId, courseId);
			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

			// When
			Topic result = domainValidationService.findTopicInCourse(topicId, courseId);

			// Then
			assertEquals(topic, result);
			verify(topicRepository).findById(topicId);
		}

		@Test
		@DisplayName("Should throw TopicDoesNotBelongToCourseException when topic belongs to different course")
		void shouldThrowTopicDoesNotBelongToCourseExceptionWhenTopicBelongsToDifferentCourse() {
			// Given
			Long paramCourseId = 114L;
			Long realCourseId = 160L;
			Long topicId = 214L;
			Topic topic = createTopic(topicId, realCourseId);
			when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

			// When
			TopicDoesNotBelongToCourseException ex = assertThrows(TopicDoesNotBelongToCourseException.class,
					() -> domainValidationService.findTopicInCourse(topicId, paramCourseId));

			// Then
			assertEquals(EntityDomain.TOPIC, ex.getAffectedEntity());
			assertEquals(EntityDomain.COURSE, ex.getParentEntity());
			verify(topicRepository).findById(topicId);
		}

		@Test
		@DisplayName("Should throw TopicNotFoundException when topic does not exist")
		void shouldThrowTopicNotFoundExceptionWhenTopicDoesNotExist() {
			// Given
			Long courseId = 115L;
			Long topicId = 215L;
			// When
			TopicNotFoundException ex = assertThrows(TopicNotFoundException.class,
					() -> domainValidationService.findTopicInCourse(topicId, courseId));

			// Then
			assertEquals(EntityDomain.TOPIC, ex.getAffectedEntity());
			assertEquals(topicId, ex.getMissingValue());
			verify(topicRepository).findById(topicId);
		}
	}

	@Nested
	@DisplayName("findReplyInTopic tests")
	class FindReplyInTopicTests {

		@Test
		@DisplayName("Should return reply when reply belongs to topic")
		void shouldReturnReplyWhenReplyBelongsToTopic() {
			// Given
			Long replyId = 316L;
			Long topicId = 216L;
			Reply reply = createReplyWithFlatTopic(replyId, topicId);
			when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));

			// When
			Reply result = domainValidationService.findReplyInTopic(replyId, topicId);

			// Then
			assertEquals(reply, result);
			verify(replyRepository).findById(replyId);
		}

		@Test
		@DisplayName("Should throw ReplyDoesNotBelongToTopicException when reply belongs to different topic")
		void shouldThrowReplyDoesNotBelongToTopicExceptionWhenReplyBelongsToDifferentTopic() {
			// Given
			Long paramTopicId = 299L;
			Long realTopicId = 217L;
			Long replyId = 317L;
			Reply reply = mock(Reply.class);
			Topic topic = mock(Topic.class);
			when(reply.getTopic()).thenReturn(topic);
			when(topic.getId()).thenReturn(realTopicId);
			when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
			// When
			ReplyDoesNotBelongToTopicException ex = assertThrows(ReplyDoesNotBelongToTopicException.class,
					() -> domainValidationService.findReplyInTopic(replyId, paramTopicId));

			// Then
			assertEquals(EntityDomain.REPLY, ex.getAffectedEntity());
			assertEquals(EntityDomain.TOPIC, ex.getParentEntity());
			verify(replyRepository).findById(replyId);
		}

		@Test
		@DisplayName("Should throw ReplyNotFoundException when reply does not exist")
		void shouldThrowReplyNotFoundExceptionWhenReplyDoesNotExist() {
			// Given
			Long topicId = 218L;
			Long replyId = 318L;
			// When
			ReplyNotFoundException ex = assertThrows(ReplyNotFoundException.class,
					() -> domainValidationService.findReplyInTopic(replyId, topicId));

			// Then
			assertEquals(EntityDomain.REPLY, ex.getAffectedEntity());
			assertEquals(replyId, ex.getMissingValue());
			verify(replyRepository).findById(replyId);
		}
	}

	@Nested
	@DisplayName("findParentReplyInTopic tests")
	class FindParentReplyInTopicTests {

		@Test
		@DisplayName("Should return null when parentId is null")
		void shouldReturnNullWhenParentIdIsNull() {
			// Given
			Long parentReplyId = null;
			Long topicId = 219L;

			// When
			Reply result = domainValidationService.findParentReplyInTopic(parentReplyId, topicId);

			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should return reply when parent reply belongs to topic")
		void shouldReturnReplyWhenParentReplyBelongsToTopic() {
			// Given
			Long parentReplyId = 320L;
			Long topicId = 220L;
			Reply reply = createReplyWithFlatTopic(parentReplyId, topicId);
			when(replyRepository.findById(parentReplyId)).thenReturn(Optional.of(reply));

			// When
			Reply result = domainValidationService.findParentReplyInTopic(parentReplyId, topicId);

			// Then
			assertEquals(reply, result);
			verify(replyRepository).findById(parentReplyId);
		}

		@Test
		@DisplayName("Should throw ParentReplyDoesNotBelongToTopicException when parent reply belongs to different topic")
		void shouldThrowParentReplyDoesNotBelongToTopicExceptionWhenParentReplyBelongsToDifferentTopic() {
			// Given
			Long realTopicId = 221L;
			Long paramTopicId = 299L;
			Long parentReplyId = 321L;
			Reply parentReply = mock(Reply.class);
			Topic topic = mock(Topic.class);
			when(topic.getId()).thenReturn(realTopicId);
			when(parentReply.getTopic()).thenReturn(topic);
			when(replyRepository.findById(parentReplyId)).thenReturn(Optional.of(parentReply));
			// When
			ParentReplyDoesNotBelongToTopicException ex = assertThrows(ParentReplyDoesNotBelongToTopicException.class,
					() -> domainValidationService.findParentReplyInTopic(parentReplyId, paramTopicId));
			// Then
			assertEquals(EntityDomain.PARENT_REPLY, ex.getAffectedEntity());
			assertEquals(EntityDomain.TOPIC, ex.getParentEntity());
			verify(replyRepository).findById(parentReplyId);
		}

		@Test
		@DisplayName("Should throw ReplyNotFoundException when parent reply does not exist")
		void shouldThrowReplyNotFoundExceptionWhenParentReplyDoesNotExist() {
			// Given
			Long topicId = 222L;
			Long parentReplyId = 322L;
			// When
			ReplyNotFoundException ex = assertThrows(ReplyNotFoundException.class,
					() -> domainValidationService.findParentReplyInTopic(parentReplyId, topicId));

			// Then
			assertEquals(EntityDomain.REPLY, ex.getAffectedEntity());
			assertEquals(parentReplyId, ex.getMissingValue());
			verify(replyRepository).findById(parentReplyId);
		}
	}
}