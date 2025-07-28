package uno.lode.ScholarTopicBoard.infra.exception.role;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityAlreadyExistsException;

public class RoleAlreadyExistsException extends EntityAlreadyExistsException {
	private static final long serialVersionUID = 1L;
    public RoleAlreadyExistsException(String name) {
        super("A role", name);
    }
}
