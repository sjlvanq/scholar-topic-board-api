package uno.lode.ScholarTopicBoard.infra.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import uno.lode.ScholarTopicBoard.infra.exception.base.EntityAlreadyExistsException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDeletionLockedException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityDoesNotBelongToParentException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityLockedException;
import uno.lode.ScholarTopicBoard.infra.exception.base.EntityNotFoundException;
import uno.lode.ScholarTopicBoard.infra.exception.dto.EntityAlreadyExistsExceptionDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.EntityLockedExceptionDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.EntityNotFoundExceptionDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.ErrorStatus400FieldDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.ErrorStatus400ResponseDTO;
import uno.lode.ScholarTopicBoard.infra.exception.dto.ErrorStatusResponseDTO;
import uno.lode.ScholarTopicBoard.infra.exception.login.BadCredentialsUnauthorizedException;
import uno.lode.ScholarTopicBoard.infra.exception.login.RoleAccessDeniedException;
import uno.lode.ScholarTopicBoard.infra.exception.login.UserBannedException;
import uno.lode.ScholarTopicBoard.infra.exception.reply.ReplyDepthLimitExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorStatusResponseDTO> handleAccessDenied(AccessDeniedException ex) {
    	return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
    			new ErrorStatusResponseDTO(ErrorStatusResponseCodes.FORBIDDEN_403, "Access denied!"));
    }

    @ExceptionHandler(UserBannedException.class)
    public ResponseEntity<ErrorStatusResponseDTO> handleAccessDenied(UserBannedException ex) {
    	return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
    			new ErrorStatusResponseDTO(ErrorStatusResponseCodes.USER_BANNED_403, ex.getMessage()));
    }
    
    @ExceptionHandler(RoleAccessDeniedException.class)
    public ResponseEntity<ErrorStatusResponseDTO> handleAccessDenied(RoleAccessDeniedException ex) {
    	return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
    			new ErrorStatusResponseDTO(ErrorStatusResponseCodes.NOT_ENROLLED_403, ex.getMessage()));
    }
    
    @ExceptionHandler(BadCredentialsUnauthorizedException.class)
    public ResponseEntity<ErrorStatusResponseDTO> handleAccessDenied(BadCredentialsUnauthorizedException ex) {
    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
    			new ErrorStatusResponseDTO(ErrorStatusResponseCodes.BAD_CREDENTIALS_401, ex.getMessage()));
    }
    
    @ExceptionHandler(EntityDoesNotBelongToParentException.class)
    public ResponseEntity<ErrorStatus400ResponseDTO> handleBelongToParent(EntityDoesNotBelongToParentException ex) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
    			new ErrorStatus400ResponseDTO(ErrorStatusResponseCodes.NOT_BELONG_400,
    					List.of(new ErrorStatus400FieldDTO(
    							"general", ex.getMessage()))));
    }

    @ExceptionHandler(EntityDeletionLockedException.class)
    public ResponseEntity<ErrorStatusResponseDTO> handleDeletionLocked(EntityDeletionLockedException ex) {
    	return ResponseEntity.status(HttpStatus.LOCKED).body(
    			new ErrorStatusResponseDTO(ErrorStatusResponseCodes.LOCKED_423, ex.getMessage()));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<EntityAlreadyExistsExceptionDTO> handleEntityExists(EntityAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
        		new EntityAlreadyExistsExceptionDTO(ErrorStatusResponseCodes.CONFLICT_409, ex.getMessage())); // 409 Conflict
    }

    //@ExceptionHandler(InvalidFormatException.class) //Type of field
    @ExceptionHandler(HttpMessageNotReadableException.class) //JSON Struct
    public ResponseEntity<ErrorStatus400ResponseDTO> handleJsonNotReadable(HttpMessageNotReadableException ex) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
    			new ErrorStatus400ResponseDTO(ErrorStatusResponseCodes.MALFORMED_400,
    					List.of(new ErrorStatus400FieldDTO(
    							"general", "Malformed request"))));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<EntityNotFoundExceptionDTO> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        		new EntityNotFoundExceptionDTO(ErrorStatusResponseCodes.NOT_FOUND_404, ex.getMessage()));
    }

    @ExceptionHandler(EntityLockedException.class)
    public ResponseEntity<EntityLockedExceptionDTO> handleLocked(EntityLockedException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED).body(
        		new EntityLockedExceptionDTO(ErrorStatusResponseCodes.LOCKED_423, ex.getMessage()));
    }
    
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorStatus400ResponseDTO> handleNotValid(BindException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        		new ErrorStatus400ResponseDTO(ErrorStatusResponseCodes.BAD_REQUEST_400,
        				ex.getAllErrors().stream().map(ErrorStatus400FieldDTO::new).toList()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorStatus400ResponseDTO> handleNotValidArgumentType(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        		new ErrorStatus400ResponseDTO(ErrorStatusResponseCodes.BAD_PATHVARIABLE_400,
        				List.of(new ErrorStatus400FieldDTO(
        						ex.getParameter().getParameterName(),
        						String.join(" ", "Invalid", ex.getParameter().getParameterName(), "argument")))));
    }

    @ExceptionHandler(ReplyDepthLimitExceededException.class)
    public ResponseEntity<ErrorStatus400ResponseDTO> handleDepthLevel(ReplyDepthLimitExceededException ex) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
    			new ErrorStatus400ResponseDTO(ErrorStatusResponseCodes.DEPTH_EXCEEDED_400,
    					List.of(new ErrorStatus400FieldDTO(
    							"general", ex.getMessage()))));
    }

}