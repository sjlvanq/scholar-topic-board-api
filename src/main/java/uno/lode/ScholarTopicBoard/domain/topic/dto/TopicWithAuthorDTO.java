package uno.lode.ScholarTopicBoard.domain.topic.dto;

import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserAuthorDTO;

public record TopicWithAuthorDTO(
		Long id,
		UserAuthorDTO author,
		String title,
		String creationDate,
		String updateDate,
		String body,
		Boolean closed) {
	public TopicWithAuthorDTO(Topic topic) {
		this(
			topic.getId(),
			new UserAuthorDTO(topic.getAuthor()),
			topic.getTitle(),
			topic.getCreationDate().toString(),
			topic.getUpdateDate().toString(),
			topic.getBody(),
			topic.isClosed()
		);
	}
}
