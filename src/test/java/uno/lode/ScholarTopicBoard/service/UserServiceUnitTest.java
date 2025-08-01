package uno.lode.ScholarTopicBoard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.role.Role;
import uno.lode.ScholarTopicBoard.domain.role.RoleRepository;
import uno.lode.ScholarTopicBoard.domain.role.dto.RolePublicResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.domain.user.UserListFilter;
import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserBanStatusUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserCoursesUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserDetailDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserListItemDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRolesUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.role.RoleNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.user.UserAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.user.UserNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private UserAuthorizationService authorizationService;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // Helper methods
    private AuthUser createAuthUser(Long userId, boolean isAdmin, boolean isCoord, boolean isModerator) {
        AuthUser authUser = mock(AuthUser.class);
        lenient().when(authUser.getId()).thenReturn(userId);
        lenient().when(authUser.isAdmin()).thenReturn(isAdmin);
        lenient().when(authUser.isCoord()).thenReturn(isCoord);
        lenient().when(authUser.isModerator()).thenReturn(isModerator);
        return authUser;
    }

    private User createUser(Long userId, String firstName, String lastName, String email, boolean deleted) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(userId);
        lenient().when(user.getFirstName()).thenReturn(firstName);
        lenient().when(user.getLastName()).thenReturn(lastName);
        lenient().when(user.getEmail()).thenReturn(email);
        lenient().when(user.isDeleted()).thenReturn(deleted);
        return user;
    }

    private Course createCourse(Long courseId, String name) {
        Course course = mock(Course.class);
        lenient().when(course.getId()).thenReturn(courseId);
        lenient().when(course.getName()).thenReturn(name);
        return course;
    }

    private Role createRole(Long roleId, String name) {
        Role role = mock(Role.class);
        lenient().when(role.getId()).thenReturn(roleId);
        lenient().when(role.getName()).thenReturn(name);
        return role;
    }

    @Nested
    @DisplayName("getAllUsers tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return only active users when filter is ACTIVE")
        void shouldReturnOnlyActiveUsersWhenFilterIsActive() {
            // Given
            User user1 = createUser(1L, "John", "Doe", "john@example.com", false);
            User user2 = createUser(2L, "Jane", "Smith", "jane@example.com", false);
            when(userRepository.findAllByDeletedFalse()).thenReturn(List.of(user1, user2));

            // When
            List<UserListItemDTO> result = userService.getAllUsers(UserListFilter.ACTIVE);

            // Then
            assertThat(result).hasSize(2);
            verify(userRepository).findAllByDeletedFalse();
            verify(userRepository, never()).findAllByDeletedTrue();
            verify(userRepository, never()).findAll();
        }

        @Test
        @DisplayName("Should return only deleted users when filter is DELETED")
        void shouldReturnOnlyDeletedUsersWhenFilterIsDeleted() {
            // Given
            User user1 = createUser(1L, "Deleted", "User", "deleted@example.com", true);
            when(userRepository.findAllByDeletedTrue()).thenReturn(List.of(user1));

            // When
            List<UserListItemDTO> result = userService.getAllUsers(UserListFilter.DELETED);

            // Then
            assertThat(result).hasSize(1);
            verify(userRepository).findAllByDeletedTrue();
            verify(userRepository, never()).findAllByDeletedFalse();
            verify(userRepository, never()).findAll();
        }

        @Test
        @DisplayName("Should return all users when filter is ALL")
        void shouldReturnAllUsersWhenFilterIsAll() {
            // Given
            User user1 = createUser(1L, "John", "Doe", "john@example.com", false);
            User user2 = createUser(2L, "Deleted", "User", "deleted@example.com", true);
            when(userRepository.findAll()).thenReturn(List.of(user1, user2));

            // When
            List<UserListItemDTO> result = userService.getAllUsers(UserListFilter.ALL);

            // Then
            assertThat(result).hasSize(2);
            verify(userRepository).findAll();
            verify(userRepository, never()).findAllByDeletedFalse();
            verify(userRepository, never()).findAllByDeletedTrue();
        }
    }

    @Nested
    @DisplayName("getAllUsersByCourse tests")
    class GetAllUsersByCourseTests {

        @Test
        @DisplayName("Should return users from course when authorized")
        void shouldReturnUsersFromCourseWhenAuthorized() {
            // Given
            Long courseId = 1L;
            AuthUser authUser = createAuthUser(1L, false, false, false);
            Course course = createCourse(courseId, "Test Course");
            User user1 = createUser(1L, "John", "Doe", "john@example.com", false);
            User user2 = createUser(2L, "Jane", "Smith", "jane@example.com", false);
            
            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(userRepository.findAllByCoursesIdAndDeletedFalse(courseId)).thenReturn(List.of(user1, user2));
            when(authorizationService.getVisibleRoles(any(User.class), eq(authUser))).thenReturn(List.of());

            // When
            List<UserListItemDTO> result = userService.getAllUsersByCourse(authUser, courseId);

            // Then
            assertThat(result).hasSize(2);
            verify(authorizationService).ensureHasCourseAccess(authUser, course);
            verify(authorizationService, times(2)).getVisibleRoles(any(User.class), eq(authUser));
        }

        @Test
        @DisplayName("Should throw CourseNotFoundException when course does not exist")
        void shouldThrowCourseNotFoundExceptionWhenCourseDoesNotExist() {
            // Given
            Long courseId = 999L;
            AuthUser authUser = createAuthUser(1L, false, false, false);
            when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

            // When + Then
            assertThrows(CourseNotFoundException.class,
                    () -> userService.getAllUsersByCourse(authUser, courseId));
        }
    }

    @Nested
    @DisplayName("getUserById tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user details when authorized")
        void shouldReturnUserDetailsWhenAuthorized() {
            // Given
            Long userId = 1L;
            AuthUser authUser = createAuthUser(2L, true, false, false);
            User user = createUser(userId, "John", "Doe", "john@example.com", false);
            Course course = createCourse(1L, "Test Course");
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.getCourses()).thenReturn(List.of(course));
            when(authorizationService.getVisibleRoles(user, authUser)).thenReturn(List.of());

            // When
            UserDetailDTO result = userService.getUserById(authUser, userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(userId);
            verify(authorizationService).ensureCanViewUser(authUser, user);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            Long userId = 999L;
            AuthUser authUser = createAuthUser(1L, true, false, false);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When + Then
            assertThrows(UserNotFoundException.class,
                    () -> userService.getUserById(authUser, userId));
        }
    }

    @Nested
    @DisplayName("createUser tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully when email is unique")
        void shouldCreateUserSuccessfullyWhenEmailIsUnique() {
            // Given
            UserRegisterRequestDTO userData = new UserRegisterRequestDTO("John", "Doe", "john@example.com", "password123");
            User savedUser = createUser(1L, "John", "Doe", "john@example.com", false);
            
            when(userRepository.existsByEmail(userData.email())).thenReturn(false);
            when(passwordEncoder.encode(userData.password())).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // When
            UserResponseDTO result = userService.createUser(userData);

            // Then
            assertThat(result).isNotNull();
            verify(passwordEncoder).encode(userData.password());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email already exists")
        void shouldThrowUserAlreadyExistsExceptionWhenEmailAlreadyExists() {
            // Given
            UserRegisterRequestDTO userData = new UserRegisterRequestDTO("John", "Doe", "existing@example.com", "password123");
            when(userRepository.existsByEmail(userData.email())).thenReturn(true);

            // When + Then
            assertThrows(UserAlreadyExistsException.class,
                    () -> userService.createUser(userData));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("updateUser tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully when email is unique")
        void shouldUpdateUserSuccessfullyWhenEmailIsUnique() {
            // Given
            Long userId = 1L;
            UserUpdateRequestDTO userData = new UserUpdateRequestDTO("John", "Updated", "john.updated@example.com", "newPassword");
            User existingUser = createUser(userId, "John", "Doe", "john@example.com", false);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmailAndIdNot(userData.email(), userId)).thenReturn(false);
            when(passwordEncoder.encode(userData.password())).thenReturn("hashedNewPassword");

            // When
            UserResponseDTO result = userService.updateUser(userId, userData);

            // Then
            assertThat(result).isNotNull();
            verify(passwordEncoder).encode(userData.password());
            verify(existingUser).update(userData, "hashedNewPassword");
        }

        @Test
        @DisplayName("Should update user without password when password is null")
        void shouldUpdateUserWithoutPasswordWhenPasswordIsNull() {
            // Given
            Long userId = 1L;
            UserUpdateRequestDTO userData = new UserUpdateRequestDTO("John", "Updated", "john.updated@example.com", null);
            User existingUser = createUser(userId, "John", "Doe", "john@example.com", false);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmailAndIdNot(userData.email(), userId)).thenReturn(false);

            // When
            UserResponseDTO result = userService.updateUser(userId, userData);

            // Then
            assertThat(result).isNotNull();
            verify(passwordEncoder, never()).encode(any());
            verify(existingUser).update(userData, null);
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email is taken by another user")
        void shouldThrowUserAlreadyExistsExceptionWhenEmailIsTakenByAnotherUser() {
            // Given
            Long userId = 1L;
            UserUpdateRequestDTO userData = new UserUpdateRequestDTO("John", "Updated", "taken@example.com", "password");
            User existingUser = createUser(userId, "John", "Doe", "john@example.com", false);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmailAndIdNot(userData.email(), userId)).thenReturn(true);

            // When + Then
            assertThrows(UserAlreadyExistsException.class,
                    () -> userService.updateUser(userId, userData));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            Long userId = 999L;
            UserUpdateRequestDTO userData = new UserUpdateRequestDTO("John", "Updated", "john@example.com", "password");
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When + Then
            assertThrows(UserNotFoundException.class,
                    () -> userService.updateUser(userId, userData));
        }
    }

    @Nested
    @DisplayName("deleteUser tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should mark user as deleted when user exists")
        void shouldMarkUserAsDeletedWhenUserExists() {
            // Given
            Long userId = 1L;
            User user = createUser(userId, "John", "Doe", "john@example.com", false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // When
            userService.deleteUser(userId);

            // Then
            verify(user).setDeleted(true);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When + Then
            assertThrows(UserNotFoundException.class,
                    () -> userService.deleteUser(userId));
        }
    }

    @Nested
    @DisplayName("updateUserRoles tests")
    class UpdateUserRolesTests {

        @Test
        @DisplayName("Should update user roles successfully when all roles exist")
        void shouldUpdateUserRolesSuccessfullyWhenAllRolesExist() {
            // Given
            Long userId = 1L;
            Set<Long> roleIds = Set.of(1L, 2L);
            UserRolesUpdateRequestDTO userRoles = new UserRolesUpdateRequestDTO(roleIds);
            User user = createUser(userId, "John", "Doe", "john@example.com", false);
            Role role1 = createRole(1L, "ADMIN");
            Role role2 = createRole(2L, "USER");
            List<Role> roles = List.of(role1, role2);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(roleRepository.findAllById(roleIds)).thenReturn(roles);

            // When
            List<RolePublicResponseDTO> result = userService.updateUserRoles(userId, userRoles);

            // Then
            assertThat(result).hasSize(2);
            verify(user).setRoles(roles);
        }

        @Test
        @DisplayName("Should throw RoleNotFoundException when some roles do not exist")
        void shouldThrowRoleNotFoundExceptionWhenSomeRolesDoNotExist() {
            // Given
            Long userId = 1L;
            Set<Long> roleIds = Set.of(1L, 999L);
            UserRolesUpdateRequestDTO userRoles = new UserRolesUpdateRequestDTO(roleIds);
            User user = createUser(userId, "John", "Doe", "john@example.com", false);
            Role role1 = createRole(1L, "ADMIN");
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(roleRepository.findAllById(roleIds)).thenReturn(List.of(role1)); // Solo retorna 1 de 2

            // When + Then
            assertThrows(RoleNotFoundException.class,
                    () -> userService.updateUserRoles(userId, userRoles));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            Long userId = 999L;
            UserRolesUpdateRequestDTO userRoles = new UserRolesUpdateRequestDTO(Set.of(1L));
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When + Then
            assertThrows(UserNotFoundException.class,
                    () -> userService.updateUserRoles(userId, userRoles));
        }
    }

    @Nested
    @DisplayName("updateUserCourses tests")
    class UpdateUserCoursesTests {

        @Test
        @DisplayName("Should update user courses successfully when all courses exist")
        void shouldUpdateUserCoursesSuccessfullyWhenAllCoursesExist() {
            // Given
            Long userId = 1L;
            Set<Long> courseIds = Set.of(1L, 2L);
            UserCoursesUpdateRequestDTO userCourses = new UserCoursesUpdateRequestDTO(courseIds);
            User user = createUser(userId, "John", "Doe", "john@example.com", false);
            Course course1 = createCourse(1L, "Course 1");
            Course course2 = createCourse(2L, "Course 2");
            List<Course> courses = List.of(course1, course2);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(courseRepository.findAllById(courseIds)).thenReturn(courses);

            // When
            List<CourseDetailDTO> result = userService.updateUserCourses(userId, userCourses);

            // Then
            assertThat(result).hasSize(2);
            verify(user).setCourses(courses);
        }

        @Test
        @DisplayName("Should throw CourseNotFoundException when some courses do not exist")
        void shouldThrowCourseNotFoundExceptionWhenSomeCoursesDoNotExist() {
            // Given
            Long userId = 1L;
            Set<Long> courseIds = Set.of(1L, 999L);
            UserCoursesUpdateRequestDTO userCourses = new UserCoursesUpdateRequestDTO(courseIds);
            User user = createUser(userId, "John", "Doe", "john@example.com", false);
            Course course1 = createCourse(1L, "Course 1");
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(courseRepository.findAllById(courseIds)).thenReturn(List.of(course1)); // Solo retorna 1 de 2

            // When + Then
            assertThrows(CourseNotFoundException.class,
                    () -> userService.updateUserCourses(userId, userCourses));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            Long userId = 999L;
            UserCoursesUpdateRequestDTO userCourses = new UserCoursesUpdateRequestDTO((Set<Long>) Set.of(1L));
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When + Then
            assertThrows(UserNotFoundException.class,
                    () -> userService.updateUserCourses(userId, userCourses));
        }
    }

    @Nested
    @DisplayName("updateUserBanStatus tests")
    class UpdateUserBanStatusTests {

        @Test
        @DisplayName("Should update ban status successfully when authorized")
        void shouldUpdateBanStatusSuccessfullyWhenAuthorized() {
            // Given
            Long userId = 1L;
            AuthUser authUser = createAuthUser(2L, true, false, false);
            UserBanStatusUpdateRequestDTO banStatus = new UserBanStatusUpdateRequestDTO(true);
            User targetUser = createUser(userId, "John", "Doe", "john@example.com", false);
            
            when(userRepository.findById(userId)).thenReturn(Optional.of(targetUser));

            // When
            userService.updateUserBanStatus(authUser, userId, banStatus);

            // Then
            verify(authorizationService).ensureCanBan(authUser, targetUser);
            verify(targetUser).setBanned(true);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            Long userId = 999L;
            AuthUser authUser = createAuthUser(1L, true, false, false);
            UserBanStatusUpdateRequestDTO banStatus = new UserBanStatusUpdateRequestDTO(true);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When + Then
            assertThrows(UserNotFoundException.class,
                    () -> userService.updateUserBanStatus(authUser, userId, banStatus));
        }
    }
}