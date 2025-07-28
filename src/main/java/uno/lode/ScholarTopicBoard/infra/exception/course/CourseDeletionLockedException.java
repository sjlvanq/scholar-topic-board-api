package uno.lode.ScholarTopicBoard.infra.exception.course;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDeletionLockedException;

public class CourseDeletionLockedException extends EntityDeletionLockedException {
	private static final long serialVersionUID = 1L;
	public CourseDeletionLockedException() {
		super("The course cannot be deleted due to the existence of enrolled users");
	}
}
