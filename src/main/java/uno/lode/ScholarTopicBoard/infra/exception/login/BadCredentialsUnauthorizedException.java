package uno.lode.ScholarTopicBoard.infra.exception.login;

public class BadCredentialsUnauthorizedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public BadCredentialsUnauthorizedException(String message) {
		super(message);
	}
}
