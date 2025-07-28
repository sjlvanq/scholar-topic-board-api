package uno.lode.ScholarTopicBoard.infra.exception.user;

import org.springframework.http.HttpMethod;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityLockedException;

public class UserLockedException extends EntityLockedException {
	private static final long serialVersionUID = 1L;

	UserLockedException(HttpMethod method){
		super("user", method);
	};
}
