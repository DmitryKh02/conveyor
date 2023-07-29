package ru.Neoflex.conveyor.DTO.Request;

import org.springframework.validation.annotation.Validated;
import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.Pattern.EmploymentStatusPattern;
import ru.Neoflex.conveyor.Enum.Pattern.WorkPositionPattern;
import ru.Neoflex.conveyor.Enum.WorkPosition;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Validated
public record EmploymentDTO(
        @EmploymentStatusPattern(status = {EmploymentStatus.UNEMPLOYED, EmploymentStatus.BUSINESS_OWNER, EmploymentStatus.SELF_EMPLOYED})
        //Статус работника
        EmploymentStatus employmentStatus,

        //ИНН
        String employerINN,

        //Зарплата по месяцам
        BigDecimal salary,

        @WorkPositionPattern(position = {WorkPosition.MIDDLE_MANAGER, WorkPosition.TOP_MANAGER})
        //Должность
        WorkPosition position,

        //Полный опыт работы
        Integer workExperienceTotal,

        //Опыт последней работы
        Integer workExperienceCurrent
) {}
