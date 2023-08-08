package ru.neoflex.conveyor.exception;

public record InvalidField(
        String fieldName,
        String message) {
}
