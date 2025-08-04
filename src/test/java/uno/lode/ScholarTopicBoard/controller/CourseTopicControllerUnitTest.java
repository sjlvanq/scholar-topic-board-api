package uno.lode.ScholarTopicBoard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithAuthorDTO;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserAuthorDTO;
import uno.lode.ScholarTopicBoard.infra.exception.ErrorStatusResponseCodes;
import uno.lode.ScholarTopicBoard.infra.exception.GlobalExceptionHandler;
import uno.lode.ScholarTopicBoard.infra.exception.course.CourseNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.topic.TopicNotFoundException;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.service.TopicService;

class CourseTopicControllerUnitTest {

	private static final Long COURSE_ID = 1L;
    private static final Long TOPIC_ID = 10L;
    private static final String USER_FIRST_NAME = "user";
    private static final String USER_LAST_NAME = "user";
    private static final String USER_EMAIL = "user@123.com";
    private static final String USER_PASSWORD = "123#Abc";
    private static final Boolean USER_BANNED = false;
    private static final Boolean USER_DELETED = false;
    private static final String TOPIC_TITLE = "topic-title";
    private static final String TOPIC_BODY = "topic-body";
    private static final Boolean TOPIC_CLOSED = false;
    private static final String COURSE_NAME = "course-name";
    private static final String COURSE_DESCRIPTION = "course-description";
	private static final LocalDateTime TOPIC_CREATION_DATE = LocalDateTime.of(2025, 12, 20, 22, 00);
	private static final LocalDateTime TOPIC_UPDATE_DATE = LocalDateTime.of(2025, 12, 20, 22, 50);

	@Mock
	private TopicService topicService;

	@InjectMocks
	private CourseTopicController topicController;

	private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
	private AuthUser mockAuthUser;

	@BeforeEach
	void setUp() {
		topicService = mock(TopicService.class);
		topicController = new CourseTopicController(topicService);
		mockAuthUser = new AuthUser( new User(1L, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_PASSWORD, USER_BANNED,
				USER_DELETED, List.of(), List.of()));

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
	    			.standaloneSetup(topicController)
	    			.setControllerAdvice(new GlobalExceptionHandler())
	    			.setCustomArgumentResolvers(mockResolver)
	    			.build();
	}

    @Test
    @DisplayName("Should return 201 and created topic data for valid POST request")
    void shouldRegisterTopic() throws Exception {
		TopicRegisterRequestDTO requestDTO = 
				new TopicRegisterRequestDTO(TOPIC_TITLE, TOPIC_BODY);
		TopicDetailDTO responseDTO = createTopicDetailDTO();

		when(topicService.createTopic(
				eq(mockAuthUser), 
				eq(COURSE_ID), 
				eq(requestDTO)))
			.thenReturn(responseDTO);

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType("application/json")
				.content(mapper.writeValueAsString(requestDTO)))
		.andExpect(status().isCreated())
		.andExpect(header().exists("Location"))
		.andExpect(jsonPath("$.id").value(TOPIC_ID))
		.andExpect(jsonPath("$.title").value(TOPIC_TITLE))
		.andExpect(jsonPath("$.body").value(TOPIC_BODY))
		.andExpect(jsonPath("$.author.firstName").value(USER_FIRST_NAME))
		.andExpect(jsonPath("$.author.lastName").value(USER_LAST_NAME))
		.andExpect(jsonPath("$.author.email").value(USER_EMAIL))
		.andExpect(jsonPath("$.course.id").value(COURSE_ID))
		.andExpect(jsonPath("$.course.name").value(COURSE_NAME))
		.andExpect(jsonPath("$.course.description").value(COURSE_DESCRIPTION))
		.andExpect(jsonPath("$.closed").value(TOPIC_CLOSED));

