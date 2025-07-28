package uno.lode.ScholarTopicBoard.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(
		@Size(min = 3, max = 50) String firstName,
		@Size(min = 3, max = 35) String lastName,
		@Size(max = 255) @Email String email,
		@Size(min = 6, message = "Password must be at least 6 characters")
		@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$",
	    	message = "Password must contain at least one letter and one digit") String password
	) {
}
