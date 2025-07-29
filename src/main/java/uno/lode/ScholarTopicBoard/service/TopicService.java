package uno.lode.ScholarTopicBoard.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.topic.TopicRepository;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithAuthorDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithCourseDTO;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicDoesNotBelongToCourseException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.util.ServiceUtil;

@Service
@RequiredArgsConstructor
public class TopicService {
	private final TopicRepository topicRepository;
	private final CourseRepository courseRepository;
	private final ServiceUtil serviceUtil;

	@Transactional
	public TopicDetailDTO createTopic(AuthUser authUser, Long courseId, TopicRegisterRequestDTO topicData) {
		Course course = getValidatedCourse(authUser, courseId);
		if (topicRepository.existsByTitleAndCourseId(topicData.title(), courseId)) {
			throw new TopicAlreadyExistsException(topicData.title());}
		//User author = findUserOrThrow(authUser.getId());
		User author = authUser.getUser();
		Topic topic = topicRepository.save(new Topic(topicData, author, course));
		return new TopicDetailDTO(topic);
	}

	@Transactional
	public TopicDetailDTO updateTopic(AuthUser authUser, Long courseId, Long topicId, TopicUpdateRequestDTO topicData) {
		Course course = getValidatedCourse(authUser, courseId);
		Topic topic = findTopicOrThrow(topicId);
		// Topic belong to course check
		checkTopicBelongToCourse(topic, courseId);
		serviceUtil.checkAdminModeratorOrAuthor(authUser, topic);
		if (topicRepository.existsByTitleAndCourseIdAndIdNot(topicData.title(), courseId, topicId)) {
			throw new TopicAlreadyExistsException(topicData.title());
		}
		topic.update(topicData);
		return new TopicDetailDTO(topic);
	}

	@Transactional(readOnly = true)
	public List<TopicWithAuthorDTO> listByCourse(AuthUser authUser, Long courseId) {
		Course course = getValidatedCourse(authUser, courseId);
		return topicRepository.findByCourseWithAuthor(course)
				.stream().map(TopicWithAuthorDTO::new)
				.toList();
	}

	@Transactional(readOnly = true)
	public TopicWithAuthorDTO getTopic(AuthUser authUser, Long courseId, Long topicId) {
		Course course = getValidatedCourse(authUser, courseId);
		Topic topic = findTopicWithAuthorOrThrow(topicId);
		checkTopicBelongToCourse(topic, courseId);
		return new TopicWithAuthorDTO(topic);
	}

	@Transactional(readOnly = true)
	public List<TopicWithCourseDTO> listByLoggedUser(AuthUser myUser) {
		return topicRepository.findByAuthorIdWithCourse(myUser.getId()).stream()
				.map(TopicWithCourseDTO::new).toList();
	}

	@Transactional
	public void deleteTopic(AuthUser authUser, Long courseId, Long topicId) {
		Course course = getValidatedCourse(authUser, courseId);
		Topic topic = findTopicOrThrow(topicId);
		checkTopicBelongToCourse(topic, courseId);
		serviceUtil.checkAdminModeratorOrAuthor(authUser, topic);
		topicRepository.delete(topic);
	}

	private Course findCourseOrThrow(Long courseId) {
		return courseRepository.findById(courseId)
			.orElseThrow(() -> new CourseNotFoundException(courseId));
	}
	
	private void checkUserHasCourseAccess(AuthUser authUser, Course course) {
	    serviceUtil.checkAdminCoordinatorOrEnrolled(authUser, course);
	}

	private Course getValidatedCourse(AuthUser authUser, Long courseId) {
		Course course = findCourseOrThrow(courseId);
		checkUserHasCourseAccess(authUser, course);
		return course;
	}
	
	private Topic findTopicOrThrow(Long topicId) {
		return topicRepository.findById(topicId)
		.orElseThrow(() -> new TopicNotFoundException(topicId));
	}

	private Topic findTopicWithAuthorOrThrow(Long topicId) {
		return topicRepository.findByIdWithAuthor(topicId)
		.orElseThrow(() -> new TopicNotFoundException(topicId));
	}

	private void checkTopicBelongToCourse(Topic topic, Long courseId) {
		if(!topic.getCourse().getId().equals(courseId)){
			throw new TopicDoesNotBelongToCourseException();
		}		
	}
	
	
}
