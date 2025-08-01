package uno.lode.ScholarTopicBoard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.role.Role;
import uno.lode.ScholarTopicBoard.domain.role.dto.RolePublicResponseDTO;
import uno.lode.ScholarTopicBoard.domain.shared.Authorable;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;
import uno.lode.ScholarTopicBoard.infra.exception.user.FakeUserNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@Service
public class UserAuthorizationService {

	@Autowired
	UserRepository userRepository;
	
	void ensureCanAccessAuthorable(AuthUser authUser, Authorable authorable) {
		if( !(isAdminOrModerator(authUser) || authorable.isAuthoredBy(authUser.getId())) ){
			throw new AccessDeniedException("Access denied!");
		}
	}
	
	void ensureCanBan(AuthUser authUser, User targetUser) {
		// Enforces business-specific ban rules beyond role-based controller
		// restrictions
		
		// Only admin or moderator can ban users
		if(!authUser.isAdmin() && !authUser.isModerator()) {
			throw new AccessDeniedException("Access denied!");
		}
		
		// Users cannot ban themselves
		if(authUser.getId().equals(targetUser.getId())) {
		    throw new AccessDeniedException("Cannot ban yourself!");
		}

		// Admin restrictions:
		// - Can ban any user except other admins
		if(authUser.isAdmin()) {
	        if(hasRole(targetUser, RoleConstants.ADMIN)) {
	            throw new AccessDeniedException("Admins cannot ban other admins!");
	        }
	        return;
	    }
		
		// Moderator restriction:
		// - Cannot ban admins, other moderators, or coordinators
		// - Can only ban users enrolled in a shared course
	    if(authUser.isModerator()) {
	        // Can't ban admins, moderators, o coordinators
	        if(hasRole(targetUser, RoleConstants.ADMIN) || 
	           hasRole(targetUser, RoleConstants.MODERATOR) ||
	           hasRole(targetUser, RoleConstants.COORD)) {
	            throw new AccessDeniedException("Moderators can only ban regular users!");
	        }
	        
	        // Must share course
	        if(!isPartner(authUser, targetUser)) {
	            throw new AccessDeniedException("Moderators can only ban users from shared courses!");
	        }
	        return;
	    }
	}
	
	void ensureCanViewUser(AuthUser authUser, User requestedUser) {
		if(isAdminOrCoordinator(authUser)) return;
		if (!isPartner(authUser, requestedUser)) {
			throw new FakeUserNotFoundException(requestedUser.getId());
		}
	}
	
	void ensureHasCourseAccess(AuthUser authUser, Course course) {
		if(isAdminOrCoordinator(authUser)) return;
		boolean isEnrolled = userRepository.existsByIdAndCoursesIdAndDeletedFalse(authUser.getId(), course.getId());
		if (!isEnrolled) {
			throw new AccessDeniedException("Access denied!");
		}
	}
	
	public List<RolePublicResponseDTO> getVisibleRoles(User user, AuthUser authUser) {
		if (user.getRoles() == null)
			return List.of();

		return user.getRoles().stream().filter(role -> canViewRole(role, authUser))
				.map(RolePublicResponseDTO::new).toList();
	}
	
	private boolean canViewRole(Role role, AuthUser authUser) {
        return authUser.isAdmin() || role.getIsPublic();
    }
	
	private boolean hasRole(User user, String roleName) {
	    return userRepository.hasRole(user.getId(), roleName);
	}
	
	private boolean isAdminOrCoordinator(AuthUser authUser) {
		return (authUser.isAdmin() || authUser.isCoord());
	}
	
	private boolean isAdminOrModerator(AuthUser authUser) {
		return (authUser.isAdmin() || authUser.isModerator());
	}
	
	private boolean isPartner(AuthUser authUser, User user) {
		return userRepository.sharesCoursesWith(authUser.getId(), user.getId());
	}
}
