package University.Dormitory.exception;

import University.Dormitory.exception.ExceptionResponseDto.ErrorResponse;
import University.Dormitory.exception.Handler.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("InvalidTokenException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordNotMatchException(PasswordNotMatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("PasswordNotMatchException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PKDuplicateException.class)
    public ResponseEntity<ErrorResponse> handlePKDuplicateException(PKDuplicateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("PKDuplicateException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenInvalidException(RefreshTokenInvalidException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("RefreshTokenInvalidException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TooLongException.class)
    public ResponseEntity<ErrorResponse> handleTooLongException(TooLongException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("TooLongException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UndefinedAuthorityException.class)
    public ResponseEntity<ErrorResponse> handleUndefinedAuthorityException(UndefinedAuthorityException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("UndefinedAuthorityException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UndefinedDormitoryException.class)
    public ResponseEntity<ErrorResponse> handleUndefinedDormitoryException(UndefinedDormitoryException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("UndefinedDormitoryException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("UserAlreadyExistException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("UserNotFoundException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadDateRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadDateRequestException(BadDateRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("BadDateRequestException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getReason(), ex.getMessage());
        log.error("BadDateRequestException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse("UNKNOWN_ERROR", ex.getMessage());
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UndefinedWorkDate.class)
    public ResponseEntity<ErrorResponse> handleUndefinedWorkDate(UndefinedWorkDate ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getError(), ex.getMessage());
        log.error("UndefinedWorkDate: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
