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
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyDetailDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.EntityAlreadyExistsExceptionDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.ErrorStatus400ResponseDTO;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.service.ReplyService;

@Tag(name = "Replies", description = "Manage replies to course topics" )

@RestController
@RequestMapping("/courses/{courseId}/topics/{topicId}/replies")
@SecurityRequirement(name = "bearer-key")
public class CourseTopicReplyController {

	private ReplyService replyService;
	public CourseTopicReplyController(ReplyService replyService) {
		this.replyService = replyService;
	}

	@Operation(summary = "List replies at root level with their children in a topic")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Replies retrieved successfully",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ReplyDetailDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No replies found", content = @Content),
	    @ApiResponse(responseCode = "400", description = "Bad request",
    		content = @Content(mediaType = "application/json",
        	examples = {
	    		@ExampleObject(name = "TopicNotBelongToCourse", value = "{\"code\": \"BAD_REQUEST_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Topic does not belong to Course\"}]}", 
	    			description = "Returned when the topic does not belong to the specified course."),
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
	    			description = "Returned when the courseId provided is not a number."),
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)", 
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
	    			description = "Returned when the topicId provided is not a number.")
	    	}, schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
        @ApiResponse(responseCode = "404", description = "Course or topic not found",
		content = @Content(mediaType = "application/json",
		examples = {
			@ExampleObject(name = "NOT_FOUND_404 (Course)", description = "Course not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}"),
			@ExampleObject(name = "NOT_FOUND_404 (Topic)", description = "Topic not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}")
		}))
    })

	@GetMapping
	public ResponseEntity<List<ReplyDetailDTO>> getAllReplies(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long courseId,
			@PathVariable Long topicId){
		List<ReplyDetailDTO> repliesInCourseTopic = replyService.getRootReplies(authUser, courseId, topicId);
		return repliesInCourseTopic.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(repliesInCourseTopic);
	}

	@Operation(summary = "Get reply by ID")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Reply retrieved successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReplyDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
			content = @Content(mediaType = "application/json",
	    	examples = {
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
	    			description = "Returned when the courseId provided is not a number."),
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)", 
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
	    			description = "Returned when the topicId provided is not a number."),
	    		@ExampleObject(name = "NOT_BELONG_400 (TopicNotBelongToCourse)", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Topic does not belong to Course\"}]}", 
    				description = "Returned when the topic does not belong to the specified course."),
	    		@ExampleObject(name = "NOT_BELONG_400 (ReplyNotBelongToTopic)", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Reply does not belong to Topic\"}]}", 
    				description = "Returned when the reply does not belong to the specified topic."),
	    	}, schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course, topic or reply not found",
    		content = @Content(mediaType = "application/json",
    		examples = {
    			@ExampleObject(name = "NOT_FOUND_404 (Course)", description = "Course not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}"),
    			@ExampleObject(name = "NOT_FOUND_404 (Topic)", description = "Topic not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}"),
    			@ExampleObject(name = "NOT_FOUND_404 (Reply)", description = "Reply not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Reply with id ? not found!\"\n}")
    	})),
	})

	@GetMapping("/{replyId}")
	public ResponseEntity<ReplyDetailDTO> getReply(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long courseId,
			@PathVariable Long topicId,
			@PathVariable Long replyId){
		return ResponseEntity.ok(replyService.getReply(authUser, courseId, topicId, replyId));
	}

	@Operation(summary = "Create a first-level reply")
	@ApiResponses({
	    @ApiResponse(responseCode = "201", description = "Reply created successfully",
	        content = @Content(mediaType = "application/json",
	        schema = @Schema(implementation = ReplyDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
			content = @Content(mediaType = "application/json",
	    	examples = {
		    	@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
		    		value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
		    		description = "Returned when the courseId provided is not a number."),
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)",
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
	    			description = "Returned when the topicId provided is not a number."),
	    		@ExampleObject(name = "NOT_BELONG_400 (TopicNotBelongToCourse)", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Topic does not belong to Course\"}]}", 
    				description = "Returned when the topic does not belong to the specified course.")
	    	}, schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course or topic not found",
    		content = @Content(mediaType = "application/json",
    		examples = {
    			@ExampleObject(name = "NOT_FOUND_404 (Course)", description = "Course not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}"),
    			@ExampleObject(name = "NOT_FOUND_404 (Topic)", description = "Topic not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}")
    	})),
	    @ApiResponse(responseCode = "409", description = "Conflict error",
        	content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityAlreadyExistsExceptionDTO.class)))
	})

	@PostMapping
	public ResponseEntity<ReplyDetailDTO> createRootReply(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long courseId,
			@PathVariable Long topicId,
			@RequestBody @Valid ReplyRegisterRequestDTO replyData,
			UriComponentsBuilder uriComponentsBuilder) {
		ReplyDetailDTO reply = replyService.createReply(authUser, courseId, topicId, null, replyData);
		URI url = uriComponentsBuilder.path("/replies/{id}").buildAndExpand(reply.id()).toUri();
		return ResponseEntity.created(url).body(reply);
	}

	@Operation(summary = "Create a child reply")
	@ApiResponses({
	    @ApiResponse(responseCode = "201", description = "Reply created successfully",
	        content = @Content(mediaType = "application/json",
	        schema = @Schema(implementation = ReplyDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
			content = @Content(mediaType = "application/json",
	    	examples = {
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
	    			description = "Returned when the courseId provided is not a number."),
	    		@ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)", 
	    			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
	    			description = "Returned when the topicId provided is not a number."),
	    		@ExampleObject(name = "NOT_BELONG_400 (TopicNotBelongToCourse)", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Topic does not belong to Course\"}]}", 
    				description = "Returned when the topic does not belong to the specified course.")
	    	}, schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course, topic or reply not found",
	    	content = @Content(mediaType = "application/json",
	    	examples = {
	    		@ExampleObject(name = "NOT_FOUND_404 (Course)", description = "Course not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}"),
	    		@ExampleObject(name = "NOT_FOUND_404 (Topic)", description = "Topic not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}"),
	    		@ExampleObject(name = "NOT_FOUND_404 (Reply)", description = "Parent reply not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Reply with id ? not found!\"\n}")
	    	})),
	    @ApiResponse(responseCode = "409", description = "Conflict error",
        	content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityAlreadyExistsExceptionDTO.class)))
	})

	@PostMapping("/{parentId}")
	public ResponseEntity<ReplyDetailDTO> createChildReply(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long courseId,
			@PathVariable Long topicId,
			@PathVariable Long parentId,
			@RequestBody @Valid ReplyRegisterRequestDTO replyData,
			UriComponentsBuilder uriComponentsBuilder) {
		ReplyDetailDTO reply = replyService.createReply(authUser, courseId, topicId, parentId, replyData);
		URI url = uriComponentsBuilder.path("/replies/{id}").buildAndExpand(reply.id()).toUri();
		return ResponseEntity.created(url).body(reply);
	}

	@Operation(summary = "Update reply")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Reply updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReplyDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
			content = @Content(mediaType = "application/json",
	    	examples = {
		    	@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
			    	value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
			    	description = "Returned when the courseId provided is not a number."),
			    @ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)", 
			    	value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
			    	description = "Returned when the topicId provided is not a number."),
			    @ExampleObject(name = "NOT_BELONG_400 (TopicNotBelongToCourse)", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Topic does not belong to Course\"}]}", 
		    		description = "Returned when the topic does not belong to the specified course."),
			    @ExampleObject(name = "NOT_BELONG_400 (TopicNotBelongToCourse)", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Reply does not belong to Topic\"}]}", 
		    		description = "Returned when the reply does not belong to the specified topic."),
	    	}, schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course, topic or reply not found",
	    	content = @Content(mediaType = "application/json",
	    	examples = {
	    		@ExampleObject(name = "NOT_FOUND_404 (Course)", description = "Course not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}"),
	    		@ExampleObject(name = "NOT_FOUND_404 (Topic)", description = "Topic not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}"),
	    		@ExampleObject(name = "NOT_FOUND_404 (Reply)", description = "Reply not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Reply with id ? not found!\"\n}")
	    	}))
	})

	@PutMapping("/{replyId}")
    public ResponseEntity<ReplyDetailDTO> updateReply(
    		@AuthenticationPrincipal AuthUser authUser,
    		@PathVariable Long courseId,
    		@PathVariable Long topicId,
    		@PathVariable Long replyId,
    		@RequestBody @Valid ReplyUpdateRequestDTO replyData){
    	return ResponseEntity.ok(replyService.updateReply(authUser, courseId, topicId, replyId, replyData));
    }

	@Operation(summary = "Delete reply")
	@ApiResponses({
	    @ApiResponse(responseCode = "204", description = "Reply successfully deleted", content = @Content),
	    @ApiResponse(responseCode = "400", description = "Bad request",
			content = @Content(mediaType = "application/json",
	    	examples = {
		    	@ExampleObject(name = "BAD_PATHVARIABLE_400 (CourseId)", 
					value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}",
					description = "Returned when the courseId provided is not a number."),
				@ExampleObject(name = "BAD_PATHVARIABLE_400 (TopicId)", 
		   			value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"topicId\",\"message\":\"Invalid topicId argument\"}]}",
		  			description = "Returned when the topicId provided is not a number."),
		   		@ExampleObject(name = "NOT_BELONG_400 (TopicNotBelongToCourse)", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Topic does not belong to Course\"}]}", 
					description = "Returned when the topic does not belong to the specified course."),
		   		@ExampleObject(name = "NOT_BELONG_400 (TopicNotBelongToCourse)", value = "{\"code\": \"NOT_BELONG_400\",\"fields\": [{\"field\": \"general\",\"message\": \"Reply does not belong to Topic\"}]}", 
					description = "Returned when the reply does not belong to the specified topic."),
	    	}, schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course, topic or reply not found",
	    	content = @Content(mediaType = "application/json",
	    	examples = {
	    		@ExampleObject(name = "NOT_FOUND_404 (Course)", description = "Course not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}"),
	    		@ExampleObject(name = "NOT_FOUND_404 (Topic)", description = "Topic not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Topic with id ? not found!\"\n}"),
	    		@ExampleObject(name = "NOT_FOUND_404 (Reply)", description = "Reply not found", value = "{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Reply with id ? not found!\"\n}")
	    	}))
	})

	@DeleteMapping("/{replyId}")
	public ResponseEntity<?> deleteReply(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long courseId,
			@PathVariable Long topicId,
			@PathVariable Long replyId){
		replyService.deleteReply(authUser, courseId, topicId, replyId);
		return ResponseEntity.noContent().build(); // noContent or isOK ?
	}

}
