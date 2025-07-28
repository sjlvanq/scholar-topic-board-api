package uno.lode.ScholarTopicBoard.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.exception.ErrorStatusResponseCodes;
import uno.lode.ScholarTopicBoard.infra.exception.GlobalExceptionHandler;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseLockedException;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.service.CourseService;

class CourseControllerUnitTest {

	private Long COURSE_ID = 1L;
	private String COURSE_NAME = "course-name";
	private String COURSE_DESC = "course-description";
	private Boolean COURSE_CLOSED = false;

	//@Mock
	private CourseService courseService;

	//@InjectMocks
	private CourseController courseController;

	//@Autowired
	private MockMvc mockMvc;
	
	private final ObjectMapper mapper = new ObjectMapper();

	private CourseDetailDTO createCourseDetailDTO() {
		return new CourseDetailDTO(COURSE_ID, COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
	}

	
	@BeforeEach
	void setUp() {
		courseService = mock(CourseService.class);
		courseController = new CourseController(courseService);
		mockMvc = MockMvcBuilders.standaloneSetup(courseController).setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}
	

	@Test
	@DisplayName("Should return 201 and created course data for valid POST")
	void shouldCreateCourse() throws Exception {
		CourseRegisterRequestDTO createRequestDTO = new CourseRegisterRequestDTO(COURSE_NAME, COURSE_DESC);
		CourseDetailDTO responseDTO = createCourseDetailDTO();

		when(courseService.createCourse(createRequestDTO)).thenReturn(responseDTO);

		mockMvc.perform(post("/courses").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(COURSE_ID)).andExpect(jsonPath("$.name").value(COURSE_NAME))
				.andExpect(jsonPath("$.description").value(COURSE_DESC))
				.andExpect(jsonPath("$.closed").value(COURSE_CLOSED));

		verify(courseService).createCourse(eq(createRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 201 Created when description is null on POST")
	void shouldCreateCourseWhenDescriptionIsNullInPost() throws Exception {
	    CourseRegisterRequestDTO createRequestDTO = new CourseRegisterRequestDTO(COURSE_NAME, null);
	    CourseDetailDTO responseDTO = createCourseDetailDTO();

	    when(courseService.createCourse(createRequestDTO)).thenReturn(responseDTO);

	    mockMvc.perform(post("/courses").contentType(MediaType.APPLICATION_JSON)
	            .content(mapper.writeValueAsBytes(createRequestDTO)))
	        .andExpect(status().isCreated())
	        .andExpect(jsonPath("$.id").value(COURSE_ID));

	    verify(courseService).createCourse(eq(createRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 204 No Content for valid DELETE request")
	void shouldDeleteCourse() throws Exception {
		mockMvc.perform(delete("/courses/{courseId}", COURSE_ID)).andExpect(status().isNoContent());

		verify(courseService).deleteCourse(eq(COURSE_ID));
	}


	@Test
	@DisplayName("Should return 200 OK and list all courses on GET")
	void shouldGetAllCourses() throws Exception {
	    List<CourseDetailDTO> responseList = List.of(
	        new CourseDetailDTO(COURSE_ID, COURSE_NAME+" 1", COURSE_DESC+" 1", COURSE_CLOSED),
	        new CourseDetailDTO(COURSE_ID+1, COURSE_NAME+" 2", COURSE_DESC+" 2", COURSE_CLOSED)
	    );
	    when(courseService.getAllCourses()).thenReturn(responseList);

	    mockMvc.perform(get("/courses")
	    		.accept(MediaType.APPLICATION_JSON))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.length()").value(responseList.size()))
	        .andExpect(jsonPath("$[0].id").value(COURSE_ID))
	        .andExpect(jsonPath("$[0].name").value(COURSE_NAME+" 1"))
	        .andExpect(jsonPath("$[1].id").value(COURSE_ID+1))
	        .andExpect(jsonPath("$[1].name").value(COURSE_NAME+" 2"));

	    verify(courseService).getAllCourses();
	}
	
	@Test
	@DisplayName("Should return 200 OK and course data for valid GET")
	void shouldGetCourse() throws Exception {
		CourseDetailDTO responseDTO = createCourseDetailDTO();
		when(courseService.getCourseById(COURSE_ID)).thenReturn(responseDTO);

		mockMvc.perform(get("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(COURSE_ID))
				.andExpect(jsonPath("$.name").value(COURSE_NAME))
				.andExpect(jsonPath("$.description").value(COURSE_DESC))
				.andExpect(jsonPath("$.closed").value(COURSE_CLOSED));

		verify(courseService).getCourseById(eq(COURSE_ID));
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when description is too long on POST")
	void shouldReturnBadRequestWhenDescriptionIsTooLongInPost() throws Exception {
		CourseRegisterRequestDTO createRequestDTO = new CourseRegisterRequestDTO(COURSE_NAME, Strings.repeat("*", 501));

		mockMvc.perform(post("/courses", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).createCourse(Mockito.any());
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when description is too long on PUT")
	void shouldReturnBadRequestWhenDescriptionIsTooLongInPut() throws Exception {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO(COURSE_NAME, Strings.repeat("*", 501),
				COURSE_CLOSED);

		mockMvc.perform(put("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).updateCourse(Mockito.any(), Mockito.any());
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when name is blank on POST")
	void shouldReturnBadRequestWhenNameIsBlankInPost() throws Exception {
		CourseRegisterRequestDTO createRequestDTO = new CourseRegisterRequestDTO("", COURSE_DESC);

		mockMvc.perform(post("/courses", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).createCourse(Mockito.any());
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when name is blank on PUT")
	void shouldReturnBadRequestWhenNameIsBlankInPut() throws Exception {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO("    ", COURSE_DESC, COURSE_CLOSED);

		mockMvc.perform(put("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).updateCourse(Mockito.any(), Mockito.any());
	}

	@Test
	@DisplayName("Should return 400 Bad Request when name is null on POST")
	void shouldReturnBadRequestWhenNameIsNullInPost() throws Exception {
		CourseRegisterRequestDTO createRequestDTO = new CourseRegisterRequestDTO(null, COURSE_DESC);

		mockMvc.perform(post("/courses", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).createCourse(Mockito.any());
	}

	@Test
	@DisplayName("Should return 400 Bad Request when name is null on PUT")
	void shouldReturnBadRequestWhenNameIsNullInPut() throws Exception {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO(null, COURSE_DESC, COURSE_CLOSED);

		mockMvc.perform(put("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).updateCourse(Mockito.any(), Mockito.any());
	}

	@Test
	@DisplayName("Should return 400 Bad Request when name is too long on POST")
	void shouldReturnBadRequestWhenNameIsTooLongInPost() throws Exception {
		CourseRegisterRequestDTO createRequestDTO = new CourseRegisterRequestDTO(Strings.repeat("*", 101), COURSE_DESC);

		mockMvc.perform(post("/courses", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).createCourse(Mockito.any());
	}

	@Test
	@DisplayName("Should return 400 Bad Request when name is too long on PUT")
	void shouldReturnBadRequestWhenNameIsTooLongInPut() throws Exception {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO(Strings.repeat("*", 101), COURSE_DESC,
				COURSE_CLOSED);

		mockMvc.perform(put("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).updateCourse(Mockito.any(), Mockito.any());
	}

	@Test
	@DisplayName("Should return 400 Bad Request when name is too short on POST")
	void shouldReturnBadRequestWhenNameIsTooShortInPost() throws Exception {
		CourseRegisterRequestDTO createRequestDTO = new CourseRegisterRequestDTO(Strings.left(COURSE_NAME, 4), COURSE_DESC);

		mockMvc.perform(post("/courses", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).createCourse(Mockito.any());
	}

	@Test
	@DisplayName("Should return 400 Bad Request when name is too short on PUT")
	void shouldReturnBadRequestWhenNameIsTooShortInPut() throws Exception {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO(Strings.left(COURSE_NAME, 4), COURSE_DESC,
				COURSE_CLOSED);

		mockMvc.perform(put("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isBadRequest());

		verify(courseService, Mockito.never()).updateCourse(Mockito.any(), Mockito.any());
	}

	@Test
	@DisplayName("Should return 409 Conflict when course already exists on POST")
	void shouldReturnConflictWhenCourseAlreadyExistsOnPost() throws Exception {
		CourseRegisterRequestDTO createRequestDTO = new CourseRegisterRequestDTO(COURSE_NAME, COURSE_DESC);

		when(courseService.createCourse(createRequestDTO)).thenThrow(new CourseAlreadyExistsException(COURSE_NAME));

		mockMvc.perform(post("/courses").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.CONFLICT_409.toString()));

		verify(courseService).createCourse(eq(createRequestDTO));
	}

	@Test
	@DisplayName("Should return 409 Conflict when course already exists on PUT")
	void shouldReturnConflictWhenCourseAlreadyExistsOnPut() throws Exception {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO(COURSE_NAME, COURSE_DESC, COURSE_CLOSED);

		when(courseService.updateCourse(COURSE_ID, updateRequestDTO)).thenThrow(new CourseAlreadyExistsException(COURSE_NAME));

		mockMvc.perform(put("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.CONFLICT_409.toString()));

		verify(courseService).updateCourse(eq(COURSE_ID), eq(updateRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 423 Locked when course cannot be deleted due to enrolled users")
	void shouldReturnLockedWhenDeleteIsBlocked() throws Exception {
	    Long lockedId = 2L;
	    Mockito.doThrow(new CourseLockedException(HttpMethod.DELETE)).when(courseService).deleteCourse(lockedId);

	    mockMvc.perform(delete("/courses/{courseId}", lockedId).contentType(MediaType.APPLICATION_JSON))
	        .andExpect(status().isLocked())
	        .andExpect(jsonPath("$.code").value("LOCKED_423"));

	    verify(courseService).deleteCourse(eq(lockedId));
	}

	@Test
	@DisplayName("Should return 204 No Content when no courses exist")
	void shouldReturnNoContentWhenNoCoursesExist() throws Exception {
	    when(courseService.getAllCourses()).thenReturn(List.of());

	    mockMvc.perform(get("/courses").accept(MediaType.APPLICATION_JSON))
	        .andExpect(status().isNoContent());

	    verify(courseService).getAllCourses();
	}

	@Test
	@DisplayName("Should return 404 Not Found when course id is invalid in DELETE request")
	void shouldReturnNotFoundWhenCourseIdIsInvalidOnDelete() throws Exception {
		Long invalidId = 999L;
		Mockito.doThrow(new CourseNotFoundException(invalidId)).when(courseService).deleteCourse(invalidId);

		mockMvc.perform(delete("/courses/{courseId}", invalidId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

		verify(courseService).deleteCourse(eq(invalidId));
	}

	@Test
	@DisplayName("Should return 404 Not Found when course id is invalid on GET")
	void shouldReturnNotFoundWhenCourseIdIsInvalidOnGet() throws Exception {
		Long invalidId = 999L;
		when(courseService.getCourseById(invalidId)).thenThrow(new CourseNotFoundException(invalidId));

		mockMvc.perform(get("/courses/{courseId}", invalidId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

		verify(courseService).getCourseById(eq(invalidId));
	}

	@Test
	@DisplayName("Should return 404 Not Found when course id is invalid on PUT")
	void shouldReturnNotFoundWhenCourseIdIsInvalidOnUpdate() throws Exception {
		Long invalidId = 999L;
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO(COURSE_NAME, COURSE_DESC, COURSE_CLOSED);

		Mockito.doThrow(new CourseNotFoundException(invalidId)).when(courseService).updateCourse(invalidId,
				updateRequestDTO);
		
	    mockMvc.perform(put("/courses/{courseId}", invalidId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(mapper.writeValueAsBytes(updateRequestDTO)))
	        .andExpect(status().isNotFound())
	        .andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));
	}
	
	@Test
	@DisplayName("Should return 200 and updated course data for valid PUT")
	void shouldUpdateCourse() throws Exception {
		CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO(COURSE_NAME, COURSE_DESC, COURSE_CLOSED);
		CourseDetailDTO responseDTO = createCourseDetailDTO();

		when(courseService.updateCourse(COURSE_ID, updateRequestDTO)).thenReturn(responseDTO);

		mockMvc.perform(put("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(COURSE_ID)).andExpect(jsonPath("$.name").value(COURSE_NAME))
				.andExpect(jsonPath("$.description").value(COURSE_DESC))
				.andExpect(jsonPath("$.closed").value(COURSE_CLOSED));

		verify(courseService).updateCourse(eq(COURSE_ID), eq(updateRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 200 Ok when description is null on PUT")
	void shouldUpdateCourseWhenDescriptionIsNullInPut() throws Exception {
	    CourseUpdateRequestDTO updateRequestDTO = new CourseUpdateRequestDTO(COURSE_NAME, null, true);
	    CourseDetailDTO responseDTO = createCourseDetailDTO();

	    when(courseService.updateCourse(COURSE_ID, updateRequestDTO)).thenReturn(responseDTO);

	    mockMvc.perform(put("/courses/{courseId}", COURSE_ID).contentType(MediaType.APPLICATION_JSON)
	            .content(mapper.writeValueAsBytes(updateRequestDTO)))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.id").value(COURSE_ID));

	    verify(courseService).updateCourse(eq(COURSE_ID), eq(updateRequestDTO));
	}

}