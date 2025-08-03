package uno.lode.ScholarTopicBoard.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.reply.Reply;
import uno.lode.ScholarTopicBoard.domain.reply.ReplyRepository;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.topic.TopicRepository;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ParentReplyDoesNotBelongToTopicException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDoesNotBelongToTopicException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicDoesNotBelongToCourseException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@Service
@RequiredArgsConstructor
public class DomainValidationService {
	private final TopicRepository topicRepository;
	private final CourseRepository courseRepository;
	private final ReplyRepository replyRepository;
	private final UserAuthorizationService userAuthorizationService;

	public void ensureCourseHasAccess(AuthUser authUser, Long courseId) {
		Course course = findCourseOrThrow(courseId);
		userAuthorizationService.ensureHasCourseAccess(authUser, course);
	}

	public void ensureTopicBelongToCourse(Long topicId, Long courseId) {
		Topic topic = findTopicOrThrow(topicId);
		if (!topic.getCourse().getId().equals(courseId)) {
			throw new TopicDoesNotBelongToCourseException();
		}
	}

	public Course findCourseWithAccess(AuthUser authUser, Long courseId) {
		Course course = findCourseOrThrow(courseId);
		userAuthorizationService.ensureHasCourseAccess(authUser, course);
		return course;
	}	
	
	public Reply findParentReplyInTopic(Long parentId, Long topicId) {
		if (parentId == null) return null;
		Reply parent = findReplyOrThrow(parentId);
		if (!parent.getTopic().getId().equals(topicId)) {
			throw new ParentReplyDoesNotBelongToTopicException();
		}
		return parent;		
	}

	public Reply findReplyInTopic(Long replyId, Long topicId) {
		Reply reply = findReplyOrThrow(replyId);
		if (!reply.getTopic().getId().equals(topicId)) {
			throw new ReplyDoesNotBelongToTopicException();
		}
		return reply;
	}
		
	public Topic findTopicInCourse(Long topicId, Long courseId) {
		Topic topic = findTopicOrThrow(topicId);
		if (!topic.getCourse().getId().equals(courseId)) {
			throw new TopicDoesNotBelongToCourseException();
		}
		return topic;
	}

	public Course findCourseOrThrow(Long courseId) {
		return courseRepository.findById(courseId)
				.orElseThrow(() -> new CourseNotFoundException(courseId));
	}
	
	public Reply findReplyOrThrow(Long replyId) {
		return replyRepository.findById(replyId)
				.orElseThrow(() -> new ReplyNotFoundException(replyId));
	}
	
	// Used when author info is not needed; only checks existence
	public Topic findTopicOrThrow(Long topicId) {
		return topicRepository.findById(topicId)
				.orElseThrow(() -> new TopicNotFoundException(topicId));
	}

	// Used when author info is needed; fetches topic with author relationship
	public Topic findTopicWithAuthorOrThrow(Long topicId) {
		return topicRepository.findByIdWithAuthor(topicId)
				.orElseThrow(() -> new TopicNotFoundException(topicId));
	}
	
}
