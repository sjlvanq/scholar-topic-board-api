package uno.lode.ScholarTopicBoard.infra.exception.reply;

public class ReplyDepthLimitExceededException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public ReplyDepthLimitExceededException() {
		super("Reply depth limit reached");
	}
}
