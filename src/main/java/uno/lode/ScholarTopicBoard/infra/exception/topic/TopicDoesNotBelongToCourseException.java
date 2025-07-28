package uno.lode.ScholarTopicBoard.infra.exception.topic;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDoesNotBelongToParentException;

public class TopicDoesNotBelongToCourseException extends EntityDoesNotBelongToParentException {
    private static final long serialVersionUID = 1L;
	public TopicDoesNotBelongToCourseException() {super("Topic", "Course");}
}
