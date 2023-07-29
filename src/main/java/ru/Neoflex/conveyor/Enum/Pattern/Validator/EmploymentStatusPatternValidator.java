package ru.Neoflex.conveyor.Enum.Pattern.Validator;

import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.Pattern.EmploymentStatusPattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EmploymentStatusPatternValidator implements ConstraintValidator<EmploymentStatusPattern, EmploymentStatus> {
    private EmploymentStatus[] statuses;

    @Override
    public void initialize(EmploymentStatusPattern annotation) {
        this.statuses = annotation.status();
    }

    @Override
    public boolean isValid(EmploymentStatus value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(statuses).contains(value);
    }

}
