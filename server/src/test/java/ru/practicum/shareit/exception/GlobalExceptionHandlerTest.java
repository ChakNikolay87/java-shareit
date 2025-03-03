package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleNoSuchElementExceptionTest() {
        NoSuchElementException exception = new NoSuchElementException("Элемент не найден");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Элемент не найден", response.getBody().getError());
    }

    @Test
    void handleItemNotAvailableExceptionTest() {
        ItemNotAvailableException exception = new ItemNotAvailableException("Предмет недоступен");

        ResponseEntity<String> response = globalExceptionHandler.handleItemNotAvailable(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Предмет недоступен", response.getBody());
    }

    @Test
    void handleIllegalArgumentExceptionTest() {
        IllegalArgumentException exception = new IllegalArgumentException("Неправильный аргумент");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConflict(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Неправильный аргумент", response.getBody().getError());
    }

    @Test
    void handleMethodArgumentNotValidExceptionTest() throws NoSuchMethodException {
        MethodParameter methodParameter = new MethodParameter(
                this.getClass().getDeclaredMethod("dummyMethod", String.class), 0);

        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "name", "must not be null"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationErrors(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getError());
        assertTrue(response.getBody().getError().contains("must not be null"));
    }

    public void dummyMethod(String name) {
    }



    @Test
    void handleMethodArgumentTypeMismatchExceptionTest() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(null, null, null, null, null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatch(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getError().contains("Некорректный тип аргумента"));
    }

    @Test
    void handleMissingRequestHeaderExceptionTest() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MethodParameter methodParameter = new MethodParameter(
                GlobalExceptionHandler.class.getMethod("handleMissingRequestHeader", MissingRequestHeaderException.class), 0);

        MissingRequestHeaderException exception = new MissingRequestHeaderException("X-Header", methodParameter);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMissingRequestHeader(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Отсутствует обязательный заголовок: X-Header", response.getBody().getError());
    }

    @Test
    void handleCommentNotFoundExceptionTest() {
        CommentNotFoundException exception = new CommentNotFoundException("Комментарий не найден");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCommentNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Комментарий не найден", response.getBody().getError());
    }

    @Test
    void handleBookingAccessExceptionTest() {
        BookingAccessException exception = new BookingAccessException("Доступ к бронированию запрещен");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBookingAccessException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Доступ к бронированию запрещен", response.getBody().getError());
    }

    @Test
    void handleGenericExceptionTest() {
        Exception exception = new Exception("Внутренняя ошибка сервера");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAllExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Внутренняя ошибка сервера", response.getBody().getError());
    }
}