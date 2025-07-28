package uno.lode.ScholarTopicBoard.infra.exception.user;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityAlreadyExistsException;

public class UserAlreadyExistsException extends EntityAlreadyExistsException {
	private static final long serialVersionUID = 1L;
    public UserAlreadyExistsException(String email) {
        super("An email", email);
    }
}
