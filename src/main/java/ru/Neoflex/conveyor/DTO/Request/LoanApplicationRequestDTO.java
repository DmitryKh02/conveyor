package ru.Neoflex.conveyor.DTO.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;


@Validated
public record LoanApplicationRequestDTO(
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "10000", message = "Amount must be greater than or equal to 10000")
        //Сумма кредита
        BigDecimal amount,

        @NotNull(message = "Term cannot be null")
        @Min(value = 6, message = "Term must be greater than or equal to 6")
        //Срок выдачи кредита в месяцах
        Integer term,

        @NotBlank(message = "First name cannot be blank")
        @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Invalid first name format")
        //Имя
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Invalid last name format")
        //Фамилия
        String lastName,

        @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Invalid middle name format")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        //Отчество
        String middleName,

        @NotBlank(message = "Email cannot be blank")
        @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}", message = "Invalid email format")
        //E-mail
        String email,

        @NotNull(message = "Birthdate cannot be null")
        @Past(message = "Birthdate must be a past date")
        //День рождения
        LocalDate birthdate,

        @NotBlank(message = "Passport series cannot be blank")
        @Pattern(regexp = "\\d{4}", message = "Invalid passport series format")
        //Серия паспорта
        String passportSeries,

        @NotBlank(message = "Passport number cannot be blank")
        @Pattern(regexp = "\\d{6}", message = "Invalid passport number format")
        //Номер паспорта
        String passportNumber
) {}