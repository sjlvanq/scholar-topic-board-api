package uno.lode.ScholarTopicBoard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseSummaryDTO;
import uno.lode.ScholarTopicBoard.domain.role.Role;
import uno.lode.ScholarTopicBoard.domain.role.RoleRepository;
import uno.lode.ScholarTopicBoard.domain.role.dto.RolePublicResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.UserListFilter;
import uno.lode.ScholarTopicBoard.domain.user.User;
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
import uno.lode.ScholarTopicBoard.infra.exception.user.FakeUserNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.user.UserAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.user.UserNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.util.ServiceUtil;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ServiceUtil serviceUtil;

	public List<UserListItemDTO> getAllUsers(UserListFilter includes) {
		List<User> users = null;
		switch(includes) {
	        case ACTIVE -> users = userRepository.findAllByDeletedFalse();
	        case DELETED -> users = userRepository.findAllByDeletedTrue();
	        case ALL -> users = userRepository.findAll();
	    }
		return users.stream().map(UserListItemDTO::new).toList();
	}

	public List<UserListItemDTO> getAllUsersByCourse(AuthUser authUser, Long courseId) {
		Course course = courseRepository.findById(courseId).orElseThrow(()->new CourseNotFoundException(courseId));
		serviceUtil.checkAdminCoordinatorOrEnrolled(authUser, course);
		return userRepository.findAllByCoursesIdAndDeletedFalse(courseId).stream()
				.map(u -> new UserListItemDTO(u, getVisibleRoles(u, authUser))).toList();
	}
	
	public UserDetailDTO getUserById(AuthUser authUser, Long id) {
		User user = findUserByIdOrThrow(id);
		if(!serviceUtil.isAdminCoordinatorOrHasSharedCourse(authUser, user.getId())) {
			throw new FakeUserNotFoundException(id);
		}
		
		List<RolePublicResponseDTO> visibleRoles = getVisibleRoles(user, authUser);
		List<CourseSummaryDTO> courses = user.getCourses() != null
				? user.getCourses().stream().map(CourseSummaryDTO::new).toList()
				: List.of();

		return new UserDetailDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), visibleRoles,
				courses);
	}

	public UserResponseDTO createUser(@Valid UserRegisterRequestDTO userData) {
		if (userRepository.existsByEmail(userData.email())) { // Includes deleted users
			throw new UserAlreadyExistsException(userData.email());
		}
		String hashedPassword = passwordEncoder.encode(userData.password());
		User user = new User(userData);
	    user.setPassword(hashedPassword);

	    return new UserResponseDTO(userRepository.save(user));
	}

	@Transactional
	public UserResponseDTO updateUser(Long userId, @Valid UserUpdateRequestDTO userData) {
		try {
			User userRef = userRepository.getReferenceById(userId);
			if (userRepository.existsByEmailAndIdNot(userData.email(), userId)) { // Includes deleted users
				throw new UserAlreadyExistsException(userData.email());
			}
			String hashedPassword = (userData.password()!=null) ? passwordEncoder.encode(userData.password()) : null;
			userRef.update(userData, hashedPassword);
			return new UserResponseDTO(userRef);
		} catch (EntityNotFoundException e) {
			throw new UserNotFoundException(userId);
		}
	}
	
	@Transactional
	public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
        		.orElseThrow(()->new UserNotFoundException(userId));
        user.setDeleted(true);
   	}

	@Transactional
	public List<RolePublicResponseDTO> updateUserRoles(Long userId, @Valid UserRolesUpdateRequestDTO userRoles) {
		User user = findUserByIdOrThrow(userId);
	    List<Role> updatedRoles = roleRepository.findAllById(userRoles.roleIds());
	    if (updatedRoles.size() != userRoles.roleIds().size()) {
	        throw new RoleNotFoundException("Some roles not found!");
	    }
	    user.setRoles(updatedRoles);
	    return updatedRoles.stream().map(RolePublicResponseDTO::new).toList();
	}

	@Transactional
	public List<CourseDetailDTO> updateUserCourses(Long userId, @Valid UserCoursesUpdateRequestDTO userCourses) {
	    User user = findUserByIdOrThrow(userId);
	    List<Course> updatedCourses = courseRepository.findAllById(userCourses.courseIds());
	    if (updatedCourses.size() != userCourses.courseIds().size()) {
	        throw new CourseNotFoundException("Some courses not found!");
	    }
	    user.setCourses(updatedCourses);
	    return updatedCourses.stream().map(CourseDetailDTO::new).toList();
	}

	@Transactional
	public void updateUserBanStatus(Long userId, @Valid UserBanStatusUpdateRequestDTO userBanStatus) {
		try {
			User userRef = userRepository.getReferenceById(userId);
			userRef.setBanned(userBanStatus.status());
		} catch (EntityNotFoundException e) {
			throw new UserNotFoundException(userId);
		}
	}
	
	private User findUserByIdOrThrow(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));
	}
	
	private List<RolePublicResponseDTO> getVisibleRoles(User user, AuthUser authUser) {
		if (user.getRoles() == null)
			return List.of();

		return user.getRoles().stream()
				.filter(role -> authUser.isAdmin() || role.getIsPublic()).map(RolePublicResponseDTO::new)
					.toList();
	}
}
