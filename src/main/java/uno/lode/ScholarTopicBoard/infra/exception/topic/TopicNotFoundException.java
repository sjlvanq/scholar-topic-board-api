package uno.lode.ScholarTopicBoard.infra.exception.topic;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityNotFoundException;

public class TopicNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;
	public TopicNotFoundException(Long id) {super("Topic", id);}
	public TopicNotFoundException(String name) {super("Topic", name);}
}
