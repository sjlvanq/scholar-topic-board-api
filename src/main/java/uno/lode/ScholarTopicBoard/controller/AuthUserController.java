package uno.lode.ScholarTopicBoard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import uno.lode.ScholarTopicBoard.infra.exception.dto.ErrorStatus400ResponseDTO;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.infra.security.AuthUserService;
import uno.lode.ScholarTopicBoard.infra.security.LoginRequestDTO;
import uno.lode.ScholarTopicBoard.infra.security.TokenDTO;
import uno.lode.ScholarTopicBoard.infra.security.TokenService;

@Tag(name = "Authentication", description = "Authentication and token handling")

@RestController
@RequestMapping("/login")
public class AuthUserController {

	private TokenService tokenService;
	private AuthenticationManager authenticationManager;
	private AuthUserService authUserService;
	public AuthUserController(TokenService tokenService, AuthenticationManager authenticationManager, AuthUserService authUserService) {
		this.tokenService = tokenService;
		this.authenticationManager = authenticationManager;
		this.authUserService = authUserService;
	}

	@Operation(
		summary = "User login",
		description = "Authenticates a user with email and password. Returns a JWT token upon success.")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "User logged successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorStatus400ResponseDTO.class),
	    	examples = {
	    		@ExampleObject(name = "MALFORMED_400",
	    			value = "{\"code\":\"MALFORMED_400\",\"fields\":[{\"field\":\"general\",\"message\":\"Malformed request\"}]}",
	    			description = "Malformed request."),
	    		@ExampleObject(name = "BAD_REQUEST_400 (Invalid Email)",
    				value = "{\"code\":\"BAD_REQUEST_400\",\"fields\":[{\"field\":\"email\",\"message\":\"must be a well-formed email address\"}]}",
    				description = "Invalid Email."),
	    		@ExampleObject(name = "BAD_REQUEST_400 (Multiple invalid)",
					value = "{\"code\":\"BAD_REQUEST_400\",\"fields\":[{\"field\":\"email\",\"message\":\"must be a well-formed email address\"},{\"field\":\"password\",\"message\":\"must not be empty\"}]}",
					description = "Multiple invalid fields.")
	    	})),
	    @ApiResponse(responseCode = "401", description = "Unauthorized",
	    	content = @Content(mediaType = "application/json",
	    	examples = {
	    		@ExampleObject(name = "BAD_CREDENTIALS_401", 
					value = "{\"code\": \"BAD_CREDENTIALS_401\",\"message\": \"Bad user email or password.\"}",
	    		    description = "Returned when the provided email or password is invalid."),
	    		}
	    	)),
	    @ApiResponse(responseCode = "403", description = "Access denied", 
	    	content = @Content(
	    			mediaType = "application/json",
	    			examples = {
	    				@ExampleObject(name = "NOT_ENROLLED_403",
	    					value = "{\"code\": \"NOT_ENROLLED_403\", \"message\": \"User is not enrolled in any course yet. Enrollment must be done by an administrator or coordinator.\"}",
	    					description = "Users whose highest role is moderator, teacher, or student and who are not enrolled in any course will not be able to complete the authentication process."),
	    				@ExampleObject(name = "USER_BANNED_403",
    						value = "{\"code\": \"USER_BANNED_403\", \"message\": \"User is banned. Check your email for details.\"}",
    						description = "The user has been banned")
	    			}))
	})

	@PostMapping
	public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginRequestDTO authData) {
		UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(authData.email(), authData.password());
		Authentication auth = authenticationManager.authenticate(authToken);
		
		AuthUser user = (AuthUser) auth.getPrincipal();
		authUserService.validateAccess(user);
		
		String tokenJWT = tokenService.createToken(user);
		return ResponseEntity.ok(new TokenDTO(tokenJWT));
	}
	
}
