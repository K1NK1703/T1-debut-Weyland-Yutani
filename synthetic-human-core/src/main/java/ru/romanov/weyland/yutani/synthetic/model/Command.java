package ru.romanov.weyland.yutani.synthetic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.romanov.weyland.yutani.synthetic.utils.CommandStatus;
import ru.romanov.weyland.yutani.synthetic.utils.Priority;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Command {

    final String id;

    @NotBlank(message = "Описание исполняемой команды не может быть пустым")
    @Size(max = 1000, message = "Описание исполняемой команды не может превышать 1000 символов")
    String description;

    @NotNull(message = "Приоритет выполнения команды не может быть пустым")
    Priority priority;

    @NotBlank(message = "Автор команды должен быть указан")
    @Size(max = 100, message = "Имя автора не может превышать 100 символов")
    String author;

    @NotNull(message = "Время назначения команды обязательно")
    @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime time;

    CommandStatus status;
    LocalDateTime executedAt;
    String executionResult;

    public Command() {
        this.id = UUID.randomUUID().toString();
        this.status = CommandStatus.PENDING;
    }

    public Command(String description, Priority priority, String author, LocalDateTime time) {
        this();
        this.description = description;
        this.priority = priority;
        this.author = author;
        this.time = time;
    }
}
