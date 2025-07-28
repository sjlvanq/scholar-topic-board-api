package uno.lode.ScholarTopicBoard.infra.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uno.lode.ScholarTopicBoard.infra.exception.ErrorStatusResponseCodes;

public record EntityNotFoundExceptionDTO(
		@Schema(example = "NOT_FOUND_404")
		ErrorStatusResponseCodes code,
		String message
) {}
