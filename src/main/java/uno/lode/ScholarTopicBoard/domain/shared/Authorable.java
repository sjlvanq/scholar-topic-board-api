package uno.lode.ScholarTopicBoard.domain.shared;

import uno.lode.ScholarTopicBoard.domain.user.User;

public interface Authorable {
	User getAuthor();

	public boolean isAuthoredBy(Long authorId);
}
