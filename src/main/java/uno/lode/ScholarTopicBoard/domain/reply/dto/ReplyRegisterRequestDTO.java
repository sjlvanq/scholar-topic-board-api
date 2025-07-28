package uno.lode.ScholarTopicBoard.domain.reply.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReplyRegisterRequestDTO(
	@NotBlank @Size(min = 10) String body
) {}