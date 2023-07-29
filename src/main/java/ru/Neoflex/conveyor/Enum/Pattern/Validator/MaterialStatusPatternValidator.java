package ru.Neoflex.conveyor.Enum.Pattern.Validator;

import ru.Neoflex.conveyor.Enum.MaterialStatus;
import ru.Neoflex.conveyor.Enum.Pattern.MaterialStatusPattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class MaterialStatusPatternValidator implements ConstraintValidator<MaterialStatusPattern, MaterialStatus> {
    private MaterialStatus[] statuses;
    @Override
    public void initialize(MaterialStatusPattern annotation) {
        this.statuses = annotation.status();
    }

    @Override
    public boolean isValid(MaterialStatus value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(statuses).contains(value);
    }
}
