package ru.romanov.weyland.yutani.synthetic.model;

import lombok.Getter;

@Getter
public enum Priority {
    COMMON("Обычная"),
    CRITICAL("Критическая");

    private final String description;

    Priority(String description) {
        this.description = description;
    }
}
