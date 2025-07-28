package uno.lode.ScholarTopicBoard.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.infra.exception.login.BadCredentialsUnauthorizedException;
import uno.lode.ScholarTopicBoard.infra.exception.login.RoleAccessDeniedException;
import uno.lode.ScholarTopicBoard.infra.exception.login.UserBannedException;

@Service
public class AuthUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new AuthUser(
        		userRepository.findByEmailWithRoles(email)
        			.orElseThrow(()->new BadCredentialsUnauthorizedException("User email not found in system")));
    }

	public void validateAccess(AuthUser user) {
		if (user.isBanned()) {
			throw new UserBannedException("User is banned. Check your email for details.");
		}
		if (!user.isAdmin() && !user.isCoord() && !user.hasCourses()) {
			throw new RoleAccessDeniedException("User is not enrolled in any course yet. Enrollment must be done by an administrator or coordinator.");
		}
	}
}