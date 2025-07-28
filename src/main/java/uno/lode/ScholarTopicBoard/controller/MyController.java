package uno.lode.ScholarTopicBoard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicDetailDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicWithCourseDTO;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.service.CourseService;
import uno.lode.ScholarTopicBoard.service.TopicService;

@Tag(name = "My Data", description = "Authenticated user operations")

@RestController
@RequestMapping("/my")
@SecurityRequirement(name = "bearer-key")
public class MyController {

    private CourseService courseService;
	private TopicService topicService;
	private MyController(CourseService courseService, TopicService topicService) {
		this.courseService = courseService;
		this.topicService = topicService;
	}

	@Operation(summary="List my courses")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = CourseDetailDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No courses found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })

    @GetMapping("/courses")
    public ResponseEntity<List<CourseDetailDTO>> getMyCourses(@AuthenticationPrincipal AuthUser myUser){
    	List<CourseDetailDTO> myCourses = courseService.getCoursesByUserId(myUser.getId());
        return myCourses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(myCourses);
    }

    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Topics retrieved successfully",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = TopicDetailDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No topics found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })

    @Operation(summary = "List my topics")
    @GetMapping("/topics")
    public ResponseEntity<List<TopicWithCourseDTO>> getMyTopics(@AuthenticationPrincipal AuthUser myUser){
    	List<TopicWithCourseDTO> myTopics = topicService.listByLoggedUser(myUser);
    	return myTopics.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(myTopics);
    }

}
