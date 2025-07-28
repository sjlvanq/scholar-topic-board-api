package uno.lode.ScholarTopicBoard.infra.exception.login;

public class RoleAccessDeniedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public RoleAccessDeniedException(String message) {
		super(message);
	}
}
