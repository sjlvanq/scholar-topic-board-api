package uno.lode.ScholarTopicBoard.infra.exception.dto;

import uno.lode.ScholarTopicBoard.infra.exception.ErrorStatusResponseCodes;

public record ErrorStatusResponseDTO(ErrorStatusResponseCodes code, String message) {

}
