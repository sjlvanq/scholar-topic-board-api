package uno.lode.ScholarTopicBoard.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uno.lode.ScholarTopicBoard.domain.user.User;

public record UserAuthorDTO(
		@Schema(example = "John")
		String firstName,
		@Schema(example = "Doe")
		String lastName,
		@Schema(example = "john@learning.edu")
		String email) {
	public UserAuthorDTO(User user){
		this(user.getFirstName(), user.getLastName(), user.getEmail());
	}
}
