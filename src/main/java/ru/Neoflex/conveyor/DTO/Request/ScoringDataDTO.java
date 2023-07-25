package ru.Neoflex.conveyor.DTO.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.Neoflex.conveyor.Enum.Gender;
import ru.Neoflex.conveyor.Enum.MaterialStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScoringDataDTO(
        //Сумма кредита
        BigDecimal amount,
        //Срок выдачи кредита в месяцах
        Integer term,
        //Имя
        String firstName,
        //Фамилия
        String lastName,
        //Отчество
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String middleName,
        //Пол
        Gender gender,
        //День рождения
        LocalDate birthdate,
        //Серия паспорта
        String passportSeries,
        //Номер паспорта
        String passportNumber,
        //Дата выдачи паспорта
        LocalDate passportIssueDate,
        //Кем выдан паспорт?
        String passportIssueBranch,
        //Материальный статус
        MaterialStatus maritalStatus,
        //TODO Зависимая сумма ?
        Integer dependentAmount,
        //Данные о работнике
        EmploymentDTO employment,
        //Аккаунт
        String account,
        //Включена ли страховка
        Boolean isInsuranceEnabled,
        //Зарплатный клиент (зарплата в этом банке)
        Boolean isSalaryClient
) {}
