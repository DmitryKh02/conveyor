package ru.Neoflex.conveyor.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.WorkPosition;

import java.math.BigDecimal;

@Schema(description = "Сущность клиента")
public record EmploymentDTO(
        @Schema(description = "Статус работника")
        EmploymentStatus employmentStatus,

        @Schema(description = "ИНН")
        String employerINN,

        @Schema(description = "Зарплата в месяц")
        BigDecimal salary,

        @Schema(description = "Должность")
        WorkPosition position,

        @Schema(description = "Полный опыт работы")
        Integer workExperienceTotal,

        @Schema(description = "Опыт последней работы")
        Integer workExperienceCurrent
) {}
