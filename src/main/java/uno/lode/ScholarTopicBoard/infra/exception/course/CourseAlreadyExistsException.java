package uno.lode.ScholarTopicBoard.infra.exception.course;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityAlreadyExistsException;

public class CourseAlreadyExistsException extends EntityAlreadyExistsException {
	private static final long serialVersionUID = 1L;
    public CourseAlreadyExistsException(String name) {
        super("A course", "name", name);
    }
}
