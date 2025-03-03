package ru.practicum.shareit.exception;

public class CommentNotFoundException extends Exception {
    public CommentNotFoundException(String message) {
        super(message);
    }
}