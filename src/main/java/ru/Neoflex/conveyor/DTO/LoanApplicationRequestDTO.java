package ru.Neoflex.conveyor.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

record LoanApplicationRequestDTO(
        BigDecimal amount,
        Integer term,
        String firstName,
        String lastName,
        String middleName,
        String email,
        LocalDate birthdate,
        String passportSeries,
        String passportNumber
) {}