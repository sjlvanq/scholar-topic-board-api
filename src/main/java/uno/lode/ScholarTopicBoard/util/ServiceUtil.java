package uno.lode.ScholarTopicBoard.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.shared.Authorable;
import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@Component
public class ServiceUtil {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CourseRepository courseRepository;

	public void checkAdminCoordinatorOrEnrolled(AuthUser authUser, Course course) {
		if (authUser.isAdmin() || authUser.isCoord())
			return; // Ok

		boolean isEnrolled = userRepository.existsByIdAndCoursesIdAndDeletedFalse(authUser.getId(), course.getId());
		if (!isEnrolled) {
			throw new AccessDeniedException("Access denied!");
		}
	}

	public void checkAdminModeratorOrAuthor(AuthUser authUser, Authorable authorable) {
		if( !(authUser.isAdmin() || authUser.isModerator() || authorable.isAuthoredBy(authUser.getId())) ){
			throw new AccessDeniedException("Access denied!");
		}
	}
	
	public boolean isAdminCoordinatorOrHasSharedCourse(AuthUser authUser, Long targetUserId) { // TODO: Tests
		if (authUser.isAdmin() || authUser.isCoord()) {
			return true;
		}
		
		return userRepository.sharesCoursesWith(authUser.getId(), targetUserId);
	}

}
