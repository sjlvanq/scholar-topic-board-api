package uno.lode.ScholarTopicBoard.domain.reply;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.reply.dto.ReplyUpdateRequestDTO;
import uno.lode.ScholarTopicBoard.domain.shared.Authorable;
import uno.lode.ScholarTopicBoard.domain.topic.Topic;
import uno.lode.ScholarTopicBoard.domain.user.User;

@Table(name = "replies")
@Entity(name = "Reply")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Reply implements Authorable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_id", nullable = false)
	@Getter private Topic topic;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	@Getter private User author;

	@OrderColumn
	@CreationTimestamp
	@Column(name = "creation_date", updatable = false)
	@Getter private LocalDateTime creationDate;

	@UpdateTimestamp
	@Column(name = "update_date")
	@Getter private LocalDateTime updateDate;

	@Getter private String body;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	@JsonBackReference
	@Getter @Setter private Reply parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference
	// org.hibernate.loader.MultipleBagFetchException
    @Getter @Setter private Set<Reply> children = new TreeSet<>();

    public Reply(Topic topic, User author, Reply parent, ReplyRegisterRequestDTO replyData) {
        this.topic = topic;
        this.author = author;
        this.parent = parent;
        this.body = replyData.body();
    }

	public void update(ReplyUpdateRequestDTO replyData) {
		if(replyData.body()!=null) {this.body = replyData.body();}
	}

	@Override
	public boolean isAuthoredBy(Long authorId) {
		return this.author.getId().equals(authorId);
	}
}
