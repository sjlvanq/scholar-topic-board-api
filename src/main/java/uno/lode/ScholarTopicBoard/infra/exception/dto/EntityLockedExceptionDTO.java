package uno.lode.ScholarTopicBoard.infra.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uno.lode.ScholarTopicBoard.infra.exception.ErrorStatusResponseCodes;

public record EntityLockedExceptionDTO(
		@Schema(example = "LOCKED_423") ErrorStatusResponseCodes code,
		String message
) {}