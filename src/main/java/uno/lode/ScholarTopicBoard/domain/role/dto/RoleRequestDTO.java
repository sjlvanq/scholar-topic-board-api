package uno.lode.ScholarTopicBoard.domain.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequestDTO(
		@NotBlank @Size(min = 4, max = 30) String name,
		Boolean isPublic) {
}
