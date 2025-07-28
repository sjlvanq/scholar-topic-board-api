package uno.lode.ScholarTopicBoard.infra.exception.base;

public abstract class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	public EntityNotFoundException(String msg) {
        super(msg);
    }
	public EntityNotFoundException(String entity, Long id) {
        super(entity + " with id " + id + " not found!");
    }
	public EntityNotFoundException(String entity, String name) {
        super(entity + " with name " + name + " not found!");
    }
}
