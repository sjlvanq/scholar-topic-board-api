package uno.lode.ScholarTopicBoard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.role.Role;
import uno.lode.ScholarTopicBoard.domain.role.dto.RolePublicResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;
import uno.lode.ScholarTopicBoard.infra.exception.user.FakeUserNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAuthorizationService authorizationService;

    // Helper methods para crear objetos mock
    private AuthUser createAuthUser(Long userId, boolean isAdmin, boolean isCoord, boolean isModerator) {
        AuthUser authUser = mock(AuthUser.class);
        lenient().when(authUser.getId()).thenReturn(userId);
        lenient().when(authUser.isAdmin()).thenReturn(isAdmin);
        lenient().when(authUser.isCoord()).thenReturn(isCoord);
        lenient().when(authUser.isModerator()).thenReturn(isModerator);
        return authUser;
    }

    private User createUser(Long userId) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        return user;
    }
    
    private Course createCourse(Long courseId) {
    	Course course = mock(Course.class);
    	when(course.getId()).thenReturn(courseId);
    	return course;
    }

    @Nested
    @DisplayName("ensureCanAccessAuthorable tests")
    class EnsureCanAccessAuthorable {

        @Test
        @DisplayName("Should allow admin to access any authorable")
        void shouldAllowAdminToAccessAnyAuthorable() {
            // Given
        	AuthUser authUser = createAuthUser(1L, true, false, false);
        	Topic topic = mock(Topic.class);
        	// When + Then
        	assertDoesNotThrow(() -> authorizationService.ensureCanAccessAuthorable(authUser, topic));
        }

        @Test
        @DisplayName("Should allow moderator to access any authorable")
        void shouldAllowModeratorToAccessAnyAuthorable() {
            // Given
        	AuthUser authUser = createAuthUser(2L, false, false, true);
        	Topic topic = mock(Topic.class);
        	// When + Then
        	assertDoesNotThrow(() -> authorizationService.ensureCanAccessAuthorable(authUser, topic));
        }

        @Test
        @DisplayName("Should allow author to access their own authorable")
        void shouldAllowAuthorToAccessTheirOwnAuthorable() {
            // Given
        	AuthUser authUser = createAuthUser(3L, false, false, false);
        	Topic topic = mock(Topic.class);
        	when(topic.isAuthoredBy(3L)).thenReturn(true);
        	// When + Then
        	assertDoesNotThrow(() -> authorizationService.ensureCanAccessAuthorable(authUser, topic));
        }

        @Test
        @DisplayName("Should deny access when user is not admin, moderator, or author")
        void shouldDenyAccessWhenUserIsNotAdminModeratorOrAuthor() {
            // Given
        	AuthUser authUser = createAuthUser(4L, false, false, false);
        	Topic topic = mock(Topic.class);
        	when(topic.isAuthoredBy(4L)).thenReturn(false);
        	// When + Then
        	assertThrows(AccessDeniedException.class,
    				() -> authorizationService.ensureCanAccessAuthorable(authUser, topic));
        }
    }

    @Nested
    @DisplayName("ensureCanBan tests")
    class EnsureCanBanTests {

        @Test
        @DisplayName("Should prevent user from banning themselves")
        void shouldPreventSelfBan() {
        	// Given
        	AuthUser authUser = createAuthUser(51L, true, false, false);
        	User targetUser = createUser(51L);
        	// When + Then
        	assertThrows(AccessDeniedException.class,
    				() -> authorizationService.ensureCanBan(authUser, targetUser));
        }

        @Test
        @DisplayName("Should deny access when user is not admin or moderator")
        void shouldDenyAccessWhenUserIsNotAdminOrModerator() {
        	// Given
        	AuthUser authUser = createAuthUser(52L, false, false, false);
        	User targetUser = mock(User.class);
            // When + Then
        	assertThrows(AccessDeniedException.class,
    				() -> authorizationService.ensureCanBan(authUser, targetUser));
        }

        @Nested
        @DisplayName("Admin ban tests")
        class AdminBanTests {

            @Test
            @DisplayName("Should allow admin to ban regular user")
            void shouldAllowAdminToBanRegularUser() {
                // Given
            	AuthUser authUser = createAuthUser(53L, true, false, false);
            	User targetUser = createUser(54L);
            	// When + Then
            	assertDoesNotThrow(() -> authorizationService.ensureCanBan(authUser, targetUser));
            }

            @Test
            @DisplayName("Should allow admin to ban moderator")
            void shouldAllowAdminToBanModerator() {
                // Given
            	AuthUser authUser = createAuthUser(55L, true, false, false);
            	User targetUser = createUser(56L);
            	// When + Then
            	assertDoesNotThrow(() -> authorizationService.ensureCanBan(authUser, targetUser));
            }

            @Test
            @DisplayName("Should allow admin to ban coordinator")
            void shouldAllowAdminToBanCoordinator() {
                // Given
            	AuthUser authUser = createAuthUser(57L, true, false, false);
            	User targetUser = createUser(58L);
            	// When + Then
            	assertDoesNotThrow(() -> authorizationService.ensureCanBan(authUser, targetUser));
            }

            @Test
            @DisplayName("Should prevent admin from banning other admin")
            void shouldPreventAdminFromBanningOtherAdmin() {
            	// Given
            	AuthUser authUser = createAuthUser(59L, true, false, false);
            	User targetUser = createUser(60L);
            	// When + Then
                when(userRepository.hasRole(targetUser.getId(), RoleConstants.ADMIN)).thenReturn(true);

            	assertThrows(AccessDeniedException.class,
        				() -> authorizationService.ensureCanBan(authUser, targetUser));
            }
        }

        @Nested
        @DisplayName("Moderator ban tests")
        class ModeratorBanTests {

            @Test
            @DisplayName("Should allow moderator to ban regular user from shared course")
            void shouldAllowModeratorToBanRegularUserFromSharedCourse() {
				// Given
				AuthUser authUser = createAuthUser(101L, false, false, true);
				User targetUser = createUser(102L);
            	// When + Then
				when(userRepository.sharesCoursesWith(101L, 102L)).thenReturn(true);
				assertDoesNotThrow(() -> authorizationService.ensureCanBan(authUser, targetUser));
            }

            @Test
            @DisplayName("Should prevent moderator from banning admin")
            void shouldPreventModeratorFromBanningAdmin() {
				// Given
				AuthUser authUser = createAuthUser(103L, false, false, true);
				User targetUser = createUser(104L);
				// When + Then
				when(userRepository.hasRole(104L, RoleConstants.ADMIN)).thenReturn(true);
				assertThrows(AccessDeniedException.class,
						() -> authorizationService.ensureCanBan(authUser, targetUser));
            }

            @Test
            @DisplayName("Should prevent moderator from banning other moderator")
            void shouldPreventModeratorFromBanningOtherModerator() {
				// Given
				AuthUser authUser = createAuthUser(105L, false, false, true);
				User targetUser = createUser(106L);
				when(userRepository.hasRole(106L, RoleConstants.ADMIN)).thenReturn(false);
				when(userRepository.hasRole(106L, RoleConstants.MODERATOR)).thenReturn(true);

				// When + Then
				assertThrows(AccessDeniedException.class,
						() -> authorizationService.ensureCanBan(authUser, targetUser));
            }

            @Test
            @DisplayName("Should prevent moderator from banning coordinator")
            void shouldPreventModeratorFromBanningCoordinator() {
				// Given
				AuthUser authUser = createAuthUser(107L, false, false, true);
				User targetUser = createUser(108L);
				when(userRepository.hasRole(108L, RoleConstants.ADMIN)).thenReturn(false);
				when(userRepository.hasRole(108L, RoleConstants.COORD)).thenReturn(true);
				when(userRepository.hasRole(108L, RoleConstants.MODERATOR)).thenReturn(false);

				// When + Then
				assertThrows(AccessDeniedException.class,
						() -> authorizationService.ensureCanBan(authUser, targetUser));
            }

            @Test
            @DisplayName("Should prevent moderator from banning user from different course")
            void shouldPreventModeratorFromBanningUserFromDifferentCourse() {
				// Given
				AuthUser authUser = createAuthUser(109L, false, false, true);
				User targetUser = createUser(110L);
				lenient().when(userRepository.sharesCoursesWith(109L, 110L)).thenReturn(false);
				
				// When + Then
				assertThrows(AccessDeniedException.class,
						() -> authorizationService.ensureCanBan(authUser, targetUser));
            }
        }
    }

    @Nested
    @DisplayName("ensureCanViewUser tests")
    class EnsureCanViewUserTests {

        @Test
        @DisplayName("Should allow admin to view any user")
        void shouldAllowAdminToViewAnyUser() {
			// Given
			AuthUser authUser = createAuthUser(201L, true, false, false);
			User targetUser = mock(User.class);
			// When + Then
        	assertDoesNotThrow(() -> authorizationService.ensureCanViewUser(authUser, targetUser));
        }

        @Test
        @DisplayName("Should allow coordinator to view any user")
        void shouldAllowCoordinatorToViewAnyUser() {
        	// Given
        	AuthUser authUser = createAuthUser(202L, false, true, false);
			User targetUser = mock(User.class);
			// When + Then
        	assertDoesNotThrow(() -> authorizationService.ensureCanViewUser(authUser, targetUser));
        }

        @Test
        @DisplayName("Should allow user to view partner from shared course")
        void shouldAllowUserToViewPartnerFromSharedCourse() {
        	// Given
        	AuthUser authUser = createAuthUser(203L, false, false, false);
			User targetUser = createUser(204L);
			when(userRepository.sharesCoursesWith(203L, 204L)).thenReturn(true);
			
			// When + Then
			assertDoesNotThrow(() -> authorizationService.ensureCanViewUser(authUser, targetUser));
        }

        @Test
        @DisplayName("Should prevent user from viewing non-partner")
        void shouldPreventUserFromViewingNonPartner() {
        	// Given
        	AuthUser authUser = createAuthUser(205L, false, false, false);
			User targetUser = createUser(206L);
			lenient().when(userRepository.sharesCoursesWith(205L, 206L)).thenReturn(false);
			
			// When + Then
			assertThrows(FakeUserNotFoundException.class,
					() -> authorizationService.ensureCanViewUser(authUser, targetUser));
        }
    }

    @Nested
    @DisplayName("ensureHasCourseAccess tests")
    class EnsureHasCourseAccessTests {

        @Test
        @DisplayName("Should allow admin to access any course")
        void shouldAllowAdminToAccessAnyCourse() {
        	// Given
        	AuthUser authUser = createAuthUser(301L, true, false, false);
        	Course course = mock(Course.class);
        	
        	// When + Then
        	assertDoesNotThrow(() -> authorizationService.ensureHasCourseAccess(authUser, course));
        	verify(userRepository, never()).existsByIdAndCoursesIdAndDeletedFalse(eq(301L), any());
        }

        @Test
        @DisplayName("Should allow coordinator to access any course")
        void shouldAllowCoordinatorToAccessAnyCourse() {
        	// Given
        	AuthUser authUser = createAuthUser(302L, false, true, false);
        	Course course = mock(Course.class);
        	
        	// When + Then
        	assertDoesNotThrow(() -> authorizationService.ensureHasCourseAccess(authUser, course));
        	verify(userRepository, never()).existsByIdAndCoursesIdAndDeletedFalse(eq(302L), any());
        }

        @Test
        @DisplayName("Should allow enrolled user to access course")
        void shouldAllowEnrolledUserToAccessCourse() {
        	// Given
        	AuthUser authUser = createAuthUser(303L, false, false, false);
        	Course course = createCourse(1053L);
        	
        	// When + Then
        	when(userRepository.existsByIdAndCoursesIdAndDeletedFalse(303L, 1053L)).thenReturn(true);
        	assertDoesNotThrow(() -> authorizationService.ensureHasCourseAccess(authUser, course));
        }

        @Test
        @DisplayName("Should prevent non-enrolled user from accessing course")
        void shouldPreventNonEnrolledUserFromAccessingCourse() {
        	// Given
        	AuthUser authUser = createAuthUser(304L, false, false, false);
        	Course course = createCourse(1054L);
        	
        	// When + Then
        	assertThrows(AccessDeniedException.class, 
        			() -> authorizationService.ensureHasCourseAccess(authUser, course));
        }
    }

    @Nested
    @DisplayName("getVisibleRoles tests")
    class GetVisibleRolesTests {

    	private Role createRole(Long id, boolean isPublic) {
    		Role role = mock(Role.class);
    		lenient().when(role.getId()).thenReturn(id);
    		lenient().when(role.getIsPublic()).thenReturn(isPublic);
    		return role;
    	}
    	
        @Test
        @DisplayName("Should return empty list when user has no roles")
        void shouldReturnEmptyListWhenUserHasNoRoles() {
			// Given
        	User user = mock(User.class);
			when(user.getRoles()).thenReturn(null);
			AuthUser authUser = createAuthUser(401L, true, false, false);
			
			// When
			List<RolePublicResponseDTO> result = authorizationService.getVisibleRoles(user, authUser);

			// Then
			assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return all roles when requesting user is admin")
        void shouldReturnAllRolesWhenRequestingUserIsAdmin() {
            // Given
        	AuthUser authUser = createAuthUser(402L, true, false, false);
            User targetUser = mock(User.class);
            Role role1 = createRole(2001L, false);
            Role role2 = createRole(2002L, true);
            when(targetUser.getRoles()).thenReturn(List.of(role1, role2));
            
            // When
            List<RolePublicResponseDTO> result = authorizationService.getVisibleRoles(targetUser, authUser);
            
            // Then
            assertThat(result).hasSize(2).extracting("id").containsExactly(2001L, 2002L);
        }

        @Test
        @DisplayName("Should return only public roles when requesting user is not admin")
		void shouldReturnOnlyPublicRolesWhenRequestingUserIsNotAdmin() {
			// Given
        	AuthUser authUser = createAuthUser(403L, false, false, false);
            User targetUser = mock(User.class);
            Role role1 = createRole(2003L, false);
            Role role2 = createRole(2004L, true);
            when(targetUser.getRoles()).thenReturn(List.of(role1, role2));

            // When
            List<RolePublicResponseDTO> result = authorizationService.getVisibleRoles(targetUser, authUser);

            // Then
            assertThat(result).hasSize(1).extracting("id").containsExactly(2004L);
		}
    }
}