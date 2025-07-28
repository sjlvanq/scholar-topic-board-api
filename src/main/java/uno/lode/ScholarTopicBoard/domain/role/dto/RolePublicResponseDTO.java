package uno.lode.ScholarTopicBoard.domain.role.dto;

import uno.lode.ScholarTopicBoard.domain.role.Role;

public record RolePublicResponseDTO(Long id, String name) {
	public RolePublicResponseDTO(Role role) {
		this(role.getId(), role.getName());
	}
}
