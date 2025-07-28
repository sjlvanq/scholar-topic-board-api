package uno.lode.ScholarTopicBoard.infra.exception.user;

public class FakeUserNotFoundException extends UserNotFoundException {
	private static final long serialVersionUID = 1L;
	public FakeUserNotFoundException(Long id) {
		super(id);
	}
}
