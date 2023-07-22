package ru.Neoflex.conveyor.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanApplicationRequestDTO(
        BigDecimal amount,
        Integer term,
        String firstName,
        String lastName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String middleName,
        String email,
        LocalDate birthdate,
        String passportSeries,
        String passportNumber
) {}