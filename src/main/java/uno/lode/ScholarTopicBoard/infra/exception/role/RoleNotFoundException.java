package uno.lode.ScholarTopicBoard.infra.exception.role;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityNotFoundException;

public class RoleNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;
	public RoleNotFoundException(Long id) {super("Role", id);}
	public RoleNotFoundException(String msg) {super(msg);}
}
