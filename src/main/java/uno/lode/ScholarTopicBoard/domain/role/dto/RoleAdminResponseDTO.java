package uno.lode.ScholarTopicBoard.domain.role.dto;

import uno.lode.ScholarTopicBoard.domain.role.Role;

public record RoleAdminResponseDTO(Long id, String name, Boolean isPublic) {
	public RoleAdminResponseDTO(Role role) {
		this(role.getId(), role.getName(), role.getIsPublic());
	}
}
