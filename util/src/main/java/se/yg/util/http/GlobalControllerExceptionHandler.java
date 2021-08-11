package se.yg.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import se.yg.util.exceptions.InvalidInputException;
import se.yg.util.exceptions.NotFoundException;

import static org.springframework.http.HttpStatus.*;

// @ExceptionHandler, @ModelAttribute, @InitBinder 가 적용된 메서드들을 AOP를 적용해 컨트롤러 단에 적용하기 위해 고안된 애너테이션 입니다.
@RestControllerAdvice
class GlobalControllerExceptionHandler {
    // Spring-web 이 있으면 정상 동작 안함
    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class) // Not Found Exception 이 발생하면 아래 Error가 동작
    public @ResponseBody
    HttpErrorInfo handleNotFoundExceptions(ServerHttpRequest request, Exception ex) {

        return createHttpErrorInfo(NOT_FOUND, request, ex);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public @ResponseBody HttpErrorInfo handleInvalidInputException(ServerHttpRequest request, Exception ex) {

        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }


//    @ResponseStatus(BAD_REQUEST)
//    @ExceptionHandler(java.lang.TypeMismatchException)
//    public @ResponseBody HttpErrorInfo handleBadRequest(ServerHttpRequest request, Exception ex){
//        LOG.info("BAD REQUEST ------------------------");
//        return createHttpErrorInfo(BAD_REQUEST, request, ex);
//    }

    private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {
        final String path = request.getPath().pathWithinApplication().value();
        final String message = ex.getMessage();

        LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
        return new HttpErrorInfo(httpStatus, path, message);
    }
// Spring-web 이 있으면 정상 동작 안함
//    @ResponseStatus(NOT_FOUND)
//    @ExceptionHandler(NotFoundException.class) // Not Found Exception 이 발생하면 아래 Error가 동작
//    public @ResponseBody
//    HttpErrorInfo handleNotFoundExceptions1(ServletWebRequest request, Exception ex) {
//
//        return createHttpErrorInfo(NOT_FOUND, request, ex);
//    }
//
//    @ResponseStatus(UNPROCESSABLE_ENTITY)
//    @ExceptionHandler(InvalidInputException.class)
//    public @ResponseBody HttpErrorInfo handleInvalidInputException1(ServletWebRequest request, Exception ex) {
//
//        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
//    }
//
//
//    private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, ServletWebRequest request, Exception ex) {
//
//        final String path = request.getContextPath();
//        final String message = ex.getMessage();
//
//        LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
//        return new HttpErrorInfo(httpStatus, path, message);
//    }
}