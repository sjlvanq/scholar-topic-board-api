package uno.lode.ScholarTopicBoard.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.topic.TopicRepository;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithAuthorDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithCourseDTO;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@Service
@RequiredArgsConstructor
public class TopicService {
	private final TopicRepository topicRepository;
	private final DomainValidationService validationService;
	private final UserAuthorizationService userAuthorizationService;

	@Transactional
	public TopicDetailDTO createTopic(AuthUser authUser, Long courseId, TopicRegisterRequestDTO topicData) {
		Course course = validationService.findCourseWithAccess(authUser, courseId);
		if (topicRepository.existsByTitleIgnoreCaseAndCourseId(topicData.title(), courseId)) {
			throw new TopicAlreadyExistsException(topicData.title());}

		Topic topic = topicRepository.save(new Topic(topicData, authUser.getUser(), course));
		return new TopicDetailDTO(topic);
	}

	@Transactional
	public TopicDetailDTO updateTopic(AuthUser authUser, Long courseId, Long topicId, TopicUpdateRequestDTO topicData) {
		validationService.ensureCourseHasAccess(authUser, courseId);
		Topic topic = validationService.findTopicInCourse(topicId, courseId);
		userAuthorizationService.ensureCanAccessAuthorable(authUser, topic);
		
		// Maybe need trim
		if (topicRepository.existsByTitleIgnoreCaseAndCourseIdAndIdNot(topicData.title(), courseId, topicId)) {
			throw new TopicAlreadyExistsException(topicData.title());
		}
		
		topic.update(topicData);
		return new TopicDetailDTO(topic);
	}

	@Transactional(readOnly = true)
	public List<TopicWithAuthorDTO> listByCourse(AuthUser authUser, Long courseId) {
		Course course = validationService.findCourseWithAccess(authUser, courseId);
		
		return topicRepository.findByCourseWithAuthor(course)
				.stream().map(TopicWithAuthorDTO::new)
				.toList();
	}

	@Transactional(readOnly = true)
	public TopicWithAuthorDTO getTopic(AuthUser authUser, Long courseId, Long topicId) {
		validationService.ensureCourseHasAccess(authUser, courseId);
		Topic topic = validationService.findTopicInCourse(topicId, courseId);
		
		return new TopicWithAuthorDTO(topic);
	}

	@Transactional(readOnly = true)
	public List<TopicWithCourseDTO> listByLoggedUser(AuthUser myUser) {
		return topicRepository.findByAuthorIdWithCourse(myUser.getId()).stream()
				.map(TopicWithCourseDTO::new).toList();
	}

	@Transactional
	public void deleteTopic(AuthUser authUser, Long courseId, Long topicId) {
		validationService.ensureCourseHasAccess(authUser, courseId);
		Topic topic = validationService.findTopicInCourse(topicId, courseId);
		userAuthorizationService.ensureCanAccessAuthorable(authUser, topic);
				
		topicRepository.delete(topic);
	}
}
