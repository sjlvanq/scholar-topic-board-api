package uno.lode.ScholarTopicBoard.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.role.dto.RolePublicResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.UserListFilter;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserBanStatusUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserCoursesUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserDetailDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserListItemDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRolesUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;
import uno.lode.ScholarTopicBoard.infra.exception.dto.EntityAlreadyExistsExceptionDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.ErrorStatus400ResponseDTO;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.service.UserService;

@Tag(name = "Users", description = "User-related operations")

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearer-key")
public class UserController {

	private UserService userService;
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "Get list of all visible users (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserListItemDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No users found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
	
    @GetMapping
    @RolesAllowed(RoleConstants.ADMIN)
    public ResponseEntity<List<UserListItemDTO>> getAllUsers(
    		@RequestParam(defaultValue="ACTIVE") UserListFilter includes
    	){
    	List<UserListItemDTO> allUsers = userService.getAllUsers(includes);
    	return allUsers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(allUsers);
    }
	

	@Operation(summary = "List users in a course (Admin, Coordinator or Enrolled user only)",
			description = """
				* Non-admin users will only see public roles of the requested users.
				* Banned users will be included in the response.	
					""")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserListItemDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No users found", content = @Content),
	    @ApiResponse(responseCode = "400", description = "The Course id provided is not a number",
	    	content = @Content(mediaType = "application/json",
	    	examples = @ExampleObject(
	    		name = "BAD_PATHVARIABLE_400 (CourseId)",
    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
    			description = "Invalid courseId argument."),
	    	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Not found",
    		content = @Content(mediaType = "application/json",
    		examples = @ExampleObject(
    				name = "NOT_FOUND_404 (Course)", description = "Course not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}")))
    })
	
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<UserListItemDTO>> getAllUsersByCourse(
    		@AuthenticationPrincipal AuthUser authUser,
    		@PathVariable Long courseId){
    	List<UserListItemDTO> usersInCourse = userService.getAllUsersByCourse(authUser, courseId);
    	return usersInCourse.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(usersInCourse);
    }
	
	
	@Operation(summary = "Register a new user (Admin only)")
	@ApiResponses({
	    @ApiResponse(responseCode = "201", description = "User created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "409", description = "Conflict error",
        	content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityAlreadyExistsExceptionDTO.class)))
	})
	
	@PostMapping
    @RolesAllowed(RoleConstants.ADMIN)
	public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRegisterRequestDTO userData,
			UriComponentsBuilder uriComponentsBuilder){

		UserResponseDTO user = userService.createUser(userData);
        URI url = uriComponentsBuilder.path("/users/{id}").buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(url).body(user);
	}
	

	@Operation(summary = "Retrieve user by ID (Admin, Coordinator or course partner)",
			description = """
				* Non-admin users will only see public roles of the requested user.
				* Access requires admin/coordinator privileges OR shared course enrollment.
				""")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "User retrieved successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "The User id provided is not a number",
	    	content = @Content(mediaType = "application/json",
	    	examples = @ExampleObject(
	    		name = "BAD_PATHVARIABLE_400 (UserId)",
	    		value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"userId\",\"message\":\"Invalid userId argument\"}]}",
	    		description = "Invalid userId argument."),
	    	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "User not found",
	    	content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"User with id ? not found!\"\n}")))
	})
	
    @GetMapping("/{userId}")
	public ResponseEntity<UserDetailDTO> getUser(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUserById(authUser, userId));
    }

	
	@Operation(summary = "Update user information (Admin or Coordinator only)")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "User updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
	    @ApiResponse(responseCode = "400", description = "The User id provided is not a number",
	    	content = @Content(mediaType = "application/json",
	    	examples = @ExampleObject(
	    		name = "BAD_PATHVARIABLE_400 (UserId)",
    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"userId\",\"message\":\"Invalid userId argument\"}]}"),
	    	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "User not found",
	    	content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"User with id ? not found!\"\n}")))
	})
	
	@PutMapping("/{userId}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.COORD})
	public ResponseEntity<UserResponseDTO> updateUser(
    		@PathVariable Long userId,
    		@RequestBody @Valid UserUpdateRequestDTO userData){
    	return ResponseEntity.ok(userService.updateUser(userId, userData));
    }

	
	@Operation(summary = "Delete user (Admin only)")
	@ApiResponses({ 
	    @ApiResponse(responseCode = "204", description = "User successfully deleted", content=@Content),
	    @ApiResponse(responseCode = "400", description = "The User id provided is not a number",
    		content = @Content(mediaType = "application/json",
    		examples = @ExampleObject(
    			name = "BAD_PATHVARIABLE_400 (UserId)",
    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"userId\",\"message\":\"Invalid userId argument\"}]}",
    			description = "Invalid userId argument."),
    		schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content),
	    @ApiResponse(responseCode = "404", description = "User not found",
	    	content = @Content(mediaType = "application/json",
	        examples = @ExampleObject(
	        		name = "NOT_FOUND_404 (User)", description = "User not found.",
	        		value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"User with id ? not found!\"\n}")))
	    /*
	    @ApiResponse(responseCode = "423", description = "Locked error",
    		content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"LOCKED_423\",\n  \"message\": \"The user cannot be deleted due to the existence of...\"\n}")))
	    */
	})
	
	@DeleteMapping("/{userId}")
    @RolesAllowed({RoleConstants.ADMIN})
	public ResponseEntity<?> deleteUser(@PathVariable Long userId){
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}
	
	
	@Operation(summary = "Assign or update user's courses (Admin or Coordinator only)")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "User courses updated successfully",
	    	content = @Content(mediaType = "application/json",
        	array = @ArraySchema(schema = @Schema(implementation = CourseDetailDTO.class)))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	    	content = @Content(mediaType = "application/json",
	    	examples = @ExampleObject(
    			name = "BAD_PATHVARIABLE_400 (UserId)",
    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"userId\",\"message\":\"Invalid userId argument\"}]}", 
    			description = "Returned when the User id provided is not a number."),
	    	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "User not found",
    		content = @Content(mediaType = "application/json",
        	examples = @ExampleObject(
        			name = "NOT_FOUND_404 (User)", description = "User not found.",
        			value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"User with id ? not found!\"\n}")))
	})

	@PatchMapping("/{userId}/courses")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.COORD})
	public ResponseEntity<List<CourseDetailDTO>> updateUserCourses(
			@PathVariable Long userId,
			@RequestBody @Valid UserCoursesUpdateRequestDTO userCourses){
		List<CourseDetailDTO> courses = userService.updateUserCourses(userId, userCourses);
		//return courses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(courses);
		return ResponseEntity.ok(courses);
	}
	
	
	@Operation(summary = "Assign or update user's roles (Admin only)")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "User roles updated successfully",
		    content = @Content(mediaType = "application/json",
		    array = @ArraySchema(schema = @Schema(implementation = RolePublicResponseDTO.class)))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	    	content = @Content(mediaType = "application/json",
	    	examples = @ExampleObject(
    			name = "BAD_PATHVARIABLE_400 (UserId)",
    			value = "{\"code\":\"BAD_REQUEST_400\",\"fields\":[{\"field\":\"userId\",\"message\":\"Invalid userId argument\"}]}", 
    			description = "Returned when the userId provided is not a number."),
	    	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "User not found",
    		content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"User with id ? not found!\"\n}")))
	})
	
	@PatchMapping("/{userId}/roles")
    @RolesAllowed(RoleConstants.ADMIN)
	public ResponseEntity<List<RolePublicResponseDTO>> updateUserRoles(
			@PathVariable Long userId,
			@RequestBody @Valid UserRolesUpdateRequestDTO userRoles){
		return ResponseEntity.ok(userService.updateUserRoles(userId, userRoles));
	}
	
	
	@Operation(summary = "Ban/unban user (Admin or Moderator only)")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "User ban status updated successfully",
		    content = @Content(mediaType = "application/json")),
	    @ApiResponse(responseCode = "400", description = "Bad request",
    		content = @Content(mediaType = "application/json",
    		examples = @ExampleObject(
    			name = "BAD_PATHVARIABLE_400 (UserId)",
    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"userId\",\"message\":\"Invalid userId argument\"}]}", 
    			description = "Returned when the userId provided is not a number."),
    		schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "User not found",
    		content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"User with id ? not found!\"\n}")))
	})
	
	@PatchMapping("/{userId}/ban")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.MODERATOR})
	public ResponseEntity<?> banUser(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long userId,
			@RequestBody @Valid UserBanStatusUpdateRequestDTO userBanStatus){
		userService.updateUserBanStatus(authUser, userId, userBanStatus);
		return ResponseEntity.noContent().build();
	}
	
}
