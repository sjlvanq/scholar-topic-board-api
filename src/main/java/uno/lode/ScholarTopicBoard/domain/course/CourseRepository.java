package uno.lode.ScholarTopicBoard.domain.course;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

	@Query("SELECT c FROM User u JOIN u.courses c WHERE u.id = :id")
	Collection<Course> findCoursesByUserId(@Param("id") Long id); //Set?

	boolean existsByName(String name);

	boolean existsByNameAndIdNot(String name, Long courseId);
	
}
