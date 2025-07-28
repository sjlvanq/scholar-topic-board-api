package uno.lode.ScholarTopicBoard.domain.user;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uno.lode.ScholarTopicBoard.domain.course.Course;
import uno.lode.ScholarTopicBoard.domain.role.Role;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserRegisterRequestDTO;
import uno.lode.ScholarTopicBoard.domain.user.dto.UserUpdateRequestDTO;


@Table(name = "users")
@Entity(name = "User")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter private Long id;
	@Getter @Column(name = "first_name") private String firstName;
	@Getter @Column(name = "last_name") private String lastName;
	@Getter private String email;
	@Getter @Setter @Column(name = "passw") private String password;
	@Getter @Setter private boolean banned = false;
	@Getter @Setter private boolean deleted = false;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
	@Getter @Setter private List<Role> roles;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
        name = "users_courses",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
	@Getter @Setter private List<Course> courses;
	
	public User(UserRegisterRequestDTO userData) {
		this.firstName = userData.firstName();
		this.lastName = userData.lastName();
		this.email = userData.email();
		this.password = userData.password();
	}

	public void update(@Valid UserUpdateRequestDTO userData, String hashedPassword) {
		if(userData.firstName()!=null) {this.firstName=userData.firstName();}
		if(userData.lastName()!=null) {this.lastName=userData.lastName();}
		if(userData.email()!=null) {this.email=userData.email();}
		if(hashedPassword!=null) {this.password=hashedPassword;}
	}
}
