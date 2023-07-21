package ru.Neoflex.conveyor.DTO;

import java.time.LocalDate;

record FinishRegistrationRequestDTO(
        Enum gender,
        Enum maritalStatus,
        Integer dependentAmount,
        LocalDate passportIssueDate,
        String passportIssueBranch,
        EmploymentDTO employment,
        String account
) {}
