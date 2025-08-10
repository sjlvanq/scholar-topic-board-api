package uno.lode.ScholarTopicBoard.infra.exception.topic;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;

public class TopicAlreadyExistsException extends EntityAlreadyExistsException {
	private static final long serialVersionUID = 1L;
    public TopicAlreadyExistsException(String name) {
        super(EntityDomain.TOPIC, "title", name);
    }
}
