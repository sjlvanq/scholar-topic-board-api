package uno.lode.ScholarTopicBoard.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.reply.Reply;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@ExtendWith(MockitoExtension.class)
class ServiceUtilUnitTest {

	@Mock
	private CourseRepository courseRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private ServiceUtil serviceUtil;

	@Test
	@DisplayName("checkAdminModeratorOrAuthor: should allow admin operation on authorable (topic)")
	void checkAdminModeratorOrAuthor_allowsAdminOnTopic() {
		AuthUser admin = Mockito.mock(AuthUser.class);
		Topic topic = Mockito.mock(Topic.class);
		
		when(admin.isAdmin()).thenReturn(true);

		assertDoesNotThrow(() -> serviceUtil.checkAdminModeratorOrAuthor(admin, topic));
		
		verifyNoInteractions(courseRepository);
	}

	@Test
	@DisplayName("checkAdminModeratorOrAuthor: should allow admin operation on authorable (reply)")
	void checkAdminModeratorOrAuthor_allowsAdminOnReply() {
		AuthUser admin = Mockito.mock(AuthUser.class);
		Reply reply = Mockito.mock(Reply.class);
		
		when(admin.isAdmin()).thenReturn(true);

		assertDoesNotThrow(() -> serviceUtil.checkAdminModeratorOrAuthor(admin, reply));
		
		verifyNoInteractions(courseRepository);
	}
	
	@Test
	@DisplayName("checkAdminModeratorOrAuthor: should allow moderator operation on authorable (topic)")
	void checkAdminModeratorOrAuthor_allowsModeratorOnTopic() {
		AuthUser moderator = Mockito.mock(AuthUser.class);
		Topic topic = Mockito.mock(Topic.class);
		
		when(moderator.isModerator()).thenReturn(true);

		assertDoesNotThrow(() -> serviceUtil.checkAdminModeratorOrAuthor(moderator, topic));
		
		verifyNoInteractions(courseRepository);
	}
	
	@Test
	@DisplayName("checkAdminModeratorOrAuthor: should allow moderator operation on authorable (reply)")
	void checkAdminModeratorOrAuthor_allowsModeratorOnReply() {
		AuthUser moderator = Mockito.mock(AuthUser.class);
		Reply reply = Mockito.mock(Reply.class);
		
		when(moderator.isModerator()).thenReturn(true);

		assertDoesNotThrow(() -> serviceUtil.checkAdminModeratorOrAuthor(moderator, reply));
		
		verifyNoInteractions(courseRepository);
	}
	
	@Test
	@DisplayName("checkAdminModeratorOrAuthor: should allow author operation on authorable (topic)")
	void checkAdminModeratorOrAuthor_allowsAuthorOnTopic() {
		Long authorId = 13L;
		AuthUser author = Mockito.mock(AuthUser.class);
		Topic topic = Mockito.mock(Topic.class);
		
		when(author.isAdmin()).thenReturn(false);
		when(author.isModerator()).thenReturn(false);
		when(author.getId()).thenReturn(authorId);
		when(topic.isAuthoredBy(authorId)).thenReturn(true);

		assertDoesNotThrow(() -> serviceUtil.checkAdminModeratorOrAuthor(author, topic));
		
		verifyNoInteractions(courseRepository);
	}
	
	@Test
	@DisplayName("checkAdminModeratorOrAuthor: should allow author operation on authorable (reply)")
	void checkAdminModeratorOrAuthor_allowsAuthorOnReply() {
		Long authorId = 14L;
		AuthUser author = Mockito.mock(AuthUser.class);
		Reply reply = Mockito.mock(Reply.class);
		
		when(author.isAdmin()).thenReturn(false);
		when(author.isModerator()).thenReturn(false);
		when(author.getId()).thenReturn(authorId);
		when(reply.isAuthoredBy(authorId)).thenReturn(true);

		assertDoesNotThrow(() -> serviceUtil.checkAdminModeratorOrAuthor(author, reply));
		
		verifyNoInteractions(courseRepository);
	}
	
	@Test
	@DisplayName("checkAdminModeratorOrAuthor: should deny access when not admin, coordinator, or author on authorable (topic)")
	void checkAdminModeratorOrAuthor_deniesAccessOnTopic() {
		Long userId = 15L;
		AuthUser user = Mockito.mock(AuthUser.class);
		Topic topic = Mockito.mock(Topic.class);

		when(user.isAdmin()).thenReturn(false);
		when(user.isModerator()).thenReturn(false);
		when(user.getId()).thenReturn(userId);
		when(topic.isAuthoredBy(userId)).thenReturn(false);
		
		AccessDeniedException exception = assertThrows(
				AccessDeniedException.class, 
				() -> serviceUtil.checkAdminModeratorOrAuthor(user, topic)
		);
		
		assertEquals("Access denied!", exception.getMessage());
	}

	@Test
	@DisplayName("checkAdminModeratorOrAuthor: should deny access when not admin, coordinator, or author on authorable (reply)")
	void checkAdminModeratorOrAuthor_deniesAccessOnReply() {
		Long userId = 16L;
		AuthUser user = Mockito.mock(AuthUser.class);
		Reply reply = Mockito.mock(Reply.class);

		when(user.isAdmin()).thenReturn(false);
		when(user.isModerator()).thenReturn(false);
		when(user.getId()).thenReturn(userId);
		when(reply.isAuthoredBy(userId)).thenReturn(false);
		
		AccessDeniedException exception = assertThrows(
				AccessDeniedException.class, 
				() -> serviceUtil.checkAdminModeratorOrAuthor(user, reply)
		);
		
		assertEquals("Access denied!", exception.getMessage());
	}
	
