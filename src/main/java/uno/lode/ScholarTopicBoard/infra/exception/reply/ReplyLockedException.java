package uno.lode.ScholarTopicBoard.infra.exception.reply;

import org.springframework.http.HttpMethod;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityLockedException;

public class ReplyLockedException extends EntityLockedException {
	private static final long serialVersionUID = 1L;

	public ReplyLockedException(HttpMethod method){
		super("reply", method);
	};
}
