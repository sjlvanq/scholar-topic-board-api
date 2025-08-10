package uno.lode.ScholarTopicBoard.infra.exception.user;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;
	public UserNotFoundException(Long id) {
        super(EntityDomain.USER, id);
    }
}
