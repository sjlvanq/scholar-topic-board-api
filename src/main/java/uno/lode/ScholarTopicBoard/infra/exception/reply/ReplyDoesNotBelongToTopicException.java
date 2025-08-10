package uno.lode.ScholarTopicBoard.infra.exception.reply;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDoesNotBelongToParentException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;

public class ReplyDoesNotBelongToTopicException extends EntityDoesNotBelongToParentException {
    private static final long serialVersionUID = 1L;
	public ReplyDoesNotBelongToTopicException() {super(EntityDomain.REPLY,EntityDomain.TOPIC);}
}
