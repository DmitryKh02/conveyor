package ru.Neoflex.conveyor.DTO;

import java.math.BigDecimal;

public record EmploymentDTO(
        Enum employmentStatus,
        String employerINN,
        BigDecimal salary,
        Enum position,
        Integer workExperienceTotal,
        Integer workExperienceCurrent
) {}
