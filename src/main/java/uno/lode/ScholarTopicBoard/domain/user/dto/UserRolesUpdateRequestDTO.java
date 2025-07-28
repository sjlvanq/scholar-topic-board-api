package uno.lode.ScholarTopicBoard.domain.user.dto;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserRolesUpdateRequestDTO(
	@NotNull @NotEmpty Set<Long> roleIds) {}
// No se pueden quitar todos los roles al usuario
// TODO: Exception