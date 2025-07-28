package uno.lode.ScholarTopicBoard.domain.course;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.course.dto.CourseUpdateRequestDTO;

@Table(name = "courses")
@Entity(name = "Course")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Course {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter private Long id;
	@Getter private String name;
	@Getter private String description;
	@Getter private Boolean closed;

	public Course(@Valid CourseRegisterRequestDTO courseData) {
		this.name = courseData.name();
		this.description = courseData.description();
		this.closed = false;
	}

	public void update(@Valid CourseUpdateRequestDTO courseData) {
		if(courseData.name() != null) {this.name=courseData.name();}
		if(courseData.description() != null) {this.description=courseData.description();}
		if(courseData.closed() != null) {this.closed=courseData.closed();}
	}
}
