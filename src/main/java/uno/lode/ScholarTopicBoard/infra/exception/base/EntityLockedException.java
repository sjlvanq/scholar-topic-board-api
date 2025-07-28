package uno.lode.ScholarTopicBoard.infra.exception.base;

import org.springframework.http.HttpMethod;

public class EntityLockedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	public EntityLockedException(String entity, HttpMethod method) {
        super(String.join(" ", 
        		entity.substring(0, 1).toUpperCase()+entity.substring(1).toLowerCase(), //Capitalize
        		"operation locked on",method.toString()));
    }
}
