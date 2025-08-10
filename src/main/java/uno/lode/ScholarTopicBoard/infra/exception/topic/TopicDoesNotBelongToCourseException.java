package uno.lode.ScholarTopicBoard.infra.exception.topic;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDoesNotBelongToParentException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;

public class TopicDoesNotBelongToCourseException extends EntityDoesNotBelongToParentException {
    private static final long serialVersionUID = 1L;
	public TopicDoesNotBelongToCourseException() {super(EntityDomain.TOPIC, EntityDomain.COURSE);}
}
