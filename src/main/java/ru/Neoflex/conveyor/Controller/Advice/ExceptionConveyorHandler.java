package ru.Neoflex.conveyor.Controller.Advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.Neoflex.conveyor.Exception.InvalidDataException;
import ru.Neoflex.conveyor.Exception.InvalidField;

import java.util.List;

@ControllerAdvice
public class ExceptionConveyorHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDataException.class)
    public List<InvalidField> onInvalidDataException(InvalidDataException e) {
        return e.getInvalidFields();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFormatException.class)
    public InvalidField onInvalidFormatException(InvalidFormatException e) {
        return new InvalidField(e.getTargetType().toString(), e.getMessage());
    }
}
