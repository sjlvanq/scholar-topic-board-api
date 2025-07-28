package uno.lode.ScholarTopicBoard.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
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
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseDetailDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;
import uno.lode.ScholarTopicBoard.infra.exception.dto.ErrorStatus400ResponseDTO;
import uno.lode.ScholarTopicBoard.service.CourseService;

@Tag(name = "Courses", description = "Course management")

@RestController
@RequestMapping("/courses")
@SecurityRequirement(name = "bearer-key")
public class CourseController {

    private CourseService courseService;
	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@Operation(summary = "List all courses")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = CourseDetailDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No courses found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CourseDetailDTO>> getAllCourses(){
    	List<CourseDetailDTO> courses = courseService.getAllCourses();
    	return courses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(courses);
    }

	@Operation(summary = "Create course (Admin only)")
    @ApiResponses({
	    @ApiResponse(responseCode = "201", description = "Course created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	        content = @Content(mediaType = "application/json",
	        schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "409", description = "Conflict error",
        	content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\"code\": \"CONFLICT_409\",  \"message\": \"A course with the name 'Example' already exists!\"}")))
	})
    @PostMapping
    @RolesAllowed(RoleConstants.ADMIN)
    public ResponseEntity<CourseDetailDTO> createCourse(@RequestBody @Valid CourseRegisterRequestDTO courseData,
                                                                UriComponentsBuilder uriComponentsBuilder) {
    	CourseDetailDTO course = courseService.createCourse(courseData);
    	URI url = uriComponentsBuilder.path("/courses/{courseId}").buildAndExpand(course.id()).toUri();
        return ResponseEntity.created(url).body(course);
    }

	@Operation(summary = "Get course by ID")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Course retrieved successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	    	content = @Content(mediaType = "application/json",
	    	examples = {
	    		@ExampleObject("{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}")
	    	})),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course not found",
	    	content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}")))
	})
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailDTO> getCourse(@PathVariable Long courseId){
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

	@Operation(summary = "Update course (Admin or Coordinator only)")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Course updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
	        content = @Content(mediaType = "application/json",
	        schema = @Schema(implementation = ErrorStatus400ResponseDTO.class),
	    	examples = {
		    	@ExampleObject(name = "BAD_PATHVARIABLE_400",
		    		value = "{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}"),
	    		@ExampleObject(name = "MALFORMED_400",
    				value = "{\"code\":\"MALFORMED_400\",\"fields\":[{\"field\":\"general\",\"message\":\"Malformed request\"}]}"),
		    })),
	    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course not found",
	    	content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}"))),
	    @ApiResponse(responseCode = "409", description = "Conflict error",
	    	content = @Content(mediaType = "application/json",
	    	examples = @ExampleObject("{\"code\": \"CONFLICT_409\",  \"message\": \"A course with the name 'Example' already exists!\"}")))
	})
    @PutMapping("/{courseId}")
	@RolesAllowed({RoleConstants.ADMIN, RoleConstants.COORD})
    public ResponseEntity<CourseDetailDTO> updateCourse(@PathVariable Long courseId, @RequestBody @Valid CourseUpdateRequestDTO courseData){
    	return ResponseEntity.ok(courseService.updateCourse(courseId, courseData));
    }

	@Operation(summary = "Delete course (Admin only)")
	@ApiResponses({
	    @ApiResponse(responseCode = "204", description = "Course successfully deleted",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetailDTO.class))),
	    @ApiResponse(responseCode = "400", description = "Bad request",
    		content = @Content(mediaType = "application/json",
    		examples = {
    			@ExampleObject("{\"code\":\"BAD_PATHVARIABLE_400\",\"fields\":[{\"field\":\"courseId\",\"message\":\"Invalid courseId argument\"}]}")
    		},
        	schema = @Schema(implementation = ErrorStatus400ResponseDTO.class))),
	    @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content),
	    @ApiResponse(responseCode = "404", description = "Course not found",
	    	content = @Content(mediaType = "application/json",
	        examples = @ExampleObject("{\n \"code\": \"NOT_FOUND_404\",\n  \"message\": \"Course with id ? not found!\"\n}"))),
	    @ApiResponse(responseCode = "423", description = "Locked error",
    		content = @Content(mediaType = "application/json",
        	examples = @ExampleObject("{\n \"code\": \"LOCKED_423\",\n  \"message\": \"The course cannot be deleted due to the existence of enrolled users\"\n}")))
	})
	@DeleteMapping("/{courseId}")
	@RolesAllowed(RoleConstants.ADMIN)
	public ResponseEntity<?> deleteCourse(@PathVariable Long courseId){
		courseService.deleteCourse(courseId);
		return ResponseEntity.noContent().build();
	}

}
