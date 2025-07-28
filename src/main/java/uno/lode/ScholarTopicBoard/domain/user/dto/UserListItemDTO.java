package uno.lode.ScholarTopicBoard.domain.user.dto;

import java.util.List;

import uno.lode.ScholarTopicBoard.domain.role.dto.RolePublicResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.User;

public record UserListItemDTO(
		Long id,
		String firstName,
		String lastName,
		String email,
		List<RolePublicResponseDTO> roles
		) {
	public UserListItemDTO(User user) {
		this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
			user.getRoles() != null ? 
				user.getRoles().stream().map(RolePublicResponseDTO::new).toList() : List.of());
	}

	public UserListItemDTO(User user, List<RolePublicResponseDTO> visibleRoles) {
		this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
				visibleRoles != null ? visibleRoles : List.of());
	}
}
