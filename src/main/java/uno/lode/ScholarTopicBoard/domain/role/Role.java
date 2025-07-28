package uno.lode.ScholarTopicBoard.domain.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uno.lode.ScholarTopicBoard.domain.role.dto.RoleRequestDTO;

@Table(name = "roles")
@Entity(name = "Role")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Role {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter private Long id;
	@Getter private String name;
	@Column(name = "is_public")
	@Getter private Boolean isPublic;

	public Role(RoleRequestDTO roleData) {
		this.name = roleData.name();
		this.isPublic = roleData.isPublic() != null ? roleData.isPublic() : false;
	}
}
