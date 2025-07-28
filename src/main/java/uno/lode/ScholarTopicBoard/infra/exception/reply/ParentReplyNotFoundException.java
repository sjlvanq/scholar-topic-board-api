package uno.lode.ScholarTopicBoard.infra.exception.reply;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityNotFoundException;

public class ParentReplyNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;
	public ParentReplyNotFoundException(Long id) {super("Reply", id);}
	public ParentReplyNotFoundException(String name) {super("Reply", name);}
}
