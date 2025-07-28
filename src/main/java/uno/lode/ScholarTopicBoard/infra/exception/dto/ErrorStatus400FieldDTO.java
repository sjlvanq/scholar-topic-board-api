package uno.lode.ScholarTopicBoard.infra.exception.dto;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public record ErrorStatus400FieldDTO(String field, String message) {
	public ErrorStatus400FieldDTO(ObjectError error) {
		this(
				(error instanceof FieldError fe) ? fe.getField() : error.getObjectName(),
						error.getDefaultMessage()); // error.getCode() returns the validation key
	}
}
