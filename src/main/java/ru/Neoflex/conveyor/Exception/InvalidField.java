package ru.Neoflex.conveyor.Exception;

public record InvalidField(
        String fieldName,
        String message) {
}
