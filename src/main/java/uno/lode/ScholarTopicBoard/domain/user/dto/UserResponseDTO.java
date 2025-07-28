package uno.lode.ScholarTopicBoard.domain.user.dto;

import uno.lode.ScholarTopicBoard.domain.user.User;

public record UserResponseDTO(
	Long id,
	String firstName,
	String lastName,
	String email) {
	public UserResponseDTO(User user){
		this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
	}
}
