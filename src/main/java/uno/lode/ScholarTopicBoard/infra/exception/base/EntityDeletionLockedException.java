package uno.lode.ScholarTopicBoard.infra.exception.base;

public abstract class EntityDeletionLockedException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public EntityDeletionLockedException(String msg) {
        super(msg);
    }
	public EntityDeletionLockedException(String entity, String lockingEntity) {
        super(String.join(" ", entity, lockingEntity)); //TODO
    }
}
