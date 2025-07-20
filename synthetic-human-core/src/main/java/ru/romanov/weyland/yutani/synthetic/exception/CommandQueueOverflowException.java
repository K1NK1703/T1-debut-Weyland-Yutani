package ru.romanov.weyland.yutani.synthetic.exception;

public class CommandQueueOverflowException extends RuntimeException {
    public CommandQueueOverflowException(String message) {
        super(message);
    }
}
