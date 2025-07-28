package uno.lode.ScholarTopicBoard.infra.exception.role;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDeletionLockedException;

public class RoleDeletionLockedException extends EntityDeletionLockedException {
	private static final long serialVersionUID = 1L;
	public RoleDeletionLockedException() {
		super("The role cannot be deleted because it is currently assigned to one or more users");
	}
}
