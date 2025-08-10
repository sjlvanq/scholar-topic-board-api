package uno.lode.ScholarTopicBoard.infra.exception.course;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDomain;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityNotFoundException;

public class CourseNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;
	public CourseNotFoundException(Long id) {super(EntityDomain.COURSE, id);}
	public CourseNotFoundException(String msg) {super(EntityDomain.COURSE, msg);}
}
