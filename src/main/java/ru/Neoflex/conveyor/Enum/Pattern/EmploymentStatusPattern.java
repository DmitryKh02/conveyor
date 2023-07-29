package ru.Neoflex.conveyor.Enum.Pattern;

import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.Pattern.Validator.EmploymentStatusPatternValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = EmploymentStatusPatternValidator.class)
public @interface EmploymentStatusPattern {
    EmploymentStatus[] status();
    String message() default "must be any of \"{status}\"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}