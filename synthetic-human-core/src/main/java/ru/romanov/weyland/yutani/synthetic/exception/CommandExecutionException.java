package ru.romanov.weyland.yutani.synthetic.exception;

public class CommandExecutionException extends RuntimeException {
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}