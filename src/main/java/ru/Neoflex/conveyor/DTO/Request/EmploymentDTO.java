package ru.Neoflex.conveyor.DTO.Request;

import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.WorkPosition;

import java.math.BigDecimal;

public record EmploymentDTO(
        //Статус работника
        EmploymentStatus employmentStatus,
        //ИНН
        String employerINN,
        //Зарплата по месяцам
        BigDecimal salary,
        //Должность
        WorkPosition position,
        //Полный опыт работы
        Integer workExperienceTotal,
        //Опыт последней работы
        Integer workExperienceCurrent
) {}
