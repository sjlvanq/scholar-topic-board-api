package uno.lode.ScholarTopicBoard.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserBanStatusUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserCoursesUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserResponseDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRolesUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;
import uno.lode.ScholarTopicBoard.infra.security.SecurityConfig;
import uno.lode.ScholarTopicBoard.infra.security.TokenService;
import uno.lode.ScholarTopicBoard.service.UserService;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private UserService userService;
	@MockitoBean
	private TokenService tokenService;
	@MockitoBean
	private UserRepository usuarioRepository;
	
    private final ObjectMapper mapper = new ObjectMapper();
	
	private final Long USER_ID = 1L;
	private final String USER_FIRST_NAME = "Testiano";
	private final String USER_LAST_NAME = "Testor";
	private final String USER_EMAIL = "test@123.com";
	private final String USER_PASSWORD = "123Passw0";
	
	//TODO: OnGetUser
	
	/***** GetAllUsersByCourse *****/
	
	// NOTE: The endpoint GET /users/by-course/{courseId} does not have security restrictions at the controller level (@RolesAllowed).
	// The authorization logic is implemented in the service layer (UserService), specifically using ServiceUtil.
	// Therefore, access and permission tests for this endpoint are located in UserServiceUnitTest and ServiceUtilTest, not here.

	
	/***** GetAllUsers *****/
	
	@Test @DisplayName("Should return 204 No Content when Admin gets all users")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnNoContentWhenUserIsAdminOnGetAllUsers() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().isNoContent());
		//.andDo(r->System.out.println(r.getResponse().getStatus()));
	}
	
	@Test @DisplayName("Should return 403 Forbidden when Coordinator gets all users")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnGetAllUsers() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator gets all users")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnGetAllUsers() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher gets all users")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnGetAllUsers() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student gets all users")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnGetAllUsers() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().isForbidden());
	}
	
	/***** createUser *****/
	
	@Test
	@DisplayName("Should return 201 Created when Admin creates a user")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnCreatedWhenUserIsAdminOnCreateUser() throws Exception {
    	UserRegisterRequestDTO createRequestDTO =
    			new UserRegisterRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
    	UserResponseDTO responseDTO = new UserResponseDTO(USER_ID, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL);
    	when(userService.createUser(createRequestDTO)).thenReturn(responseDTO);
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isCreated());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator creates a user")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnCreateUser() throws Exception {
    	UserRegisterRequestDTO createRequestDTO =
    			new UserRegisterRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator creates a user")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnCreateUser() throws Exception {
    	UserRegisterRequestDTO createRequestDTO =
    			new UserRegisterRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher creates a user")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnCreateUser() throws Exception {
    	UserRegisterRequestDTO createRequestDTO =
    			new UserRegisterRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when a regular User creates a user")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsUserOnCreateUser() throws Exception {
    	UserRegisterRequestDTO createRequestDTO =
    			new UserRegisterRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	/***** getUser *****/
	// TODO
	
	/***** updateUser *****/
	
	@Test
	@DisplayName("Should return 200 OK when Admin updates a user")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnOkWhenUserIsAdminOnUpdateUser() throws Exception {
    	UserUpdateRequestDTO updateRequestDTO =
    			new UserUpdateRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
    	UserResponseDTO responseDTO = new UserResponseDTO(USER_ID, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL);
    	when(userService.updateUser(USER_ID, updateRequestDTO)).thenReturn(responseDTO);
		mockMvc.perform(put("/users/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 200 OK when Coordinator updates a user") // Asumo que el coordinador puede actualizar usuarios
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnOkWhenUserIsCoordinatorOnUpdateUser() throws Exception {
    	UserUpdateRequestDTO updateRequestDTO =
    			new UserUpdateRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
    	UserResponseDTO responseDTO = new UserResponseDTO(USER_ID, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL);
    	when(userService.updateUser(USER_ID, updateRequestDTO)).thenReturn(responseDTO);
		mockMvc.perform(put("/users/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator updates a user")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnUpdateUser() throws Exception {
    	UserUpdateRequestDTO updateRequestDTO =
    			new UserUpdateRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
		mockMvc.perform(put("/users/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher updates a user")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnUpdateUser() throws Exception {
    	UserUpdateRequestDTO updateRequestDTO =
    			new UserUpdateRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
		mockMvc.perform(put("/users/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student updates a user")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnUpdateUser() throws Exception {
    	UserUpdateRequestDTO updateRequestDTO =
    			new UserUpdateRequestDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD);
		mockMvc.perform(put("/users/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	//***** deleteUser *****/
	
	@Test
	@DisplayName("Should return 204 No Content when Admin deletes a user")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnNoContentWhenUserIsAdminOnDeleteUser() throws Exception {
		mockMvc.perform(delete("/users/{userId}", USER_ID))
		.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator deletes a user")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnDeleteUser() throws Exception {
		mockMvc.perform(delete("/users/{userId}", USER_ID))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator deletes a user")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnDeleteUser() throws Exception {
		mockMvc.perform(delete("/users/{userId}", USER_ID))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher deletes a user")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnDeleteUser() throws Exception {
		mockMvc.perform(delete("/users/{userId}", USER_ID))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student deletes a user")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnDeleteUser() throws Exception {
		mockMvc.perform(delete("/users/{userId}", USER_ID))
		.andExpect(status().isForbidden());
	}
	
	//***** updateUserCourses *****//
	
	@Test
	@DisplayName("Should return 200 OK when Admin updates user courses")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnOkWhenUserIsAdminOnUpdateUsersCourses() throws Exception {
		UserCoursesUpdateRequestDTO coursesUpdateRequest = new UserCoursesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/courses", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(coursesUpdateRequest)))
		.andExpect(status().isOk()); //TODO Unificar retorno (PATCH ban devuelve NoContent)
	}
	
	@Test
	@DisplayName("Should return 200 OK when Coordinator updates user courses")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnOkWhenUserIsCoordinatorOnUpdateUsersCourses() throws Exception {
		UserCoursesUpdateRequestDTO coursesUpdateRequest = new UserCoursesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/courses", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(coursesUpdateRequest)))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator updates user courses")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnUpdateUsersCourses() throws Exception {
		UserCoursesUpdateRequestDTO coursesUpdateRequest = new UserCoursesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/courses", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(coursesUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher updates user courses")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnUpdateUsersCourses() throws Exception {
		UserCoursesUpdateRequestDTO coursesUpdateRequest = new UserCoursesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/courses", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(coursesUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student updates user courses")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnUpdateUsersCourses() throws Exception {
		UserCoursesUpdateRequestDTO coursesUpdateRequest = new UserCoursesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/courses", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(coursesUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	//***** updateUserRoles *****//
	
	@Test
	@DisplayName("Should return 200 OK when Admin updates user roles")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnOkWhenUserIsAdminOnUpdateUserRoles() throws Exception {
		UserRolesUpdateRequestDTO rolesUpdateRequest = new UserRolesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/roles", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(rolesUpdateRequest)))
		.andExpect(status().isOk()); //TODO Unificar retorno (PATCH ban devuelve NoContent)
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator updates user roles")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnUpdateUserRoles() throws Exception {
		UserRolesUpdateRequestDTO rolesUpdateRequest = new UserRolesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/roles", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(rolesUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator updates user roles")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnUpdateUserRoles() throws Exception {
		UserRolesUpdateRequestDTO rolesUpdateRequest = new UserRolesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/roles", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(rolesUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher updates user roles")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnUpdateUserRoles() throws Exception {
		UserRolesUpdateRequestDTO rolesUpdateRequest = new UserRolesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/roles", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(rolesUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student updates user roles")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnUpdateUserRoles() throws Exception {
		UserRolesUpdateRequestDTO rolesUpdateRequest = new UserRolesUpdateRequestDTO(Set.of(1L));
		mockMvc.perform(patch("/users/{userId}/roles", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(rolesUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	//***** updateUserBanStatus *****//
	
	@Test
	@DisplayName("Should return 204 No Content when Admin bans a user")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnNoContentWhenUserIsAdminOnBanUser() throws Exception {
		UserBanStatusUpdateRequestDTO banUpdateRequest = new UserBanStatusUpdateRequestDTO(false);
		mockMvc.perform(patch("/users/{userId}/ban", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(banUpdateRequest)))
		.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator bans a user")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnBanUser() throws Exception {
		UserBanStatusUpdateRequestDTO banUpdateRequest = new UserBanStatusUpdateRequestDTO(false);
		mockMvc.perform(patch("/users/{userId}/ban", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(banUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 204 No Content when Moderator bans a user")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnNoContentWhenUserIsModeratorOnBanUser() throws Exception {
		UserBanStatusUpdateRequestDTO banUpdateRequest = new UserBanStatusUpdateRequestDTO(false);
		mockMvc.perform(patch("/users/{userId}/ban", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(banUpdateRequest)))
		.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher bans a user")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnBanUser() throws Exception {
		UserBanStatusUpdateRequestDTO banUpdateRequest = new UserBanStatusUpdateRequestDTO(false);
		mockMvc.perform(patch("/users/{userId}/ban", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(banUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(roles = RoleConstants.STUDENT)
	@DisplayName("Should return 403 Forbidden when Student bans a user")
	void shouldReturnForbiddenWhenUserIsStudentOnBanUser() throws Exception {
		UserBanStatusUpdateRequestDTO banUpdateRequest = new UserBanStatusUpdateRequestDTO(false);
		mockMvc.perform(patch("/users/{userId}/ban", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(banUpdateRequest)))
		.andExpect(status().isForbidden());
	}
	
}

