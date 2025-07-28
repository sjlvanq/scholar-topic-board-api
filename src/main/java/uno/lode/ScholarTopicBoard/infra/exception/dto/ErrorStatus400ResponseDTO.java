package uno.lode.ScholarTopicBoard.infra.exception.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import uno.lode.ScholarTopicBoard.infra.exception.ErrorStatusResponseCodes;

public record ErrorStatus400ResponseDTO(
		@Schema(example = "BAD_REQUEST_400")
		ErrorStatusResponseCodes code,
		List<ErrorStatus400FieldDTO> fields) {
}
