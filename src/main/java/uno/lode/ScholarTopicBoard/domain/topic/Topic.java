package uno.lode.ScholarTopicBoard.domain.topic;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.shared.Authorable;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.topic.dto.TopicUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.User;

@Table(name = "topics")
@Entity(name = "Topic")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topic implements Authorable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	@Getter private User author;

	@ManyToOne
	@JoinColumn(name = "course_id")
	@Getter private Course course;

	@Getter private String title;
	
	@CreationTimestamp
	@Column(name = "creation_date", updatable = false)
	@Getter private LocalDateTime creationDate;
	
	@UpdateTimestamp
	@Column(name = "update_date")
	@Getter private LocalDateTime updateDate;
	
	@Getter private String body;
	@Getter private boolean closed = false;

	public Topic(TopicRegisterRequestDTO topicRegisterData, User author, Course course) {
		this.author = author;
		this.course = course;
		this.title = topicRegisterData.title();
		this.body = topicRegisterData.body();
	}

	public void update(@Valid TopicUpdateRequestDTO topicData) {
		if(topicData.title()!=null) {this.title = topicData.title();}
		if(topicData.body()!=null) {this.body = topicData.body();}
		if(topicData.closed()!=null) {this.closed = topicData.closed();}
	}

	@Override
	public boolean isAuthoredBy(Long authorId) {
		return this.author.getId().equals(authorId);
	}
}
