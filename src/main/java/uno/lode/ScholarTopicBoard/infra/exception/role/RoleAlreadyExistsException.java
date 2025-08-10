package uno.lode.ScholarTopicBoard.infra.exception.role;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;

public class RoleAlreadyExistsException extends EntityAlreadyExistsException {
	private static final long serialVersionUID = 1L;
    public RoleAlreadyExistsException(String name) {
        super(EntityDomain.ROLE, "name", name);
    }
}
