package uno.lode.ScholarTopicBoard.domain.topic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import uno.lode.ScholarTopicBoard.domain.course.Course;

public interface TopicRepository extends JpaRepository<Topic, Long> {

	boolean existsByTitle(String title);

	boolean existsByTitleAndCourseId(String title, Long courseId);

	boolean existsByTitleIgnoreCaseAndCourseIdAndIdNot(@NotBlank @Size(min = 5, max = 50) String title, Long courseId,
			Long topicId);

	boolean existsByTitleAndCourseIdAndIdNot(String title, Long courseId, Long topicId);
	
	List<Topic> findByAuthorId(Long authorId);

	@Query("SELECT t FROM Topic t JOIN FETCH t.course c JOIN FETCH t.author JOIN FETCH t.author.roles "
			+ "WHERE t.author.id = :authorId")
	List<Topic> findByAuthorIdWithCourse(@Param("authorId") Long authorId);

	//List<Topic> findByCourseId(Long courseId);s
	@Query("SELECT t FROM Topic t JOIN FETCH t.author a JOIN FETCH a.roles r WHERE t.course = :course")
	List<Topic> findByCourseWithAuthor(@Param("course") Course course);

	Optional<Topic> findByIdAndCourseId(Long topicId, Long courseId);

	@Query("SELECT t FROM Topic t JOIN FETCH t.author a WHERE t.id = :topicId")
	Optional<Topic> findByIdWithAuthor(@Param("topicId") Long topicId);

	//@Query("SELECT COUNT(u) > 0 FROM User u JOIN u.courses c WHERE u.id = :userId AND c.id = :courseId")
	//boolean isUserEnrolledInCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
