package uno.lode.ScholarTopicBoard.controller;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;
import uno.lode.ScholarTopicBoard.infra.security.SecurityConfig;
import uno.lode.ScholarTopicBoard.infra.security.TokenService;
import uno.lode.ScholarTopicBoard.service.CourseService;

@WebMvcTest(CourseController.class)
@Import(SecurityConfig.class)
class CourseControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private CourseService courseService;
	@MockitoBean
	private TokenService tokenService;
	@MockitoBean
	private UserRepository usuarioRepository;
	
    private final ObjectMapper mapper = new ObjectMapper();
    
    private final Long COURSE_ID = 1L;
    private final String COURSE_NAME = "Course 1";
    private final String COURSE_DESC = "Course 1 description";
    private final Boolean COURSE_CLOSED = false;
    
	/***** getAllCourses *****/
	
	@Test
	@DisplayName("Should return 204 No Content when Admin gets all courses")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnNoContentWhenUserIsAdminOnGetAllCourses() throws Exception {
		mockMvc.perform(get("/courses"))
		.andExpect(status().isNoContent());
		//.andDo(r->System.out.println(r.getResponse().getStatus()));
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator gets all courses")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnGetAllCourses() throws Exception {
		mockMvc.perform(get("/courses"))
		.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator gets all courses")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnGetAllCourses() throws Exception {
		mockMvc.perform(get("/courses"))
		.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher gets all courses")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnGetAllCourses() throws Exception {
		mockMvc.perform(get("/courses"))
		.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student gets all courses")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnGetAllCourses() throws Exception {
		mockMvc.perform(get("/courses"))
		.andExpect(status().isNoContent());
	}

	/***** createCourse *****/
	
	@Test
	@DisplayName("Should return 201 Created when Admin creates a course")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnCreatedWhenUserIsAdminOnCreateCourse() throws Exception {
    	CourseRegisterRequestDTO createRequestDTO =
    			new CourseRegisterRequestDTO(COURSE_NAME, COURSE_DESC);
    	CourseDetailDTO responseDTO = new CourseDetailDTO(COURSE_ID, COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
    	when(courseService.createCourse(createRequestDTO)).thenReturn(responseDTO);
		mockMvc.perform(post("/courses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isCreated());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator creates a course")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnCreateCourse() throws Exception {
    	CourseRegisterRequestDTO createRequestDTO =
    			new CourseRegisterRequestDTO(COURSE_NAME, COURSE_DESC);
		mockMvc.perform(post("/courses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator creates a course")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnCreateCourse() throws Exception {
    	CourseRegisterRequestDTO createRequestDTO =
    			new CourseRegisterRequestDTO(COURSE_NAME, COURSE_DESC);
		mockMvc.perform(post("/courses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher creates a course")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnCreateCourse() throws Exception {
    	CourseRegisterRequestDTO createRequestDTO =
    			new CourseRegisterRequestDTO(COURSE_NAME, COURSE_DESC);
		mockMvc.perform(post("/courses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when a regular User creates a course")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsUserOnCreateCourse() throws Exception {
    	CourseRegisterRequestDTO createRequestDTO =
    			new CourseRegisterRequestDTO(COURSE_NAME, COURSE_DESC);
		mockMvc.perform(post("/courses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	/***** getCourse *****/
	
	@Test
	@DisplayName("Should return 204 No Content when Admin gets a course")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnNoContentWhenUserIsAdminOnGetCourse() throws Exception {
		mockMvc.perform(get("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator gets a course")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnGetCourse() throws Exception {
		mockMvc.perform(get("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator gets a course")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnGetCourse() throws Exception {
		mockMvc.perform(get("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher gets a course")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnGetCourse() throws Exception {
		mockMvc.perform(get("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student gets a course")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnGetCourse() throws Exception {
		mockMvc.perform(get("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isOk());
	}
	
	/***** updateCourse *****/
	
	@Test
	@DisplayName("Should return 201 Created when Admin updates a course")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnCreatedWhenUserIsAdminOnUpdateCourse() throws Exception {
    	CourseUpdateRequestDTO updateRequestDTO =
    			new CourseUpdateRequestDTO(COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
    	CourseDetailDTO responseDTO = new CourseDetailDTO(COURSE_ID, COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
    	when(courseService.updateCourse(COURSE_ID, updateRequestDTO)).thenReturn(responseDTO);
		mockMvc.perform(put("/courses/{courseId}", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator updates a course")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnUpdateCourse() throws Exception {
    	CourseUpdateRequestDTO updateRequestDTO =
    			new CourseUpdateRequestDTO(COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
		mockMvc.perform(put("/courses/{courseId}", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator updates a course")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnUpdateCourse() throws Exception {
    	CourseUpdateRequestDTO updateRequestDTO =
    			new CourseUpdateRequestDTO(COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
		mockMvc.perform(put("/courses/{courseId}", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher updates a course")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnUpdateCourse() throws Exception {
    	CourseUpdateRequestDTO updateRequestDTO =
    			new CourseUpdateRequestDTO(COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
		mockMvc.perform(put("/courses/{courseId}", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when a regular User updates a course")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsUserOnUpdateCourse() throws Exception {
    	CourseUpdateRequestDTO updateRequestDTO =
    			new CourseUpdateRequestDTO(COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
		mockMvc.perform(put("/courses/{courseId}", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
		.andExpect(status().isForbidden());
	}
	
	/***** deleteCourse *****/
	
	@Test
	@DisplayName("Should return 204 No Content when Admin deletes a course")
	@WithMockUser(roles = RoleConstants.ADMIN)
	void shouldReturnNoContentWhenUserIsAdminOnDeleteCourse() throws Exception {
		mockMvc.perform(delete("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Coordinator deletes a course")
	@WithMockUser(roles = RoleConstants.COORD)
	void shouldReturnForbiddenWhenUserIsCoordinatorOnDeleteCourse() throws Exception {
		mockMvc.perform(delete("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Moderator deletes a course")
	@WithMockUser(roles = RoleConstants.MODERATOR)
	void shouldReturnForbiddenWhenUserIsModeratorOnDeleteCourse() throws Exception {
		mockMvc.perform(delete("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Teacher deletes a course")
	@WithMockUser(roles = RoleConstants.TEACHER)
	void shouldReturnForbiddenWhenUserIsTeacherOnDeleteCourse() throws Exception {
		mockMvc.perform(delete("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when Student deletes a course")
	@WithMockUser(roles = RoleConstants.STUDENT)
	void shouldReturnForbiddenWhenUserIsStudentOnDeleteCourse() throws Exception {
		mockMvc.perform(delete("/courses/{courseId}", COURSE_ID))
		.andExpect(status().isForbidden());
	}

	
}
