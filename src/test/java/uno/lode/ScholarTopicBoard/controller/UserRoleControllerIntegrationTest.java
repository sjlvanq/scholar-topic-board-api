package uno.lode.ScholarTopicBoard.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;
import uno.lode.ScholarTopicBoard.infra.security.SecurityConfig;
import uno.lode.ScholarTopicBoard.infra.security.TokenService;
import uno.lode.ScholarTopicBoard.service.RoleService;

@WebMvcTest(UserRoleController.class)
@Import(SecurityConfig.class)
class UserRoleControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private RoleService roleService;
	@MockitoBean
	private TokenService tokenService;
	@MockitoBean
	private UserRepository usuarioRepository;
	    
	/***** GetAllRoles *****/
	
	@Test
	@DisplayName("Should return 204 No Content when Admin gets all roles")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnNoContentWhenUserIsAdminOnGetAllRoles() throws Exception {
		mockMvc.perform(get("/users/roles"))
		.andExpect(status().isNoContent());
		//.andDo(r->System.out.println(r.getResponse().getStatus()));
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator gets all roles")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnGetAllRoles() throws Exception {
		mockMvc.perform(get("/users/roles"))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator gets all roles")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnGetAllRoles() throws Exception {
		mockMvc.perform(get("/users/roles"))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher gets all roles")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnGetAllRoles() throws Exception {
		mockMvc.perform(get("/users/roles"))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student gets all roles")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnGetAllRoles() throws Exception {
		mockMvc.perform(get("/users/roles"))
		.andExpect(status().isForbidden());
	}
}
