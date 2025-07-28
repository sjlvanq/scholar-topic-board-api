// Archivo: UserServiceTest.java
package uno.lode.ScholarTopicBoard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.role.Role;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserDetailDTO;
import uno.lode.ScholarTopicBoard.infra.exception.user.FakeUserNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.user.UserNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.util.ServiceUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private CourseRepository courseRepository;
	@Mock
	private ServiceUtil serviceUtil;

	@InjectMocks
	private UserService userService;

	@Test
	void shouldThrowAccessDeniedWhenUserNotAllowedToGetUsersByCourse() {
		Long courseId = 1L;
		AuthUser mockUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);

		when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
		doThrow(new SecurityException("Forbidden")).when(serviceUtil).checkAdminCoordinatorOrEnrolled(mockUser,
				mockCourse);

		assertThrows(SecurityException.class, () -> {
			userService.getAllUsersByCourse(mockUser, courseId);
		});
	}

	@Test
	void shouldReturnUsersWhenUserIsAllowed() {
		Long courseId = 1L;
		AuthUser mockUser = mock(AuthUser.class);
		Course mockCourse = mock(Course.class);

		when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
		when(userRepository.findAllByCoursesIdAndDeletedFalse(courseId)).thenReturn(java.util.Collections.emptyList());

		userService.getAllUsersByCourse(mockUser, courseId);
	}

	@Test
	@DisplayName("Should return user details when user exists and access is allowed")
	void shouldReturnUserDetailsWhenUserExistsAndAccessIsAllowed() {
		Long userId = 1L;
		AuthUser mockAuthUser = mock(AuthUser.class);
		User mockUser = mock(User.class);
		Role mockRole = mock(Role.class);
		Course mockCourse = mock(Course.class);

		when(mockAuthUser.isAdmin()).thenReturn(false);
		when(mockUser.getId()).thenReturn(userId);
		when(mockUser.getFirstName()).thenReturn("John");
		when(mockUser.getLastName()).thenReturn("Doe");
		when(mockUser.getEmail()).thenReturn("john.doe@example.com");
		when(mockUser.getRoles()).thenReturn(List.of(mockRole));
		when(mockUser.getCourses()).thenReturn(List.of(mockCourse));
		when(mockRole.getIsPublic()).thenReturn(true);

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(serviceUtil.isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId)).thenReturn(true);

		UserDetailDTO result = userService.getUserById(mockAuthUser, userId);

		assertNotNull(result);
		assertEquals(userId, result.id());
		assertEquals("John", result.firstName());
		assertEquals("Doe", result.lastName());
		assertEquals("john.doe@example.com", result.email());
		assertEquals(1, result.roles().size());
		assertEquals(1, result.courses().size());

		verify(userRepository).findById(userId);
		verify(serviceUtil).isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId);
	}

	@Test
	@DisplayName("Should throw UserNotFoundException when user does not exist")
	void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
		Long userId = 999L;
		AuthUser mockAuthUser = mock(AuthUser.class);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.getUserById(mockAuthUser, userId);
		});

		verify(userRepository).findById(userId);
		verify(serviceUtil, never()).isAdminCoordinatorOrHasSharedCourse(any(), any());
	}

	@Test
	@DisplayName("Should throw FakeUserNotFoundException when access is denied")
	void shouldThrowFakeUserNotFoundExceptionWhenAccessIsDenied() {
		Long userId = 1L;
		AuthUser mockAuthUser = mock(AuthUser.class);
		User mockUser = mock(User.class);

		when(mockUser.getId()).thenReturn(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(serviceUtil.isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId)).thenReturn(false);

		assertThrows(FakeUserNotFoundException.class, () -> {
			userService.getUserById(mockAuthUser, userId);
		});

		verify(userRepository).findById(userId);
		verify(serviceUtil).isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId);
	}

	@Test
	@DisplayName("Should return only public roles when user is not admin")
	void shouldReturnOnlyPublicRolesWhenUserIsNotAdmin() {
		Long userId = 1L;
		AuthUser mockAuthUser = mock(AuthUser.class);
		User mockUser = mock(User.class);
		Role publicRole = mock(Role.class);
		Role privateRole = mock(Role.class);

		when(mockAuthUser.isAdmin()).thenReturn(false);
		when(mockUser.getId()).thenReturn(userId);
		when(mockUser.getFirstName()).thenReturn("John");
		when(mockUser.getLastName()).thenReturn("Doe");
		when(mockUser.getEmail()).thenReturn("john.doe@example.com");
		when(mockUser.getRoles()).thenReturn(List.of(publicRole, privateRole));
		when(mockUser.getCourses()).thenReturn(List.of());
		when(publicRole.getIsPublic()).thenReturn(true);
		when(privateRole.getIsPublic()).thenReturn(false);

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(serviceUtil.isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId)).thenReturn(true);

		UserDetailDTO result = userService.getUserById(mockAuthUser, userId);

		assertEquals(1, result.roles().size()); // Public role only

		verify(userRepository).findById(userId);
		verify(serviceUtil).isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId);
	}

	@Test
	@DisplayName("Should return all roles when user is admin")
	void shouldReturnAllRolesWhenUserIsAdmin() {
		Long userId = 1L;
		AuthUser mockAuthUser = mock(AuthUser.class);
		User mockUser = mock(User.class);
		Role publicRole = mock(Role.class);
		Role privateRole = mock(Role.class);

		when(mockAuthUser.isAdmin()).thenReturn(true);
		when(mockUser.getId()).thenReturn(userId);
		when(mockUser.getFirstName()).thenReturn("John");
		when(mockUser.getLastName()).thenReturn("Doe");
		when(mockUser.getEmail()).thenReturn("john.doe@example.com");
		when(mockUser.getRoles()).thenReturn(List.of(publicRole, privateRole));
		when(mockUser.getCourses()).thenReturn(List.of());

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(serviceUtil.isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId)).thenReturn(true);

		UserDetailDTO result = userService.getUserById(mockAuthUser, userId);

		assertEquals(2, result.roles().size()); // Public and private roles

		verify(userRepository).findById(userId);
		verify(serviceUtil).isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId);
	}

	@Test
	@DisplayName("Should return empty lists when user has no roles or courses")
	void shouldReturnEmptyListsWhenUserHasNoRolesOrCourses() {
		Long userId = 1L;
		AuthUser mockAuthUser = mock(AuthUser.class);
		User mockUser = mock(User.class);

		when(mockUser.getId()).thenReturn(userId);
		when(mockUser.getFirstName()).thenReturn("John");
		when(mockUser.getLastName()).thenReturn("Doe");
		when(mockUser.getEmail()).thenReturn("john.doe@example.com");
		when(mockUser.getRoles()).thenReturn(null);
		when(mockUser.getCourses()).thenReturn(null);

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(serviceUtil.isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId)).thenReturn(true);

		UserDetailDTO result = userService.getUserById(mockAuthUser, userId);

		assertTrue(result.roles().isEmpty());
		assertTrue(result.courses().isEmpty());

		verify(userRepository).findById(userId);
		verify(serviceUtil).isAdminCoordinatorOrHasSharedCourse(mockAuthUser, userId);
	}
}
