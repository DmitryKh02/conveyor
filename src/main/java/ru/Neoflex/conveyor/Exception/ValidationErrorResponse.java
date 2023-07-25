package ru.Neoflex.conveyor.Exception;

import java.util.List;
public record ValidationErrorResponse(
        List<Violation> violations
) {
}
