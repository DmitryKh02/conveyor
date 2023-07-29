package ru.Neoflex.conveyor.Exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InvalidDataException extends RuntimeException{
    private final List<InvalidField> invalidField;

    public InvalidDataException(List<InvalidField> invalidField) {
        super();
        this.invalidField = invalidField;
    }

    public List<InvalidField> getInvalidFields() {
        return invalidField;
    }
}
