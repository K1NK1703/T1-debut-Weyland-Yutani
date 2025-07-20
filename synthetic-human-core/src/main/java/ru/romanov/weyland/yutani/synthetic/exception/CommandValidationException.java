package ru.romanov.weyland.yutani.synthetic.exception;

public class CommandValidationException extends RuntimeException {
    public CommandValidationException(String message) {
        super(message);
    }
}