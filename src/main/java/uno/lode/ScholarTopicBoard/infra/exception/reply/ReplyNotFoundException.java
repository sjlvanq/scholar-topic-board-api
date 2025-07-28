package uno.lode.ScholarTopicBoard.infra.exception.reply;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityNotFoundException;

public class ReplyNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;
	public ReplyNotFoundException(Long id) {super("Reply", id);}
	public ReplyNotFoundException(String name) {super("Reply", name);}
}
