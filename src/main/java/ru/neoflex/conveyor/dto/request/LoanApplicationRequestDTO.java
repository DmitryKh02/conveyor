package ru.neoflex.conveyor.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Запрос на создание кредитного предложения")
public record LoanApplicationRequestDTO(
        @Schema(description = "Сумма кредита")
        BigDecimal amount,

        @Schema(description = "Срок выдачи кредита в месяцах")
        Integer term,

        @Schema(description = "Имя")
        String firstName,

        @Schema(description = "Фамилия")
        String lastName,

        @Schema(description = "Отчество")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String middleName,

        @Schema(description = "E-mail")
        String email,

        @Schema(description = "День рождения")
        LocalDate birthdate,

        @Schema(description = "Серия паспорта")
        String passportSeries,

        @Schema(description = "Номер паспорта")
        String passportNumber
) {}