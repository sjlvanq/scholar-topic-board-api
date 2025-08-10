package uno.lode.ScholarTopicBoard.infra.exception.user;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;

public class UserAlreadyExistsException extends EntityAlreadyExistsException {
	private static final long serialVersionUID = 1L;
    public UserAlreadyExistsException(String email) {
        super(EntityDomain.USER, "email", email);
    }
}
