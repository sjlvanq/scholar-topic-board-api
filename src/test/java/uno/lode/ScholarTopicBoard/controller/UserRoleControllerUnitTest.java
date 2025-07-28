package uno.lode.ScholarTopicBoard.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uno.lode.ScholarTopicBoard.domain.role.dto.RoleAdminResponseDTO;
import uno.lode.ScholarTopicBoard.infra.exception.GlobalExceptionHandler;
import uno.lode.ScholarTopicBoard.service.RoleService;

class UserRoleControllerUnitTest {

	private final Long ROLE_ID = 1L;
	private final String ROLE_NAME = "role-name";
	private final Boolean ROLE_IS_PUBLIC = true;

	@Mock
	private RoleService roleService;

	@InjectMocks
	private UserRoleController roleController;

	private MockMvc mockMvc;

    @BeforeEach
	void setUp() {
		roleService = mock(RoleService.class);
		roleController = new UserRoleController(roleService);
	    mockMvc = MockMvcBuilders
	    			.standaloneSetup(roleController)
	    			.setControllerAdvice(new GlobalExceptionHandler())
	    			.build();
	}

	@Test
	@DisplayName("Should return 200 OK and list all roles on GET request")
	void shouldGetAllRoles() throws Exception {
	    List<RoleAdminResponseDTO> responseList = List.of(
	    		new RoleAdminResponseDTO(ROLE_ID, ROLE_NAME, ROLE_IS_PUBLIC),
	    		new RoleAdminResponseDTO(ROLE_ID+1, ROLE_NAME+"_2", !ROLE_IS_PUBLIC)
	    );
	    when(roleService.getAllRoles()).thenReturn(responseList);

	    mockMvc.perform(get("/users/roles")
	    		.accept(MediaType.APPLICATION_JSON))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.length()").value(responseList.size()))
	        .andExpect(jsonPath("$[0].id").value(ROLE_ID))
	        .andExpect(jsonPath("$[0].name").value(ROLE_NAME))
	        .andExpect(jsonPath("$[0].isPublic").value(ROLE_IS_PUBLIC))
	        .andExpect(jsonPath("$[1].id").value(ROLE_ID+1))
	        .andExpect(jsonPath("$[1].name").value(ROLE_NAME+"_2"))
	        .andExpect(jsonPath("$[1].isPublic").value(!ROLE_IS_PUBLIC));

	    verify(roleService).getAllRoles();
	}

    @Test
	@DisplayName("Should return 403 Forbidden when user is not admin on GET request for all roles")
	void shouldReturnForbiddenWhenUserIsNotAdminOnGetAll() throws Exception {
	    doThrow(new AccessDeniedException("Forbidden")).when(roleService).getAllRoles();
	    mockMvc.perform(get("/users/roles")).andExpect(status().isForbidden());
	    verify(roleService).getAllRoles();
	}

	@Test
	@DisplayName("Should return 204 No Content when no roles exist")
	void shouldReturnNoContentWhenNoRolesExist() throws Exception {
	    when(roleService.getAllRoles()).thenReturn(List.of());

	    mockMvc.perform(get("/users/roles").accept(MediaType.APPLICATION_JSON))
	        .andExpect(status().isNoContent());

	    verify(roleService).getAllRoles();
	}
}
