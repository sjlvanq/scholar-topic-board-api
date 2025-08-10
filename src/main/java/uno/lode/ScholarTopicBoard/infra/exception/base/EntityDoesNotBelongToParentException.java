package uno.lode.ScholarTopicBoard.infra.exception.base;

public abstract class EntityDoesNotBelongToParentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final EntityDomain affectedEntity;
	private final EntityDomain parentEntity;

	public EntityDoesNotBelongToParentException(EntityDomain affectedEntity, EntityDomain parentEntity) {
		super(String.format("%s does not belong to %s", affectedEntity.getDisplayName(), parentEntity.getDisplayName()));
		this.affectedEntity = affectedEntity;
		this.parentEntity = parentEntity;
	}

	public EntityDomain getAffectedEntity() {
		return affectedEntity;
	}

	public EntityDomain getParentEntity() {
		return parentEntity;
	}
	
}
