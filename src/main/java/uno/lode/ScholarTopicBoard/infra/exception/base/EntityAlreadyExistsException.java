package uno.lode.ScholarTopicBoard.infra.exception.base;

public abstract class EntityAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	public EntityAlreadyExistsException(String msg) {
        super(msg);
    }
	public EntityAlreadyExistsException(String entity, String name) {
        super(entity + " with the value '" + name + "' already exists!");
    }
	public EntityAlreadyExistsException(String entity, String field, String value) {
        super(String.join(" ", entity, "with the", field, "'"+value+"'", "already exists!"));
    }
}
