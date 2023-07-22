package ru.Neoflex.conveyor.DTO;

import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.WorkPosition;

import java.math.BigDecimal;

public record EmploymentDTO(
        Enum<EmploymentStatus> employmentStatus,
        String employerINN,
        BigDecimal salary,
        Enum<WorkPosition> position,
        Integer workExperienceTotal,
        Integer workExperienceCurrent
) {}
