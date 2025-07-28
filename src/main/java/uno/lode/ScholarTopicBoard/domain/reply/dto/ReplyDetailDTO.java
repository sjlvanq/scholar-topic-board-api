package uno.lode.ScholarTopicBoard.domain.reply.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import uno.lode.ScholarTopicBoard.domain.reply.Reply;

public record ReplyDetailDTO(
		Long id,
		String authorName,
		LocalDateTime creationDate,
		LocalDateTime updateDate,
		String body,
		List<ReplyDetailDTO> children
	) {
	public ReplyDetailDTO(Reply reply) {
		this(
			reply.getId(),
			String.join(" ",reply.getAuthor().getFirstName(), reply.getAuthor().getLastName()),
			reply.getCreationDate(),
			reply.getUpdateDate(),
			reply.getBody(),
			reply.getChildren().stream().map(ReplyDetailDTO::new).collect(Collectors.toList())
		);
	}
}
