package ru.Neoflex.conveyor.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.Neoflex.conveyor.Enum.Gender;
import ru.Neoflex.conveyor.Enum.MaterialStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScoringDataDTO(
        BigDecimal amount,
        Integer term,
        String firstName,
        String lastName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String middleName,
        Gender gender,
        LocalDate birthdate,
        String passportSeries,
        String passportNumber,
        LocalDate passportIssueDate,
        String passportIssueBranch,
        MaterialStatus maritalStatus,
        Integer dependentAmount,
        EmploymentDTO employment,
        String account,
        Boolean isInsuranceEnabled,
        Boolean isSalaryClient
) {}
