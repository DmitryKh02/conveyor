package ru.Neoflex.conveyor.Exception;

public record Violation(
        String fieldName,
        String message) {
}
