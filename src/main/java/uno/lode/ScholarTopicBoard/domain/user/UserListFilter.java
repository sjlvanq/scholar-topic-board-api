package uno.lode.ScholarTopicBoard.domain.user;

public enum UserListFilter {
	ALL("all"),
	ACTIVE("active"),
	DELETED("deleted");
	
	private final String value;
	UserListFilter(String value){
		this.value = value;
	}

    public String getValue() {
        return value;
    }

    public static UserListFilter fromValue(String value) {
        for (UserListFilter type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(); //TODO
    }
}