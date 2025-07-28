package uno.lode.ScholarTopicBoard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserCoursesUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserDetailDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRolesUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.exception.ErrorStatusResponseCodes;
import uno.lode.ScholarTopicBoard.infra.exception.GlobalExceptionHandler;
import uno.lode.ScholarTopicBoard.infra.exception.user.UserAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.user.UserNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.service.UserService;

class UserControllerUnitTest {
	@InjectMocks
	private UserController userController;
	@Mock
	private UserService userService;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
	private final String endpoint = "/users";
	private AuthUser mockAuthUser;
	
	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		userController = new UserController(userService);
		mockAuthUser = new AuthUser( new User(1L, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD, false,
				false, List.of(), List.of()));

		HandlerMethodArgumentResolver mockResolver = new HandlerMethodArgumentResolver() {
	        @Override
	        public boolean supportsParameter(MethodParameter parameter) {
	            return parameter.getParameterType().equals(AuthUser.class);
	        }

	        @Override
	        public Object resolveArgument(MethodParameter parameter,
	                                      ModelAndViewContainer mavContainer,
	                                      NativeWebRequest webRequest,
	                                      WebDataBinderFactory binderFactory) {
	            return mockAuthUser;
			}
	    };

	    mockMvc = MockMvcBuilders
	    			.standaloneSetup(userController)
	    			.setControllerAdvice(new GlobalExceptionHandler())
	    			.setCustomArgumentResolvers(mockResolver)
	    			.build();
	}
	
	private final Long USER_ID = 1L;
	private final String USER_FIRST_NAME = "Testiano";
	private final String USER_LAST_NAME = "Testor";
	private final String USER_EMAIL = "test@123.com";
	private final String USER_PASSWORD = "123Passw0";

    @Test
    @DisplayName("Should return 201 and created user data for valid POST request")
    void shouldCreateUser() throws Exception {
    	UserRegisterRequestDTO createRequestDTO =
    			new UserRegisterRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
    	UserResponseDTO responseDTO = createUserResponseDTO();

    	when(userService.createUser(createRequestDTO)).thenReturn(responseDTO);

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(createRequestDTO))
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(USER_ID))
        .andExpect(jsonPath("$.firstName").value(USER_FIRST_NAME))
        .andExpect(jsonPath("$.lastName").value(USER_LAST_NAME))
        .andExpect(jsonPath("$.email").value(USER_EMAIL));

        verify(userService).createUser(createRequestDTO);
    }

    @Test
    @DisplayName("Should return 200 and updated user data for valid PUT request")
    void shouldUpdateUser() throws Exception {
    	UserUpdateRequestDTO updateRequestDTO =
    			new UserUpdateRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
    	UserResponseDTO responseDTO = createUserResponseDTO();

    	when(userService.updateUser(USER_ID, updateRequestDTO)).thenReturn(responseDTO);

        mockMvc.perform(put("/users/{userId}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequestDTO))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(USER_ID))
        .andExpect(jsonPath("$.firstName").value(USER_FIRST_NAME))
        .andExpect(jsonPath("$.lastName").value(USER_LAST_NAME))
        .andExpect(jsonPath("$.email").value(USER_EMAIL));

        verify(userService).updateUser(USER_ID, updateRequestDTO);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    @DisplayName("Should return 200 and the requested user")
    void shouldGetUser() throws Exception {
    	UserDetailDTO responseDTO = new UserDetailDTO(USER_ID, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, null, null);
    	when(userService.getUserById((AuthUser) any(AuthUser.class), eq(USER_ID))).thenReturn(responseDTO);

        mockMvc.perform(get("/users/{userId}", USER_ID)
        		.contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(USER_ID))
        .andExpect(jsonPath("$.firstName").value(USER_FIRST_NAME))
        .andExpect(jsonPath("$.lastName").value(USER_LAST_NAME))
        .andExpect(jsonPath("$.email").value(USER_EMAIL));
        //TODO:
        // -ROLES
        // -COURSES
        //.andExpect(mapper.)
        
        verify(userService).getUserById(any(AuthUser.class), eq(USER_ID));
    }
    
    /******************************************************/

    @Test
    @DisplayName("Should return 404 Not Found when user id is invalid on GET")
    void shouldReturnNotFoundWhenUserIdIsInvalidOnGetOne() throws Exception {
    	Long invalidId = 999L;
    	when(userService.getUserById(any(AuthUser.class), eq(invalidId))).thenThrow(new UserNotFoundException(invalidId));
        		mockMvc.perform(get("/users/{userId}", invalidId))
        	.andExpect(status().isNotFound())
        	.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));
        verify(userService).getUserById(any(AuthUser.class), eq(invalidId));
    }
    
    @Test
    @DisplayName("Should return 404 Not Found when user id is invalid on PUT request")
    void shouldReturnNotFoundWhenUserIdIsInvalidOnPut() throws Exception {
    	Long invalidId = 999L;
    	UserUpdateRequestDTO updateRequestDTO = new UserUpdateRequestDTOBuilder().build();
    	when(userService.updateUser(invalidId, updateRequestDTO)).thenThrow(new UserNotFoundException(invalidId));
        		mockMvc.perform(put("/users/{userId}", invalidId).contentType(MediaType.APPLICATION_JSON)
        			.content(mapper.writeValueAsBytes(updateRequestDTO)))
        	.andExpect(status().isNotFound())
        	.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));
        verify(userService).updateUser(Mockito.eq(invalidId), Mockito.eq(updateRequestDTO));
    }
    
    /******************************************************/

    @Test
	@DisplayName("Should return 400 Bad Request when first name is too short on POST")
	void shouldReturnBadRequestWhenFirstNameIsTooShortInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withFirstName(Strings.left(USER_FIRST_NAME,2)).build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when first name is too long on POST")
	void shouldReturnBadRequestWhenFirstNameIsTooLongInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withFirstName(Strings.repeat("*", 51)).build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when first name is blank on POST")
	void shouldReturnBadRequestWhenFirstNameIsBlankInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withFirstName("    ").build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when last name is too short on POST")
	void shouldReturnBadRequestWhenLastNameIsTooShortInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withLastName(Strings.left(USER_LAST_NAME,2)).build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when last name is too long on POST")
	void shouldReturnBadRequestWhenLastNameIsTooLongInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withLastName(Strings.repeat("*", 51)).build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when last name is blank on POST")
	void shouldReturnBadRequestWhenLastNameIsBlankInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withLastName("    ").build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when email is too long on POST")
	void shouldReturnBadRequestWhenEmailIsTooLongInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withEmail(Strings.repeat("A",255)+"@123.com").build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when email is invalid on POST")
	void shouldReturnBadRequestWhenEmailIsInvalidInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withEmail("123456789").build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when email is blank on POST")
	void shouldReturnBadRequestWhenEmailIsBlankInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withEmail("    ").build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when password is blank on POST")
	void shouldReturnBadRequestWhenPasswordIsBlankInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withPassword("    ").build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when password is too short on POST")
	void shouldReturnBadRequestWhenPasswordIsTooShortInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withPassword("$Aa1").build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when password is too weak on POST")
	void shouldReturnBadRequestWhenPasswordIsTooWeakInPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = 
			new UserRegisterRequestDTOBuilder().withPassword("abcdefgh").build();
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    /******************************************************/
    
    @Test
	@DisplayName("Should return 400 Bad Request when first name is too short on PUT")
	void shouldReturnBadRequestWhenFirstNameIsTooShortInPut() throws Exception {
		UserUpdateRequestDTO createRequestDTO = 
			new UserUpdateRequestDTOBuilder().withFirstName(Strings.left(USER_FIRST_NAME,2)).build();
		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when first name is too long on PUT")
	void shouldReturnBadRequestWhenFirstNameIsTooLongInPut() throws Exception {
		UserUpdateRequestDTO createRequestDTO = 
			new UserUpdateRequestDTOBuilder().withFirstName(Strings.repeat("*", 51)).build();
		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
       
    @Test
	@DisplayName("Should return 400 Bad Request when last name is too short on PUT")
	void shouldReturnBadRequestWhenLastNameIsTooShortInPut() throws Exception {
		UserUpdateRequestDTO createRequestDTO = 
			new UserUpdateRequestDTOBuilder().withLastName(Strings.left(USER_LAST_NAME,2)).build();
		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when last name is too long on PUT")
	void shouldReturnBadRequestWhenLastNameIsTooLongInPut() throws Exception {
		UserUpdateRequestDTO createRequestDTO = 
			new UserUpdateRequestDTOBuilder().withLastName(Strings.repeat("*", 51)).build();
		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
       
    @Test
	@DisplayName("Should return 400 Bad Request when email is too long on PUT")
	void shouldReturnBadRequestWhenEmailIsTooLongInPut() throws Exception {
		UserUpdateRequestDTO createRequestDTO = 
			new UserUpdateRequestDTOBuilder().withEmail(Strings.repeat("A",255)+"@123.com").build();
		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when email is invalid on PUT")
	void shouldReturnBadRequestWhenEmailIsInvalidInPut() throws Exception {
		UserUpdateRequestDTO createRequestDTO = 
			new UserUpdateRequestDTOBuilder().withEmail("123456789").build();
		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when password is too short on PUT")
	void shouldReturnBadRequestWhenPasswordIsTooShortInPut() throws Exception {
		UserUpdateRequestDTO createRequestDTO = 
			new UserUpdateRequestDTOBuilder().withPassword("$Aa1").build();
		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    @Test
	@DisplayName("Should return 400 Bad Request when password is too weak on PUT")
	void shouldReturnBadRequestWhenPasswordIsTooWeakInPut() throws Exception {
		UserUpdateRequestDTO createRequestDTO = 
			new UserUpdateRequestDTOBuilder().withPassword("abcdefgh").build();
		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());
		verify(userService, Mockito.never()).createUser(Mockito.any());
	}
    
    /******************************************************/

	@Test
	@DisplayName("Should return 403 Forbidden when user is not admin on POST")
	void shouldReturnForbiddenWhenUserIsNotAdminOnPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = new UserRegisterRequestDTOBuilder().build();
		doThrow(new AccessDeniedException("Forbidden"))
	        .when(userService).createUser(eq(createRequestDTO));

	    mockMvc.perform(post("/users")
	    	.contentType("application/json")
	    	.content(mapper.writeValueAsString(createRequestDTO)))
	        .andExpect(status().isForbidden());

	    verify(userService).createUser(eq(createRequestDTO));
	}

	@Test
	@DisplayName("Should return 403 Forbidden when user is not admin on PUT")
	void shouldReturnForbiddenWhenUserIsNotAdminOnPut() throws Exception {
		UserUpdateRequestDTO updateRequestDTO = new UserUpdateRequestDTOBuilder().build();
		doThrow(new AccessDeniedException("Forbidden"))
	        .when(userService).updateUser(eq(USER_ID), eq(updateRequestDTO));

	    mockMvc.perform(put("/users/{userId}", USER_ID)
	    	.contentType("application/json")
	    	.content(mapper.writeValueAsString(updateRequestDTO)))
	        .andExpect(status().isForbidden());

	    verify(userService).updateUser(eq(USER_ID), eq(updateRequestDTO));
	}

	@Test
	@DisplayName("Should return 403 Forbidden when user is not admin in DELETE request")
	void shouldReturnForbiddenWhenUserIsNotAdminOnDelete() throws Exception {
		// TODO: For implement in controller
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when user is not admin on PUT for roles update")
	void shouldReturnForbiddenWhenUserIsNotAdminOnPutRoles() throws Exception {
		UserRolesUpdateRequestDTO updateRolesRequest = new UserRolesUpdateRequestDTO(Set.of(1L, 2L, 3L));
		doThrow(new AccessDeniedException("Forbidden"))
        	.when(userService).updateUserRoles(USER_ID, updateRolesRequest);
	    mockMvc.perform(patch("/users/{userId}/roles", USER_ID)
		    	.contentType("application/json")
		    	.content(mapper.writeValueAsString(updateRolesRequest)))
		        .andExpect(status().isForbidden());
		verify(userService).updateUserRoles(eq(USER_ID), eq(updateRolesRequest));
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when user is not admin on PUT for courses update")
	void shouldReturnForbiddenWhenUserIsNotAdminOnPutCourses() throws Exception {
		UserCoursesUpdateRequestDTO updateCoursesRequest = new UserCoursesUpdateRequestDTO(Set.of(1L, 2L, 3L));
		doThrow(new AccessDeniedException("Forbidden"))
        	.when(userService).updateUserCourses(USER_ID, updateCoursesRequest);
	    mockMvc.perform(patch("/users/{userId}/courses", USER_ID)
		    	.contentType("application/json")
		    	.content(mapper.writeValueAsString(updateCoursesRequest)))
		        .andExpect(status().isForbidden());
		verify(userService).updateUserCourses(eq(USER_ID), eq(updateCoursesRequest));
	}
	
    /******************************************************/
	
	@Test
	@DisplayName("Should return 409 Conflict when user email already exists on POST request")
	void shouldReturnConflictWhenUserEmailAlreadyExistsOnPost() throws Exception {
		UserRegisterRequestDTO createRequestDTO = new UserRegisterRequestDTOBuilder().build();
		when(userService.createUser(createRequestDTO)).thenThrow(new UserAlreadyExistsException(USER_EMAIL));

		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.CONFLICT_409.toString()));
		verify(userService).createUser(eq(createRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 409 Conflict when user email already exists on PUT request")
	void shouldReturnConflictWhenUserEmailAlreadyExistsOnPut() throws Exception {
		UserUpdateRequestDTO updateRequestDTO = new UserUpdateRequestDTOBuilder().build();
		when(userService.updateUser(USER_ID, updateRequestDTO)).thenThrow(new UserAlreadyExistsException(USER_EMAIL));

		mockMvc.perform(put("/users/{userId}", USER_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.CONFLICT_409.toString()));
		verify(userService).updateUser(eq(USER_ID), eq(updateRequestDTO));
	}
	
	/******************************************************/
	
    private class UserRegisterRequestDTOBuilder {
    	private String firstName = USER_FIRST_NAME;
    	private String lastName = USER_LAST_NAME;
    	private String email = USER_EMAIL;
    	private String password = USER_PASSWORD;
    	public UserRegisterRequestDTOBuilder withFirstName(String firstName) {this.firstName = firstName; return this;}
    	public UserRegisterRequestDTOBuilder withLastName(String lastName) {this.lastName = lastName; return this;}
    	public UserRegisterRequestDTOBuilder withEmail(String email) {this.email=email; return this;}
    	public UserRegisterRequestDTOBuilder withPassword(String password) {this.password = password; return this;}
    	public UserRegisterRequestDTO build() {
    		return new UserRegisterRequestDTO(firstName, lastName, email, password);
    	}
    }
    
    private class UserUpdateRequestDTOBuilder {
    	private String firstName = USER_FIRST_NAME;
    	private String lastName = USER_LAST_NAME;
    	private String email = USER_EMAIL;
    	private String password = USER_PASSWORD;
    	public UserUpdateRequestDTOBuilder withFirstName(String firstName) {this.firstName = firstName; return this;}
    	public UserUpdateRequestDTOBuilder withLastName(String lastName) {this.lastName = lastName; return this;}
    	public UserUpdateRequestDTOBuilder withEmail(String email) {this.email=email; return this;}
    	public UserUpdateRequestDTOBuilder withPassword(String password) {this.password = password; return this;}
    	public UserUpdateRequestDTO build() {
    		return new UserUpdateRequestDTO(firstName, lastName, email, password);
    	}
    }
    
    private UserResponseDTO createUserResponseDTO() {
    	return new UserResponseDTO(USER_ID, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL);
    }
}