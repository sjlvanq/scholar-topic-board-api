package uno.lode.ScholarTopicBoard.infra.exception.reply;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDoesNotBelongToParentException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;

public class ParentReplyDoesNotBelongToTopicException extends EntityDoesNotBelongToParentException {
    private static final long serialVersionUID = 1L;
	public ParentReplyDoesNotBelongToTopicException() {super(EntityDomain.PARENT_REPLY, EntityDomain.TOPIC);}
}
