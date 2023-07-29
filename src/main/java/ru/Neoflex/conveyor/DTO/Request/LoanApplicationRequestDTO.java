package ru.Neoflex.conveyor.DTO.Request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanApplicationRequestDTO(
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

        //E-mail
        String email,

        //День рождения
        LocalDate birthdate,

        //Серия паспорта
        String passportSeries,

        //Номер паспорта
        String passportNumber
) {}