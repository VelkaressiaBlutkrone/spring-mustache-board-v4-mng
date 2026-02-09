package com.example.v4.global.exception;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.example.v4.board.dto.BoardReponseDto;
import com.example.v4.board.dto.BoardRequestDto;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String GLOBAL_MESSAGE = "globalMessage";
    private static final String MESSAGE_PREFIX = "Warning: ";

    private static boolean acceptsHtml(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/html");
    }

    private static String messageBody(Exception ex) {
        return MESSAGE_PREFIX + ex.getMessage();
    }

    private static ResponseEntity<Void> redirectWithFlash(HttpServletRequest request, String message) {
        FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
        if (outputFlashMap != null) {
            outputFlashMap.put(GLOBAL_MESSAGE, message);
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
    }

    @ExceptionHandler(RestClientErrorException.class)
    public ResponseEntity<?> handleRestClientErrorException(RestClientErrorException ex,
            HttpServletRequest request) {
        if (acceptsHtml(request)) {
            return redirectWithFlash(request, messageBody(ex));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageBody(ex));
    }

    @ExceptionHandler(RestServerErrorException.class)
    public ResponseEntity<?> handleRestServerErrorException(RestServerErrorException ex,
            HttpServletRequest request) {
        if (acceptsHtml(request)) {
            return redirectWithFlash(request, messageBody(ex));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageBody(ex));
    }

    @ExceptionHandler(UserDuplicationException.class)
    public ResponseEntity<?> handleUserDuplicationException(UserDuplicationException ex,
            HttpServletRequest request) {
        if (acceptsHtml(request)) {
            return redirectWithFlash(request, messageBody(ex));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageBody(ex));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex,
            HttpServletRequest request) {
        if (acceptsHtml(request)) {
            return redirectWithFlash(request, messageBody(ex));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageBody(ex));
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<?> handleBoardNotFoundException(BoardNotFoundException ex,
            HttpServletRequest request) {
        if (acceptsHtml(request)) {
            return redirectWithFlash(request, messageBody(ex));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageBody(ex));
    }

    @ExceptionHandler(BoardAccessDeniedException.class)
    public ResponseEntity<?> handleBoardAccessDeniedException(BoardAccessDeniedException ex,
            HttpServletRequest request) {
        if (acceptsHtml(request)) {
            return redirectWithFlash(request, messageBody(ex));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(messageBody(ex));
    }

    @ExceptionHandler(InvalidBoardIdException.class)
    public ResponseEntity<?> handleInvalidBoardIdException(InvalidBoardIdException ex,
            HttpServletRequest request) {
        if (acceptsHtml(request)) {
            return redirectWithFlash(request, messageBody(ex));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageBody(ex));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public Object handleInvalidCredentialsException(InvalidCredentialsException ex,
            HttpServletRequest request) {
        if (acceptsHtml(request)) {
            ModelAndView mav = new ModelAndView("user/login-form");
            mav.addObject(GLOBAL_MESSAGE, messageBody(ex));
            if (ex.getDto() != null) {
                mav.addObject("dto", ex.getDto());
            }
            return mav;
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageBody(ex));
    }

    /**
     * @Valid 검증 실패 시 Spring이 던지는 예외.
     * BindingResult가 @Valid 파라미터 바로 다음에 없을 때 발생할 수 있음.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var bindingResult = ex.getBindingResult();
        if (!acceptsHtml(request)) {
            String msg = bindingResult.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MESSAGE_PREFIX + msg);
        }

        String path = request.getRequestURI();
        ModelAndView mav;

        if (path != null && path.contains("/board/")) {
            Object target = ex.getTarget();
            BoardRequestDto dto = target instanceof BoardRequestDto b ? b : new BoardRequestDto(null, null);
            String viewName = path.contains("/update") ? "board/update-form" : "board/save-form";

            mav = new ModelAndView(viewName);
            mav.addObject("dto", dto);
            Matcher m = Pattern.compile("/board/(\\d+)/update").matcher(path);
            if (m.find()) {
                String boardId = m.group(1);
                BoardReponseDto model = new BoardReponseDto(boardId, dto.getTitle(), dto.getContent(), null, null, List.of());
                mav.addObject("model", model);
            }
        } else if (path != null && (path.contains("/join") || path.contains("/login"))) {
            String viewName = path.contains("/join") ? "user/join-form" : "user/login-form";
            mav = new ModelAndView(viewName);
            mav.addObject("dto", ex.getTarget());
        } else {
            return redirectWithFlash(request, MESSAGE_PREFIX + "입력값을 확인해주세요.");
        }

        String globalMsg = MESSAGE_PREFIX + bindingResult.getFieldErrors().stream()
                .map(e -> e.getField() + ": " + (e.getDefaultMessage() != null ? e.getDefaultMessage() : ""))
                .collect(Collectors.joining("; "));
        mav.addObject(GLOBAL_MESSAGE, globalMsg);
        return mav;
    }

    @ExceptionHandler(ValidationException.class)
    public Object handleValidationException(ValidationException ex, HttpServletRequest request) {
        if (!acceptsHtml(request)) {
            String msg = ex.getBindingResult().getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MESSAGE_PREFIX + msg);
        }

        ModelAndView mav = new ModelAndView(ex.getViewName());

        String globalMsg = MESSAGE_PREFIX + ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + (e.getDefaultMessage() != null ? e.getDefaultMessage() : ""))
                .collect(Collectors.joining("; "));
        mav.addObject(GLOBAL_MESSAGE, globalMsg);
        mav.addObject("dto", ex.getDto());

        if (ex.getPathVariableValue() != null) {
            BoardRequestDto dto = ex.getDto() instanceof BoardRequestDto b ? b : new BoardRequestDto(null, null);
            BoardReponseDto model = new BoardReponseDto(
                    ex.getPathVariableValue(),
                    dto.getTitle(),
                    dto.getContent(),
                    null,
                    null,
                    List.of());
            mav.addObject("model", model);
        }

        return mav;
    }

}
