package uno.lode.ScholarTopicBoard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uno.lode.ScholarTopicBoard.domain.reply.Reply;
import uno.lode.ScholarTopicBoard.domain.reply.ReplyRepository;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyDetailDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDepthLimitExceededException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;

@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {
	@Mock
	private ReplyRepository replyRepository;
	@Mock
	private UserAuthorizationService userAuthorizationService;
	@Mock
	private DomainValidationService validationService;
	
	@InjectMocks
	private ReplyService replyService;

	private AuthUser createAuthUserWithUser(Long id) {
		AuthUser authUser = mock(AuthUser.class);
		User user = mock(User.class);
		when(authUser.getUser()).thenReturn(user);
		return authUser;
	}
	
	private Reply createReplyWithAuthor(Long id) {
		Reply reply = mock(Reply.class);
		User author = mock(User.class);
		when(reply.getId()).thenReturn(id);
		when(reply.getAuthor()).thenReturn(author);
		return reply;
	}
	
	// Tests
	@Nested
	@DisplayName("getReply tests")
	class GetReplyTests {
		@Test
		void shouldReturnReplyDetailDTO() {
			// Given
			AuthUser authUser = mock(AuthUser.class);
			Long courseId = 101L;
			Long topicId = 201L;
			Long replyId = 301L;
			Reply replyWithAuthor = createReplyWithAuthor(replyId);
			doNothingWhenValidateCourseAndTopic(authUser, courseId, topicId);
			when(validationService.findReplyInTopic(replyId, topicId)).thenReturn(replyWithAuthor);
			// When
		    ReplyDetailDTO result = replyService.getReply(authUser, courseId, topicId, replyId);
		    // Then
		    assertNotNull(result);
		}
	}

	@Nested
	@DisplayName("createReply tests")
	class CreateReplyTests {

		@Test
		void shouldCreateRootReplySuccessfully() {
			AuthUser authUser = createAuthUserWithUser(10L);
			Long courseId = 101L;
			Long topicId = 201L;
			Long replyId = 301L;
			Reply savedReply = createReplyWithAuthor(replyId);
			ReplyRegisterRequestDTO dto = new ReplyRegisterRequestDTO("Reply body");

			doNothing().when(validationService).ensureCourseHasAccess(authUser, courseId);
			when(validationService.findTopicInCourse(topicId, courseId)).thenReturn(mock(Topic.class));
			when(validationService.findParentReplyInTopic(null, topicId)).thenReturn(null);
			when(replyRepository.save(any(Reply.class))).thenReturn(savedReply);
			
			// When
			ReplyDetailDTO result = replyService.createReply(authUser, courseId, topicId, null, dto);
			assertNotNull(result);
		}

		@Test
		void shouldCreateReplyWhenParentIsAtMaxAllowedDepth() {
			AuthUser authUser = createAuthUserWithUser(10L);
			Long courseId = 101L;
			Long topicId = 201L;
			Long replyId = 351L;
			Long parentReplyId = 301L;
			Reply parent = mock(Reply.class);
			Reply grandParent = mock(Reply.class);
			when(parent.getParent()).thenReturn(grandParent);
			when(grandParent.getParent()).thenReturn(null); // profundidad = 2

			Reply savedReply = createReplyWithAuthor(replyId);
			ReplyRegisterRequestDTO dto = new ReplyRegisterRequestDTO("Child at max depth");

			doNothing().when(validationService).ensureCourseHasAccess(authUser, courseId);
			when(validationService.findTopicInCourse(topicId, courseId)).thenReturn(mock(Topic.class));
			when(validationService.findParentReplyInTopic(parentReplyId, topicId)).thenReturn(parent);
			when(replyRepository.save(any(Reply.class))).thenReturn(savedReply);

			// When
			ReplyDetailDTO result = replyService.createReply(authUser, courseId, topicId, parentReplyId, dto);

			// Then
			assertNotNull(result);
			assertEquals(replyId, result.id());
		}

		@Test
		void shouldThrowReplyDepthLimitExceededWhenParentIsAtDepthLimit() {
			AuthUser authUser = mock(AuthUser.class);
			Long courseId = 101L;
			Long topicId = 201L;
			Long parentReplyId = 301L;

			Reply parent = mock(Reply.class);
			Reply grandParent = mock(Reply.class);
			Reply greatGrandParent = mock(Reply.class);
			when(parent.getParent()).thenReturn(grandParent);
			when(grandParent.getParent()).thenReturn(greatGrandParent);
			when(greatGrandParent.getParent()).thenReturn(null); // profundidad = 3

			ReplyRegisterRequestDTO dto = new ReplyRegisterRequestDTO("Too deep");

			doNothing().when(validationService).ensureCourseHasAccess(authUser, courseId);
			when(validationService.findTopicInCourse(topicId, courseId)).thenReturn(mock(Topic.class));
			when(validationService.findParentReplyInTopic(parentReplyId, topicId)).thenReturn(parent);

			// Then
			assertThrows(
				ReplyDepthLimitExceededException.class,
				() -> replyService.createReply(authUser, courseId, topicId, parentReplyId, dto)
			);
			verify(replyRepository, never()).save(any());
		}

		@Test
		void shouldThrowReplyDepthLimitExceededWhenParentIsWellBeyondDepthLimit() {
			AuthUser authUser = mock(AuthUser.class);
			Long courseId = 101L;
			Long topicId = 201L;
			Long parentReplyId = 301L;

			// Cadena artificialmente profunda
			Reply reply3 = mock(Reply.class); // profundidad 3
			Reply reply2 = mock(Reply.class);
			Reply reply1 = mock(Reply.class);
			Reply root = mock(Reply.class);

			when(reply3.getParent()).thenReturn(reply2);
			when(reply2.getParent()).thenReturn(reply1);
			when(reply1.getParent()).thenReturn(root);
			when(root.getParent()).thenReturn(null);

			ReplyRegisterRequestDTO dto = new ReplyRegisterRequestDTO("Exceeds depth");

			doNothing().when(validationService).ensureCourseHasAccess(authUser, courseId);
			when(validationService.findTopicInCourse(topicId, courseId)).thenReturn(mock(Topic.class));
			when(validationService.findParentReplyInTopic(parentReplyId, topicId)).thenReturn(reply3);

			// Then
			assertThrows(
				ReplyDepthLimitExceededException.class,
				() -> replyService.createReply(authUser, courseId, topicId, parentReplyId, dto)
			);
			verify(replyRepository, never()).save(any());
		}

		@Test
		void shouldCreateChildReplySuccessfully() {
			AuthUser authUser = createAuthUserWithUser(10L);
			Long courseId = 101L;
			Long topicId = 201L;
			Long replyId = 351L;
			Long parentReplyId = 301L;
			Reply parentReply = mock(Reply.class);
			Reply savedReply = createReplyWithAuthor(replyId);
			ReplyRegisterRequestDTO dto = new ReplyRegisterRequestDTO("Reply body");

			doNothing().when(validationService).ensureCourseHasAccess(authUser, courseId);
			when(validationService.findTopicInCourse(topicId, courseId)).thenReturn(mock(Topic.class));
			when(validationService.findParentReplyInTopic(parentReplyId, topicId)).thenReturn(parentReply);
			when(replyRepository.save(any(Reply.class))).thenReturn(savedReply);
			
			// When
			ReplyDetailDTO result = replyService.createReply(authUser, courseId, topicId, parentReplyId, dto);
			assertNotNull(result);
		}
	}

	@Nested
	@DisplayName("getRootReplies tests")
	class GetRootRepliesTests {
		@Test
		void shouldReturnListOfReplyDetailDTO() {
			AuthUser authUser = mock(AuthUser.class);
			Long courseId = 101L;
			Long topicId = 201L;
			Long reply1Id = 301L;
			Long reply2Id = 302L;
			doNothingWhenValidateCourseAndTopic(authUser, courseId, topicId);
			Reply reply1 = createReplyWithAuthor(reply1Id);
			Reply reply2 = createReplyWithAuthor(reply2Id);
			List<Reply> replies = List.of(reply1, reply2);
			when(replyRepository.findByTopicIdAndParentIsNull(topicId)).thenReturn(replies);
			// When
			List<ReplyDetailDTO> result = replyService.getRootReplies(authUser, courseId, topicId);
			// Then
			assertThat(result).hasSize(2);
			assertEquals(reply1Id, result.get(0).id());
			assertThat(result.get(0)).isInstanceOf(ReplyDetailDTO.class);
			assertEquals(reply2Id, result.get(1).id());
			assertThat(result.get(1)).isInstanceOf(ReplyDetailDTO.class);
			verify(replyRepository).findByTopicIdAndParentIsNull(topicId);
		}
	}

	@Nested
	@DisplayName("updateReply tests")
	class UpdateReplyTests {
		@Test
		void shouldUpdateReplyAndReturnReplyDetailDTO() {
			// Given
			AuthUser authUser = mock(AuthUser.class);
			Long courseId = 101L;
			Long topicId = 201L;
			Long replyId = 301L;
			ReplyUpdateRequestDTO dto = new ReplyUpdateRequestDTO("Edited body");
			Reply replyWithAuthor = createReplyWithAuthor(replyId);
			when(replyWithAuthor.getBody()).thenReturn("Edited body");
			doNothingWhenValidateCourseAndTopic(authUser, courseId, topicId);
			when(validationService.findReplyInTopic(replyId, topicId)).thenReturn(replyWithAuthor);
			// When
		    ReplyDetailDTO result = replyService.updateReply(authUser, courseId, topicId, replyId, dto);
		    // Then
		    assertNotNull(result);
		    assertEquals("Edited body", result.body());
		}
	}

	@Nested
	@DisplayName("deleteReply tests")
	class DeleteReplyTests {
		@Test
		void shouldDeleteReplySuccessfully() {
			AuthUser authUser = mock(AuthUser.class);
			Long courseId = 101L;
			Long topicId = 201L;
			Long replyId = 301L;
			doNothingWhenValidateCourseAndTopic(authUser, courseId, topicId);
			//When + Then
			assertDoesNotThrow(() -> replyService.deleteReply(authUser, courseId, topicId, replyId));
		}
	}

	private void doNothingWhenValidateCourseAndTopic(AuthUser authUser, Long courseId, Long topicId) {
		doNothing().when(validationService).ensureCourseHasAccess(authUser, courseId);
		doNothing().when(validationService).ensureTopicBelongToCourse(topicId, courseId);
	}
}
