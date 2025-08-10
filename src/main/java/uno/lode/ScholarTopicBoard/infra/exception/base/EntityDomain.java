package uno.lode.ScholarTopicBoard.infra.exception.base;

public enum EntityDomain {
	USER("User"), COURSE("Course"), TOPIC("Topic"), REPLY("Reply"), ROLE("Role");

	private final String displayName;

	EntityDomain(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}