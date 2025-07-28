package uno.lode.ScholarTopicBoard.infra.exception.reply;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDoesNotBelongToParentException;

public class ParentReplyDoesNotBelongToTopicException extends EntityDoesNotBelongToParentException {
    private static final long serialVersionUID = 1L;
	public ParentReplyDoesNotBelongToTopicException() {super("Parent reply", "topic");}
}
