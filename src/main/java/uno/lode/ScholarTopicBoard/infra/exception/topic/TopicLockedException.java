package uno.lode.ScholarTopicBoard.infra.exception.topic;

import org.springframework.http.HttpMethod;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityLockedException;

public class TopicLockedException extends EntityLockedException {
	private static final long serialVersionUID = 1L;

	public TopicLockedException(HttpMethod method){
		super("topic", method);
	};
}
