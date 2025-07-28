package uno.lode.ScholarTopicBoard.infra.exception.login;

public class UserBannedException  extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public UserBannedException(String message) {
		super(message);
	}
}
