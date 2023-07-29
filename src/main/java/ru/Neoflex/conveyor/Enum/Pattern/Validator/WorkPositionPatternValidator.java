package ru.Neoflex.conveyor.Enum.Pattern.Validator;

import ru.Neoflex.conveyor.Enum.Pattern.WorkPositionPattern;
import ru.Neoflex.conveyor.Enum.WorkPosition;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class WorkPositionPatternValidator implements ConstraintValidator<WorkPositionPattern, WorkPosition> {
    WorkPosition[] positions;
    @Override
    public void initialize(WorkPositionPattern annotation) {
        this.positions = annotation.position();
    }

    @Override
    public boolean isValid(WorkPosition value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(positions).contains(value);    }
}
