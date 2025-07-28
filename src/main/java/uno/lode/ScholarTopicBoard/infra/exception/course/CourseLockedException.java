package uno.lode.ScholarTopicBoard.infra.exception.course;

import org.springframework.http.HttpMethod;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityLockedException;

public class CourseLockedException extends EntityLockedException {
	private static final long serialVersionUID = 1L;

	public CourseLockedException(HttpMethod method){
		super("course", method);
	};
}
