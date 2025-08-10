package uno.lode.ScholarTopicBoard.infra.exception.base;

public abstract class EntityAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
	private final EntityDomain affectedEntity;
	private final String conflictingField;
	private final String conflictingValue;

	public EntityAlreadyExistsException(EntityDomain affectedEntity, String conflictingField, String conflictingValue) {
        super(String.format("%s with %s '%s' already exists!", affectedEntity.getDisplayName(), conflictingField, conflictingValue));
        this.affectedEntity = affectedEntity;
        this.conflictingField = conflictingField;
        this.conflictingValue = conflictingValue;
    }

	public EntityDomain getAffectedEntity() {
		return affectedEntity;
	}

	public String getConflictingField() {
		return conflictingField;
	}

	public String getConflictingValue() {
		return conflictingValue;
	}
	
}