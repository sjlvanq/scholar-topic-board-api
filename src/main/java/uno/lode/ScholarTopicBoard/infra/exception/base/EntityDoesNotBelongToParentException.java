package uno.lode.ScholarTopicBoard.infra.exception.base;

public abstract class EntityDoesNotBelongToParentException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public EntityDoesNotBelongToParentException(String entity, String parent) {
		super(String.join(" ", entity, "does not belong to", parent));
	}
}
