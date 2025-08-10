package uno.lode.ScholarTopicBoard.infra.exception.base;

public abstract class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	private final EntityDomain affectedEntity;
	private final Long missingValue;

	public EntityNotFoundException(EntityDomain affectedEntity, String msg) {
		super(msg);
		this.affectedEntity = affectedEntity;
		this.missingValue = null;
	}

	public EntityNotFoundException(EntityDomain affectedEntity, Long missingId) {
		super(String.format("%s with id '%s' not found!", affectedEntity.getDisplayName(), missingId));
		this.affectedEntity = affectedEntity;
		this.missingValue = missingId;
	}

	public EntityDomain getAffectedEntity() {
		return affectedEntity;
	}

	public Long getMissingValue() {
		return missingValue;
	}
}
