package uno.lode.ScholarTopicBoard.domain.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByEmailAndDeletedFalse(String email);

	boolean existsByEmailAndIdNot(String email, Long userId);

	boolean existsByIdAndCoursesIdAndDeletedFalse(Long userId, Long courseId);

	List<User> findAll();

	List<User> findAllByCoursesIdAndDeletedFalse(Long courseId);
	
	List<User> findAllByDeletedFalse();

	List<User> findAllByDeletedTrue();

	Optional<User> findByEmailAndDeletedFalse(String email);
	
	@Query("SELECT u FROM User u JOIN FETCH u.roles r WHERE u.deleted = false AND u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email); // AndDeletedFalse
	
	@Query("SELECT u FROM User u JOIN FETCH u.roles r JOIN FETCH u.courses c WHERE u.deleted = false AND u.email = :email")
	Optional<User> findByEmailWithRolesAndCourses(String email); // AndDeletedFalse

	@Query("SELECT u FROM User u JOIN FETCH u.roles r WHERE u.deleted = false and u.id = :userId")
	Optional<User> findById(@Param("userId") Long userId);

	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
			"FROM User u JOIN u.roles r WHERE u.id = :userId AND r.name = :roleName")
	boolean hasRole(@Param("userId") Long userId, @Param("roleName") String roleName);
	
	@Query("SELECT COUNT(u) > 0 FROM User u JOIN u.courses c WHERE u.id = :userId AND c.id = :courseId")
	boolean isUserEnrolledInCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
	
	@Query(value = "SELECT IF( COUNT(*) > 0, 'TRUE', 'FALSE') FROM users_courses uc1 " +
			"JOIN users_courses uc2 ON uc1.course_id = uc2.course_id " +
			"JOIN users u1 ON uc1.user_id = u1.id " +
			"JOIN users u2 ON uc2.user_id = u2.id " +
			"WHERE uc1.user_id = :authUserId AND uc2.user_id = :targetUserId " +
			"AND u1.deleted = false AND u2.deleted = false", 
    nativeQuery = true)
	boolean sharesCoursesWith(@Param("authUserId") Long authUserId, @Param("targetUserId") Long targetUserId);
}