		verify(topicService).createTopic(eq(mockAuthUser), eq(COURSE_ID), eq(requestDTO));
    }

    @Test
    @DisplayName("Should return 200 and updated topic data for valid PUT request")
    void shouldUpdateTopic() throws Exception {
    	TopicUpdateRequestDTO updateRequestDTO =
    			new TopicUpdateRequestDTO("new-topic-title", "new-topic-body", false);
    	TopicDetailDTO responseDTO = createTopicDetailDTO();

		when(topicService.updateTopic(
				eq(mockAuthUser),
				eq(COURSE_ID),
				eq(TOPIC_ID),
				eq(updateRequestDTO)))
			.thenReturn(responseDTO);

        mockMvc.perform(put("/courses/{courseId}/topics/{topicId}", COURSE_ID, TOPIC_ID)
        		.contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequestDTO))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(TOPIC_ID))
		.andExpect(jsonPath("$.title").value(TOPIC_TITLE))
		.andExpect(jsonPath("$.body").value(TOPIC_BODY))
		.andExpect(jsonPath("$.author.firstName").value(USER_FIRST_NAME))
		.andExpect(jsonPath("$.author.lastName").value(USER_LAST_NAME))
		.andExpect(jsonPath("$.author.email").value(USER_EMAIL))
		.andExpect(jsonPath("$.course.id").value(COURSE_ID))
		.andExpect(jsonPath("$.course.name").value(COURSE_NAME))
		.andExpect(jsonPath("$.course.description").value(COURSE_DESCRIPTION))
		.andExpect(jsonPath("$.closed").value(TOPIC_CLOSED));

        verify(topicService).updateTopic(eq(mockAuthUser), eq(COURSE_ID), eq(TOPIC_ID), eq(updateRequestDTO));
    }

    @Test
    @DisplayName("Should return 200 and the requested topic")
    void shouldGetTopic() throws Exception {
    	TopicWithAuthorDTO responseDTO = 
    			new TopicWithAuthorDTO(
    					TOPIC_ID, 
    					createUserAuthorDTO(), 
    					TOPIC_TITLE,
    					TOPIC_CREATION_DATE.toString(), 
    					TOPIC_UPDATE_DATE.toString(), 
    					TOPIC_BODY, 
    					TOPIC_CLOSED);
    	
    	when(topicService.getTopic(
    			eq(mockAuthUser), 
    			eq(COURSE_ID), 
    			eq(TOPIC_ID)))
    		.thenReturn(responseDTO);

        mockMvc.perform(get("/courses/{courseId}/topics/{topicId}", COURSE_ID, TOPIC_ID)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(TOPIC_ID))
		.andExpect(jsonPath("$.author.firstName").value(USER_FIRST_NAME))
		.andExpect(jsonPath("$.author.lastName").value(USER_LAST_NAME))
		.andExpect(jsonPath("$.author.email").value(USER_EMAIL))
		.andExpect(jsonPath("$.title").value(TOPIC_TITLE))
		.andExpect(jsonPath("$.creationDate").value(TOPIC_CREATION_DATE.toString()))
		.andExpect(jsonPath("$.updateDate").value(TOPIC_UPDATE_DATE.toString()))
		.andExpect(jsonPath("$.body").value(TOPIC_BODY))
        .andExpect(jsonPath("$.closed").value(false));

        verify(topicService).getTopic(eq(mockAuthUser), eq(COURSE_ID), eq(TOPIC_ID));
    }

	@Test
	@DisplayName("Should return 200 OK and list all topics in course when course id is valid")
	void shouldGetAllTopicsInCourse() throws Exception {
	    List<TopicWithAuthorDTO> responseList = List.of(
	        new TopicWithAuthorDTO(TOPIC_ID, createUserAuthorDTO(), TOPIC_TITLE,
	        		TOPIC_CREATION_DATE.toString(), TOPIC_UPDATE_DATE.toString(), TOPIC_BODY, false),
	        new TopicWithAuthorDTO(TOPIC_ID+1, createUserAuthorDTO(), TOPIC_TITLE+" 2",
	        		TOPIC_CREATION_DATE.toString(), TOPIC_UPDATE_DATE.toString(), TOPIC_BODY, false)
	    );
	    
	    when(topicService.listByCourse(
	    		eq(mockAuthUser), 
	    		eq(COURSE_ID))).thenReturn(responseList);

	    mockMvc.perform(get("/courses/{courseId}/topics", COURSE_ID)
	    	.accept(MediaType.APPLICATION_JSON))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.length()").value(responseList.size()))
        .andExpect(jsonPath("$[0].id").value(TOPIC_ID))
		.andExpect(jsonPath("$[0].author.firstName").value(USER_FIRST_NAME))
		.andExpect(jsonPath("$[0].author.lastName").value(USER_LAST_NAME))
		.andExpect(jsonPath("$[0].author.email").value(USER_EMAIL))
		.andExpect(jsonPath("$[0].title").value(TOPIC_TITLE))
		.andExpect(jsonPath("$[0].creationDate").value(TOPIC_CREATION_DATE.toString()))
		.andExpect(jsonPath("$[0].updateDate").value(TOPIC_UPDATE_DATE.toString()))
		.andExpect(jsonPath("$[0].body").value(TOPIC_BODY))
        .andExpect(jsonPath("$[0].closed").value(false))
        .andExpect(jsonPath("$[1].id").value(TOPIC_ID+1))
		.andExpect(jsonPath("$[1].author.firstName").value(USER_FIRST_NAME))
		.andExpect(jsonPath("$[1].author.lastName").value(USER_LAST_NAME))
		.andExpect(jsonPath("$[1].author.email").value(USER_EMAIL))
		.andExpect(jsonPath("$[1].title").value(TOPIC_TITLE+" 2"))
		.andExpect(jsonPath("$[1].creationDate").value(TOPIC_CREATION_DATE.toString()))
		.andExpect(jsonPath("$[1].updateDate").value(TOPIC_UPDATE_DATE.toString()))
		.andExpect(jsonPath("$[1].body").value(TOPIC_BODY))
        .andExpect(jsonPath("$[1].closed").value(false));

	    verify(topicService).listByCourse(eq(mockAuthUser), eq(COURSE_ID));
	}
	
	@Test
	@DisplayName("Should return 404 NOT FOUND when listing all topics for invalid course id")
	void shouldReturnNotFoundWhenGettingAllTopicsWithInvalidCourseId() throws Exception {
		Long invalidId = 999L;
	    when(topicService.listByCourse(any(AuthUser.class), eq(invalidId))).thenThrow(new CourseNotFoundException(invalidId));

	    mockMvc.perform(get("/courses/{courseId}/topics", invalidId)
	    	.accept(MediaType.APPLICATION_JSON))
	    .andExpect(status().isNotFound())
	    .andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

	    verify(topicService).listByCourse(any(AuthUser.class), eq(invalidId));
	}
    
    @Test
    @DisplayName("Should return 404 Not FOUND when topic id is invalid in GET request")
    void shouldReturnTopicNotFound() throws Exception {
    	Long invalidId = 999L;
    	when(topicService.getTopic(any(AuthUser.class), eq(COURSE_ID), eq(invalidId))).thenThrow(new TopicNotFoundException(invalidId));

        mockMvc.perform(get("/courses/{courseId}/topics/{topicId}", COURSE_ID, invalidId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

        verify(topicService).getTopic(eq(mockAuthUser), eq(COURSE_ID), eq(invalidId));
    }
    
    @DisplayName("Should return 404 Not FOUND when course id is invalid in GET request")
    void shouldReturnNotFoundWhenCourseIdIsInvalidOnGet() throws Exception {
    	Long invalidCourseId = 999L;
    	when(topicService.getTopic(any(AuthUser.class), eq(invalidCourseId), eq(TOPIC_ID)))
    		.thenThrow(new CourseNotFoundException(invalidCourseId));

        mockMvc.perform(get("/courses/{courseId}/topics/{topicId}", invalidCourseId, TOPIC_ID)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

		verify(topicService).getTopic(eq(mockAuthUser), eq(invalidCourseId), eq(TOPIC_ID));
	}

    @Test
    @DisplayName("Should return 404 Not FOUND when course id is invalid on POST")
    void shouldReturnNotFoundWhenCourseIdIsInvalidOnPost() throws Exception {
    	Long invalidCourseId = 999L;
		TopicRegisterRequestDTO requestDTO = 
				new TopicRegisterRequestDTO(TOPIC_TITLE, TOPIC_BODY);
    	when(topicService.createTopic(any(AuthUser.class), eq(invalidCourseId), eq(requestDTO)))
    		.thenThrow(new CourseNotFoundException(invalidCourseId));

		mockMvc.perform(post("/courses/{courseId}/topics", invalidCourseId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(requestDTO)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

		verify(topicService).createTopic(any(AuthUser.class), eq(invalidCourseId), eq(requestDTO));
	}
    
    @Test
    @DisplayName("Should return 404 Not FOUND when course id is invalid in PUT request")
    void shouldReturnNotFoundWhenCourseIdIsInvalidOnPut() throws Exception {
    	Long invalidCourseId = 999L;
    	TopicUpdateRequestDTO requestDTO = 
    			new TopicUpdateRequestDTO(TOPIC_TITLE, TOPIC_BODY, true);
    	when(topicService.updateTopic(
    			any(AuthUser.class), eq(invalidCourseId), eq(TOPIC_ID), eq(requestDTO)))
    		.thenThrow(new TopicNotFoundException(invalidCourseId));

		mockMvc.perform(put("/courses/{courseId}/topics/{topicId}", invalidCourseId, TOPIC_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(requestDTO)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

		verify(topicService).updateTopic(eq(mockAuthUser), eq(invalidCourseId), eq(TOPIC_ID), eq(requestDTO));
	}
    
    @Test
    @DisplayName("Should return 404 Not FOUND when topic id is invalid in PUT request")
    void shouldReturnNotFoundWhenTopicIdIsInvalidOnPut() throws Exception {
    	Long invalidTopicId = 999L;
    	TopicUpdateRequestDTO requestDTO = 
    			new TopicUpdateRequestDTO(TOPIC_TITLE, TOPIC_BODY, true);
    	when(topicService.updateTopic(
    			any(AuthUser.class), eq(COURSE_ID), eq(invalidTopicId), eq(requestDTO)))
    		.thenThrow(new TopicNotFoundException(invalidTopicId));

		mockMvc.perform(put("/courses/{courseId}/topics/{topicId}", COURSE_ID, invalidTopicId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(requestDTO)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

		verify(topicService).updateTopic(eq(mockAuthUser), eq(COURSE_ID), eq(invalidTopicId), eq(requestDTO));
	}
    
	@Test
	@DisplayName("Should return 400 Bad Request when body is too short on POST.")
	void shouldReturnBadRequestWhenBodyIsTooShortInPost() throws Exception {
		TopicRegisterRequestDTO createRequestDTO = new TopicRegisterRequestDTO(TOPIC_TITLE, Strings.left(TOPIC_BODY,4));

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO));
	}

	@Test
	@DisplayName("Should return 400 Bad Request when title is too short on POST.")
	void shouldReturnBadRequestWhenTitleIsTooShortInPost() throws Exception {
		TopicRegisterRequestDTO createRequestDTO = new TopicRegisterRequestDTO(Strings.left(TOPIC_TITLE, 4), TOPIC_BODY);

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when title is too long on POST.")
	void shouldReturnBadRequestWhenTitleIsTooLongInPost() throws Exception {
		TopicRegisterRequestDTO createRequestDTO = new TopicRegisterRequestDTO(Strings.repeat("*", 51), TOPIC_BODY);

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when body is blank on POST.")
	void shouldReturnBadRequestWhenBodyIsBlankInPost() throws Exception {
		TopicRegisterRequestDTO createRequestDTO = new TopicRegisterRequestDTO(TOPIC_TITLE, "    ");

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO));
	}

	@Test
	@DisplayName("Should return 400 Bad Request when body is null on POST.")
	void shouldReturnBadRequestWhenBodyIsNullInPost() throws Exception {
		TopicRegisterRequestDTO createRequestDTO = new TopicRegisterRequestDTO(TOPIC_TITLE, null);

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when title is blank on POST.")
	void shouldReturnBadRequestWhenTitleIsBlankInPost() throws Exception {
		TopicRegisterRequestDTO createRequestDTO = new TopicRegisterRequestDTO("    ", TOPIC_BODY);

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO));
	}

	@Test
	@DisplayName("Should return 400 Bad Request when title is null on POST.")
	void shouldReturnBadRequestWhenTitleIsNullInPost() throws Exception {
		TopicRegisterRequestDTO createRequestDTO = new TopicRegisterRequestDTO(null, TOPIC_BODY);

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO));
	}

	@Test
	@DisplayName("Should return 400 Bad Request when body is blank on PUT.")
	void shouldReturnBadRequestWhenBodyIsBlankInPut() throws Exception {
		TopicUpdateRequestDTO updateRequestDTO = new TopicUpdateRequestDTO(TOPIC_TITLE, "    ", true);

		mockMvc.perform(put("/courses/{courseId}/topics/{topicId}", COURSE_ID, TOPIC_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).updateTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID), eq(updateRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when title is blank on PUT.")
	void shouldReturnBadRequestWhenTitleIsBlankInPut() throws Exception {
		TopicUpdateRequestDTO updateRequestDTO = new TopicUpdateRequestDTO("    ", TOPIC_BODY, true);

		mockMvc.perform(put("/courses/{courseId}/topics/{topicId}", COURSE_ID, TOPIC_ID).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO))).andExpect(status().isBadRequest());

		verify(topicService, Mockito.never()).updateTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID), eq(updateRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when user is not author or admin on POST")
	void shouldReturnForbiddenWhenUserIsNotAuthorOrAdminOnPost() throws Exception {
		TopicRegisterRequestDTO requestDTO = new TopicRegisterRequestDTO(TOPIC_TITLE, TOPIC_BODY);
	    doThrow(new AccessDeniedException("Forbidden"))
	        .when(topicService).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(requestDTO));

	    mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
	    	.contentType("application/json")
	    	.content(mapper.writeValueAsString(requestDTO)))
	        .andExpect(status().isForbidden());

	    verify(topicService).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(requestDTO));
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when user is not author or admin in PUT request")
	void shouldReturnForbiddenWhenUserIsNotAuthorOrAdminOnPut() throws Exception {
		TopicUpdateRequestDTO updateRequestDTO = new TopicUpdateRequestDTO(TOPIC_TITLE, TOPIC_BODY, true);
	    doThrow(new AccessDeniedException("Forbidden"))
	        .when(topicService).updateTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID), any(TopicUpdateRequestDTO.class));

	    mockMvc.perform(put("/courses/{courseId}/topics/{topicId}", COURSE_ID, TOPIC_ID)
	    	.contentType("application/json")
	    	.content(mapper.writeValueAsString(updateRequestDTO)))
	    	.andExpect(status().isForbidden());

	    verify(topicService).updateTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID), any(TopicUpdateRequestDTO.class));
	}
	
	@Test
	@DisplayName("Should return 403 Forbidden when user is not author or admin on DELETE")
	void shouldReturnForbiddenWhenUserIsNotAuthorOrAdminOnDelete() throws Exception {
	    doThrow(new AccessDeniedException("Forbidden"))
	        .when(topicService).deleteTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID));

	    mockMvc.perform(delete("/courses/{courseId}/topics/{topicId}", COURSE_ID, TOPIC_ID))
	        .andExpect(status().isForbidden());

	    verify(topicService).deleteTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID));
	}
	
	@Test
	@DisplayName("Should return 204 No Content for valid DELETE request")
	void shouldDeleteTopic() throws Exception {
		mockMvc.perform(delete("/courses/{courseId}/topics/{topicId}", COURSE_ID, TOPIC_ID)).andExpect(status().isNoContent());

		verify(topicService).deleteTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when course id is invalid on DELETE")
	void shouldReturnNotFoundWhenCourseIdIsInvalidOnDelete() throws Exception {
		Long invalidId = 999L;
		doThrow(new CourseNotFoundException(invalidId)).when(topicService).deleteTopic(any(AuthUser.class), eq(invalidId), eq(TOPIC_ID));

		mockMvc.perform(delete("/courses/{courseId}/topics/{topicId}", invalidId, TOPIC_ID))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

		verify(topicService).deleteTopic(any(AuthUser.class),eq(invalidId), eq(TOPIC_ID));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when topic id is invalid on DELETE")
	void shouldReturnNotFoundWhenTopicIdIsInvalidOnDelete() throws Exception {
		Long invalidId = 999L;
		doThrow(new TopicNotFoundException(invalidId)).when(topicService).deleteTopic(any(AuthUser.class), eq(COURSE_ID), eq(invalidId));

		mockMvc.perform(delete("/courses/{courseId}/topics/{topicId}", COURSE_ID, invalidId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.NOT_FOUND_404.toString()));

		verify(topicService).deleteTopic(any(AuthUser.class),eq(COURSE_ID), eq(invalidId));
	}
	
	@Test
	@DisplayName("Should return 409 Conflict when topic title already exists on POST request")
	void shouldReturnConflictWhenTopicTitleAlreadyExistsOnPost() throws Exception {
		TopicRegisterRequestDTO createRequestDTO = new TopicRegisterRequestDTO(TOPIC_TITLE, TOPIC_BODY);

		when(topicService.createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO)))
			.thenThrow(new TopicAlreadyExistsException(COURSE_NAME));

		mockMvc.perform(post("/courses/{courseId}/topics", COURSE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createRequestDTO)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.CONFLICT_409.toString()));

		verify(topicService).createTopic(any(AuthUser.class), eq(COURSE_ID), eq(createRequestDTO));
	}
	
	@Test
	@DisplayName("Should return 409 Conflict when topic title already exists on PUT request")
	void shouldReturnConflictWhenTopicTitleAlreadyExistsOnPut() throws Exception {
		TopicUpdateRequestDTO updateRequestDTO = new TopicUpdateRequestDTO(TOPIC_TITLE, TOPIC_BODY, TOPIC_CLOSED);

		when(topicService.updateTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID), eq(updateRequestDTO)))
			.thenThrow(new TopicAlreadyExistsException(COURSE_NAME));

		mockMvc.perform(put("/courses/{courseId}/topics/{topicId}", COURSE_ID, TOPIC_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(updateRequestDTO)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(ErrorStatusResponseCodes.CONFLICT_409.toString()));

		verify(topicService).updateTopic(any(AuthUser.class), eq(COURSE_ID), eq(TOPIC_ID), eq(updateRequestDTO));
	}	

    private UserAuthorDTO createUserAuthorDTO() {
        return new UserAuthorDTO(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL);
    }

    private CourseDetailDTO createCourseDetailDTO() {
        return new CourseDetailDTO(COURSE_ID, COURSE_NAME, COURSE_DESCRIPTION, true);
    }

    private TopicDetailDTO createTopicDetailDTO() {
        return new TopicDetailDTO(
            TOPIC_ID,
            createUserAuthorDTO(),
            createCourseDetailDTO(),
            TOPIC_TITLE,
            TOPIC_CREATION_DATE,
            TOPIC_UPDATE_DATE,
            TOPIC_BODY,
            false
        );
    }

}