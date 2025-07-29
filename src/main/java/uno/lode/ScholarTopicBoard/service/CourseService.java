package uno.lode.ScholarTopicBoard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseDeletionLockedException;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;

@Service
public class CourseService {
	@Autowired
	private CourseRepository courseRepository;
	
	public List<CourseDetailDTO> getAllCourses() {
		return courseRepository.findAll().stream()
				.map(CourseDetailDTO::new)
				.toList();
	}

	public CourseDetailDTO getCourseById(Long id) {
		return courseRepository.findById(id)
		        .map(CourseDetailDTO::new)
		        .orElseThrow(() -> new CourseNotFoundException(id));
	}

	public List<CourseDetailDTO> getCoursesByUserId(Long id) {
		return courseRepository.findCoursesByUserId(id).stream()
				.map(CourseDetailDTO::new)
				.toList();
	}

	@Transactional
	public CourseDetailDTO createCourse(CourseRegisterRequestDTO courseData) {
        if (courseRepository.existsByName(courseData.name())) {
            throw new CourseAlreadyExistsException(courseData.name());
        }
	    Course course= courseRepository.save(new Course(courseData));
	    return new CourseDetailDTO(course);
	}

	@Transactional
	public CourseDetailDTO updateCourse(Long courseId, CourseUpdateRequestDTO courseData) {
		Course course = courseRepository.findById(courseId)
			.orElseThrow(() -> new CourseNotFoundException(courseId));
		if (courseRepository.existsByNameAndIdNot(courseData.name(), courseId)) {
            throw new CourseAlreadyExistsException(courseData.name());
        }
		course.update(courseData);
		return new CourseDetailDTO(course);
	}

	@Transactional
	public void deleteCourse(Long courseId) {
	    Course course = courseRepository.findById(courseId)
	        .orElseThrow(() -> new CourseNotFoundException(courseId));
	    try {
	        courseRepository.delete(course);
	    } catch (DataIntegrityViolationException ex) {
	        throw new CourseDeletionLockedException();
	    }
	}
}
