package ru.romanov.weyland.yutani.synthetic.model;

import lombok.Getter;

@Getter
public enum CommandStatus {
    PENDING("Ожидает выполнения"),
    EXECUTING("Выполняется"),
    COMPLETED("Выполнена"),
    FAILED("Ошибка выполнения");

    private final String description;

    CommandStatus(String description) {
        this.description = description;
    }
}
