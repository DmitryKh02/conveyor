package ru.Neoflex.conveyor.DTO.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.Neoflex.conveyor.Enum.Gender;
import ru.Neoflex.conveyor.Enum.MaterialStatus;
import ru.Neoflex.conveyor.Enum.Pattern.GenderPattern;
import ru.Neoflex.conveyor.Enum.Pattern.MaterialStatusPattern;

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

        @JsonInclude(JsonInclude.Include.NON_NULL)
        //Отчество
        String middleName,

        @GenderPattern(gender = {Gender.FEMALE, Gender.MALE,Gender.NOT_BINARY})
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

        //Кем выдан паспорт
        String passportIssueBranch,

        @MaterialStatusPattern(status = {MaterialStatus.DIVORCED, MaterialStatus.MARRIED, MaterialStatus.SINGLE})
        //Материальный статус
        MaterialStatus maritalStatus,

        //Число иждивенцев
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
