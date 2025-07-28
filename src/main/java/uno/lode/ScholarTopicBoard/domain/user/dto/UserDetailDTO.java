package uno.lode.ScholarTopicBoard.domain.user.dto;

import java.util.List;

import uno.lode.ScholarTopicBoard.domain.course.dto.CourseSummaryDTO;
import uno.lode.ScholarTopicBoard.domain.role.dto.RolePublicResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.User;

public record UserDetailDTO(
		Long id,
		String firstName,
		String lastName,
		String email,
		List<RolePublicResponseDTO> roles,
		List<CourseSummaryDTO> courses
		) {
	
	public UserDetailDTO(User user) {
	    this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
	        user.getRoles() != null ? 
	            user.getRoles().stream().map(RolePublicResponseDTO::new).toList() : List.of(),
	        user.getCourses() != null ? 
	            user.getCourses().stream().map(CourseSummaryDTO::new).toList() : List.of());
	}
}
