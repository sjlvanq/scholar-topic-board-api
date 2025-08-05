package uno.lode.ScholarTopicBoard.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uno.lode.ScholarTopicBoard.domain.reply.Reply;
import uno.lode.ScholarTopicBoard.domain.reply.ReplyRepository;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyDetailDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDepthLimitExceededException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@Service
@RequiredArgsConstructor
public class ReplyService {
	private final ReplyRepository replyRepository;
	private final DomainValidationService validationService;
	private final UserAuthorizationService userAuthorizationService;

	@Transactional(readOnly = true)
	public ReplyDetailDTO getReply(AuthUser authUser, Long courseId, Long topicId, Long replyId) {
		validateCourseAndTopic(authUser, courseId, topicId);
		Reply reply = validationService.findReplyInTopic(replyId, topicId);
		return new ReplyDetailDTO(reply);
	}

	@Transactional
	public ReplyDetailDTO createReply(AuthUser authUser, Long courseId, Long topicId, Long parentId, ReplyRegisterRequestDTO replyData) {
		validationService.ensureCourseHasAccess(authUser, courseId);
		Topic topic = validationService.findTopicInCourse(topicId, courseId);
		Reply parent = validationService.findParentReplyInTopic(parentId, topicId);
		if(getDepth(parent)>2) {
			throw new ReplyDepthLimitExceededException();
		}
		Reply reply = replyRepository.save(new Reply(topic, authUser.getUser(), parent, replyData));
		return new ReplyDetailDTO(reply);
	}

	@Transactional(readOnly = true)
	public List<ReplyDetailDTO> getRootReplies(AuthUser authUser, Long courseId, Long topicId) {
		validateCourseAndTopic(authUser, courseId, topicId);
		return replyRepository.findByTopicIdAndParentIsNull(topicId)
				.stream().map(ReplyDetailDTO::new).collect(Collectors.toList());
	}

	@Transactional
	public ReplyDetailDTO updateReply(AuthUser authUser, Long courseId, Long topicId, Long replyId, ReplyUpdateRequestDTO replyData) {
		validateCourseAndTopic(authUser, courseId, topicId);
		Reply reply = validationService.findReplyInTopic(replyId, topicId);
		userAuthorizationService.ensureCanAccessAuthorable(authUser, reply);
		reply.update(replyData);
		return new ReplyDetailDTO(reply);
	}

	@Transactional
	public void deleteReply(AuthUser authUser, Long courseId, Long topicId, Long replyId) {
		validateCourseAndTopic(authUser, courseId, topicId);
		Reply reply = validationService.findReplyInTopic(replyId, topicId);
		userAuthorizationService.ensureCanAccessAuthorable(authUser, reply);
		replyRepository.delete(reply);
	}

	private void validateCourseAndTopic(AuthUser authUser, Long courseId, Long topicId) {
		validationService.ensureCourseHasAccess(authUser, courseId);
		validationService.ensureTopicBelongToCourse(topicId, courseId);
	}

	private int getDepth(Reply reply) {
		return (reply==null) ? 0 : 1 + getDepth(reply.getParent());
	}
}
