package uno.lode.ScholarTopicBoard.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.course.CourseRepository;
import uno.lode.ScholarTopicBoard.domain.reply.Reply;
import uno.lode.ScholarTopicBoard.domain.reply.ReplyRepository;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyDetailDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.topic.TopicRepository;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ParentReplyDoesNotBelongToTopicException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDepthLimitExceededException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDoesNotBelongToTopicException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicDoesNotBelongToCourseException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.util.ServiceUtil;

@Service
@RequiredArgsConstructor
public class ReplyService {
	private final ReplyRepository replyRepository;
	private final TopicRepository topicRepository;
	private final CourseRepository courseRepository;
	private final ServiceUtil serviceUtil;

	@Transactional(readOnly = true)
	public ReplyDetailDTO getReply(AuthUser authUser, Long courseId, Long topicId, Long replyId) {
		Topic topic = validateAccess(authUser, courseId, topicId);
		Reply reply = findReplyOrThrow(replyId);
		if (!reply.getTopic().getId().equals(topicId)) {
			throw new ReplyDoesNotBelongToTopicException();
		}
		return new ReplyDetailDTO(reply);
	}

	@Transactional
	public ReplyDetailDTO createReply(AuthUser authUser, Long courseId, Long topicId, Long parentId, ReplyRegisterRequestDTO replyData) {
		Topic topic = validateAccess(authUser, courseId, topicId);
		User author = authUser.getUser();
		Reply parent = validateParentReply(parentId, topicId);
		if(getDepth(parent)>2) {
			throw new ReplyDepthLimitExceededException();
		}
		Reply reply = replyRepository.save(new Reply(topic, author, parent, replyData));
		return new ReplyDetailDTO(reply);
	}

	@Transactional(readOnly = true)
	public Set<ReplyDetailDTO> getRootReplies(AuthUser authUser, Long courseId, Long topicId) {
		validateAccess(authUser, courseId, topicId); // Admin or Enrolled
		return replyRepository.findByTopicIdAndParentIsNull(topicId)
				.stream().map(ReplyDetailDTO::new).collect(Collectors.toSet());
	}

	@Transactional
	public ReplyDetailDTO updateReply(AuthUser authUser, Long courseId, Long topicId, Long replyId, ReplyUpdateRequestDTO replyData) {
		Topic topic = validateAccess(authUser, courseId, topicId);
		Reply reply = findReplyOrThrow(replyId);
		validateOwnershipAndTopic(reply, authUser, topicId);
		reply.update(replyData);
		return new ReplyDetailDTO(reply);
	}

	@Transactional
	public void deleteReply(AuthUser authUser, Long courseId, Long topicId, Long replyId) {
		Topic topic = validateAccess(authUser, courseId, topicId);
		Reply reply = findReplyOrThrow(replyId);
		validateOwnershipAndTopic(reply, authUser, topicId);
		replyRepository.delete(reply);
	}

	private void validateOwnershipAndTopic(Reply reply, AuthUser authUser, Long topicId) {
		if(!reply.getTopic().getId().equals(topicId)) {
			throw new ReplyDoesNotBelongToTopicException();
		}
		if(! (reply.isAuthoredBy(authUser.getId()) || authUser.isAdmin() || authUser.isModerator())) {
			throw new AccessDeniedException("Access denied!");
		}
	}

	private Reply findReplyOrThrow(Long replyId) {
		return replyRepository.findById(replyId)
				.orElseThrow(() -> new ReplyNotFoundException(replyId));
	}

	private Course findCourseOrThrow(Long courseId) {
		return courseRepository.findById(courseId)
				.orElseThrow(() -> new CourseNotFoundException(courseId));
	}

	private Topic findTopicOrThrow(Long topicId) {
		return topicRepository.findById(topicId)
				.orElseThrow(() -> new TopicNotFoundException(topicId));
	}

	private Topic validateAccess(AuthUser authUser, Long courseId, Long topicId) {
		Course course = findCourseOrThrow(courseId);
		//serviceUtil.checkAdminOrEnrolled(authUser, course);
		serviceUtil.checkAdminCoordinatorOrEnrolled(authUser, course);
		Topic topic = findTopicOrThrow(topicId);
		if (!topic.getCourse().getId().equals(courseId)) {
			throw new TopicDoesNotBelongToCourseException();
		}
		return topic;
	}

	private Reply validateParentReply(Long parentId, Long topicId) {
		if (parentId == null) return null;
		Reply parent = findReplyOrThrow(parentId);
		if (!parent.getTopic().getId().equals(topicId)) {
			throw new ParentReplyDoesNotBelongToTopicException();
		}
		return parent;
	}

	private int getDepth(Reply reply) {
		return (reply==null) ? 0 : 1 + getDepth(reply.getParent());
	}
}
