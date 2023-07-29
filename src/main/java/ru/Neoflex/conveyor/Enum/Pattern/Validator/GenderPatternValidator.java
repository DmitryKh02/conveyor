package ru.Neoflex.conveyor.Enum.Pattern.Validator;


import ru.Neoflex.conveyor.Enum.Gender;
import ru.Neoflex.conveyor.Enum.Pattern.GenderPattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class GenderPatternValidator implements ConstraintValidator<GenderPattern, Gender> {
    Gender[] genders;
    @Override
    public void initialize(GenderPattern annotation) {
        this.genders = annotation.gender();
    }

    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(genders).contains(value);
    }
}