	@Test
	@DisplayName("checkAdminCoordinatorOrEnrolled: should allow access when user is admin")
	void checkAdminCoordinatorOrEnrolled_allowsAdmin() {
		Long adminId = 1L;
		//Long courseId = 21L;
		AuthUser admin = Mockito.mock(AuthUser.class);
		Course course = Mockito.mock(Course.class);
		
		when(admin.isAdmin()).thenReturn(true);
		//when(admin.isCoord()).thenReturn(false);
		//when(course.getId()).thenReturn(courseId);

		assertDoesNotThrow(() -> serviceUtil.checkAdminCoordinatorOrEnrolled(admin, course));
		
		verifyNoInteractions(courseRepository);
	}

	@Test
	@DisplayName("checkAdminCoordinatorOrEnrolled: should allow access when user is coordinator")
	void checkAdminCoordinatorOrEnrolled_allowsCoordinator() {
		Long coordId = 2L;
		//Long courseId = 22L;
		AuthUser coord = Mockito.mock(AuthUser.class);
		Course course = Mockito.mock(Course.class);
		
		when(coord.isAdmin()).thenReturn(false);
		when(coord.isCoord()).thenReturn(true);
		//when(course.getId()).thenReturn(courseId);
		
		assertDoesNotThrow(() -> serviceUtil.checkAdminCoordinatorOrEnrolled(coord, course));
		
		verifyNoInteractions(courseRepository);
	}
	
	@Test
	@DisplayName("checkAdminCoordinatorOrEnrolled: should allow access when user is enrolled in course")
	void checkAdminCoordinatorOrEnrolled_allowsEnrolled() {
		Long userId = 3L;
		Long courseId = 23L;
		AuthUser userAuth = Mockito.mock(AuthUser.class);
		Course course = Mockito.mock(Course.class);
		
		when(userAuth.getId()).thenReturn(userId);
		when(userAuth.isAdmin()).thenReturn(false);
		when(userAuth.isCoord()).thenReturn(false);
		when(course.getId()).thenReturn(courseId);
		
		when(userRepository.existsByIdAndCoursesIdAndDeletedFalse(userId, courseId)).thenReturn(true);
		
		assertDoesNotThrow(() -> serviceUtil.checkAdminCoordinatorOrEnrolled(userAuth, course));

		verify(userRepository).existsByIdAndCoursesIdAndDeletedFalse(userId, courseId);
	}

	@Test
	@DisplayName("checkAdminCoordinatorOrEnrolled: should deny access when user is not admin, coordinator, or enrolled")
	void checkAdminCoordinatorOrEnrolled_deniesAccess() {
		Long userId = 4L;
		Long courseId = 24L;
		AuthUser authUser = Mockito.mock(AuthUser.class);
		Course course = Mockito.mock(Course.class);
		
		when(authUser.getId()).thenReturn(userId);
		when(authUser.isAdmin()).thenReturn(false);
		when(authUser.isCoord()).thenReturn(false);
		when(course.getId()).thenReturn(courseId);
		
		when(userRepository.existsByIdAndCoursesIdAndDeletedFalse(userId, courseId)).thenReturn(false);

		AccessDeniedException exception = assertThrows(
				AccessDeniedException.class, 
				() -> serviceUtil.checkAdminCoordinatorOrEnrolled(authUser, course)
		);
		
		assertEquals("Access denied!", exception.getMessage());

		verify(userRepository).existsByIdAndCoursesIdAndDeletedFalse(userId, courseId);
	}
	
	@Test
	void shouldReturnTrueWhenUserIsAdmin() {
		AuthUser authUser = Mockito.mock(AuthUser.class);
		Long targetUserId = 31L;
	    when(authUser.isAdmin()).thenReturn(true);
	    assertTrue(serviceUtil.isAdminCoordinatorOrHasSharedCourse(authUser, targetUserId));
	}

	@Test
	void shouldReturnTrueWhenUserIsCoordinator() {
		AuthUser authUser = Mockito.mock(AuthUser.class);
		Long targetUserId = 32L;
		when(authUser.isCoord()).thenReturn(true);
	    assertTrue(serviceUtil.isAdminCoordinatorOrHasSharedCourse(authUser, targetUserId));
	}

	@Test
	void shouldReturnTrueWhenUsersShareCourse() {
		AuthUser authUser = Mockito.mock(AuthUser.class);
		Long authUserId = 5L;
		Long targetUserId = 33L;
	    when(authUser.isAdmin()).thenReturn(false);
	    when(authUser.isCoord()).thenReturn(false);
	    when(authUser.getId()).thenReturn(authUserId);
	    when(userRepository.sharesCoursesWith(authUserId, targetUserId)).thenReturn(true);
	    assertTrue(serviceUtil.isAdminCoordinatorOrHasSharedCourse(authUser, targetUserId));
	    
	    verify(userRepository).sharesCoursesWith(authUserId, targetUserId);
	}
}
