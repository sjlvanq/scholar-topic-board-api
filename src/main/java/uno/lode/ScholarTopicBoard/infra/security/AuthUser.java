package uno.lode.ScholarTopicBoard.infra.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;


public class AuthUser implements UserDetails {
    private static final long serialVersionUID = 1L;
	@Getter private Long id;
    @Getter private String email;
    private String password;
    private List<GrantedAuthority> authorities;

    @Getter
    private final User user;
    
    private final Boolean isBanned;
    
    public AuthUser(User user) {
    	this.user = user;
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = user.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(r.getName()))
            .collect(Collectors.toList());
        this.isBanned = user.isBanned();
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }
	@Override public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}

	public boolean isAdmin() {
		return getAuthorities().stream().anyMatch(role -> ("ROLE_" + RoleConstants.ADMIN).equals(role.getAuthority()));
	}

	public boolean isCoord() {
		return getAuthorities().stream().anyMatch(role -> ("ROLE_" + RoleConstants.COORD).equals(role.getAuthority()));
	}
	
	public boolean isModerator() {
		return getAuthorities().stream().anyMatch(role -> ("ROLE_" + RoleConstants.MODERATOR).equals(role.getAuthority()));
	}
	
	public boolean isBanned() {
		return this.isBanned;
	}
	
	public boolean hasCourses() {
	    return user.getCourses() != null && !user.getCourses().isEmpty();
	}	
}