package uno.lode.ScholarTopicBoard.infra.exception.course;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;

public class CourseAlreadyExistsException extends EntityAlreadyExistsException {
	private static final long serialVersionUID = 1L;
    public CourseAlreadyExistsException(String name) {
        super(EntityDomain.COURSE, "name", name);
    }
}
