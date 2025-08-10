package uno.lode.ScholarTopicBoard.infra.exception.role;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityNotFoundException;

public class RoleNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;
	public RoleNotFoundException(String msg) {super(EntityDomain.ROLE, msg);}
	public RoleNotFoundException(Long id) {super(EntityDomain.ROLE, id);}
}
