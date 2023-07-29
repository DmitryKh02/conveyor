package ru.Neoflex.conveyor.Enum.Pattern;

import ru.Neoflex.conveyor.Enum.Pattern.Validator.WorkPositionPatternValidator;
import ru.Neoflex.conveyor.Enum.WorkPosition;

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
@Constraint(validatedBy = WorkPositionPatternValidator.class)
public @interface WorkPositionPattern {
    WorkPosition[] position();
    String message() default "must be any of \"{position}\"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
