package uno.lode.ScholarTopicBoard.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import jakarta.validation.Valid;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithAuthorDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.EntityAlreadyExistsExceptionDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.ErrorStatus400ResponseDTO;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.service.TopicService;

@Tag(name = "Topics", description = "Topics management")

@RestController
@RequestMapping("/courses/{courseId}/topics")
@SecurityRequirement(name = "bearer-key")
public class CourseTopicController {

	private TopicService topicService;
	public CourseTopicController(TopicService topicService) {
		this.topicService = topicService;
	}

	@Operation(summary = "List topics in course")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Topics in course retrieved successfully",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = TopicWithAuthorDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No topics found in course", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad request", content =  @Content(
        	mediaType = "application/json",
        	examples = {
        		@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
        	    	value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
        	    	description = "Returned when the courseId provided is not a number.")
        	}
        )),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course not found",
    		content = @Content(mediaType = "application/json",
    		examples = @ExampleObject("{\"code\": \"NOT_FOUND_404\", \"message\": \"Course with id ? not found!\"\n}")))
    })

    @GetMapping
    public ResponseEntity<List<TopicWithAuthorDTO>> getAllTopicsFromCourse(
    		@AuthenticationPrincipal AuthUser authUser,
    		@PathVariable Long courseId){
    	List<TopicWithAuthorDTO> topicsInCourse = topicService.listByCourse(authUser, courseId);
		return topicsInCourse.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(topicsInCourse);
    }

	@Operation(summary = "Create topic")
	@ApiResponses({
	    @ApiResponse(responseCode = "201", description = "Topic created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopicDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course not found",
			content = @Content(mediaType = "application/json",
			examples = @ExampleObject("{\"code\": \"NOT_FOUND_404\", \"message\": \"Course with id ? not found!\"\n}"))),
	    @ApiResponse(responseCode = "409", description = "Conflict error",
        	content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityAlreadyExistsExceptionDTO.class)))
	})

	@PostMapping
	public ResponseEntity<TopicDetailDTO> createTopic(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long courseId,
			@RequestBody @Valid TopicRegisterRequestDTO topicData,
			UriComponentsBuilder uriComponentsBuilder) {
		TopicDetailDTO topicResponse = topicService.createTopic(authUser, courseId, topicData);
		URI url = uriComponentsBuilder.path("/courses/{courseId}/topics/{topicId}")
				.buildAndExpand(courseId, topicResponse.id()).toUri();
		return ResponseEntity.created(url).body(topicResponse);
	}

	@Operation(summary = "Get topic by ID")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Topic retrieved successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopicWithAuthorDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	    	content = @Content(mediaType = "application/json",
	    	examples = {
	    		@ExampleObject(name = "NOT_BELONG_400", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Topic does not belong to Course\"}]}", 
	    			description = "Returned when the topic does not belong to the specified course."),
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
	    			description = "Returned when the courseId provided is not a number."),
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)", 
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
	    			description = "Returned when the topicId provided is not a number.")
	    	},
	    	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Topic not found", //Course or Topic?
	    	content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}")))
	})

	@GetMapping("/{topicId}")
	public ResponseEntity<TopicWithAuthorDTO> getTopicFromCourse(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long courseId,
			@PathVariable Long topicId) {
		return ResponseEntity.ok(topicService.getTopic(authUser, courseId, topicId));
	}

	@Operation(summary = "Update topic")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Topic updated successfully",
	        content = @Content(mediaType = "application/json",
	        schema = @Schema(implementation = TopicUpdateRequestDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	    	content = @Content(mediaType = "application/json",
	    	examples = {
	    			@ExampleObject(name = "NOT_BELONG_400", value = """
	   					{
	   						"code": "NOT_BELONG_400",
	   						"fields": [
	   							{
	   								"field": "general",
	   								"message": "Topic does not belong to Course"
	   							}
	   						]
	   					}
	   					""", description = "Returned when the topic does not belong to the specified course."),
	   			@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
	   				value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
	   				description = "Returned when the courseId provided is not a number."),
	   			@ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)", 
	   				value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
	   				description = "Returned when the topicId provided is not a number.")
		    },
	    	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Topic or course not found",
	    	content = @Content(mediaType = "application/json",
	        examples = {
	        		@ExampleObject(
	        				name = "NOT_FOUND_404 (Topic)",
	        				value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}"),
	        		@ExampleObject(
	        				name = "NOT_FOUND_404 (Course)",
	        				value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}")
	        }))	})

	@PutMapping("/{topicId}")
    public ResponseEntity<TopicDetailDTO> updateTopic(
    		@AuthenticationPrincipal AuthUser authUser,
    		@PathVariable Long courseId,
    		@PathVariable Long topicId,
    		@RequestBody @Valid TopicUpdateRequestDTO topicData){
    	return ResponseEntity.ok(topicService.updateTopic(authUser, courseId, topicId, topicData));
    }

	@Operation(summary = "Delete topic (Admin, Moderator or Author only)")
	@ApiResponses({
	    @ApiResponse(responseCode = "204", description = "Topic successfully deleted", content = @Content),
	    @ApiResponse(responseCode = "400", description = "Bad request",
    	content = @Content(mediaType = "application/json",
    	examples = {
    		@ExampleObject(name = "NOT_BELONG_400", value = """
    				{
    					"code": "NOT_BELONG_400",
    					"fields": [
    						{
    							"field": "general",
    							"message": "Topic does not belong to Course"
    						}
    					]
    				}
    				""", description = "Returned when the topic does not belong to the specified course."),
    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
    			description = "Returned when the courseId provided is not a number."),
    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)", 
    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
    			description = "Returned when the topicId provided is not a number.")
    	},
    	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Topic or course not found",
	    	content = @Content(mediaType = "application/json",
	        examples = {
	        		@ExampleObject(
	        				name = "NOT_FOUND_404 (Topic)",
	        				value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}"),
	        		@ExampleObject(
	        				name = "NOT_FOUND_404 (Course)",
	        				value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}")
	        }))
	})

	@DeleteMapping("/{topicId}") // Access control in service
	public ResponseEntity<?> deleteTopic(
			@AuthenticationPrincipal AuthUser authUser,
    		@PathVariable Long courseId,
    		@PathVariable Long topicId){
		topicService.deleteTopic(authUser, courseId, topicId);
		return ResponseEntity.noContent().build();
	}

}
