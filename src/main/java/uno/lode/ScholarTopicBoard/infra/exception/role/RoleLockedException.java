package uno.lode.ScholarTopicBoard.infra.exception.role;

import org.springframework.http.HttpMethod;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityLockedException;

public class RoleLockedException extends EntityLockedException {
	private static final long serialVersionUID = 1L;

	public RoleLockedException(HttpMethod method){
		super("role", method);
	};
}
