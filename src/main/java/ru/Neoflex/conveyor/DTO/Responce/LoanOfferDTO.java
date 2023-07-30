package ru.Neoflex.conveyor.DTO.Responce;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Вариант кредитного предложения")
public record LoanOfferDTO(
        @Schema(description = "ID офера")
        Long applicationId,

        @Schema(description = "Запрошенная сумма кредита")
        BigDecimal requestedAmount,

        @Schema(description = "Итоговая сумма выдаваемого кредита")
        BigDecimal totalAmount,

        @Schema(description = "Срок выдачи кредита в месяцах")
        Integer term,

        @Schema(description = "Месячный платеж")
        BigDecimal monthlyPayment,

        @Schema(description = "Ставка по кредиту")
        BigDecimal rate,

        @Schema(description = "Включена ли страховка")
        Boolean isInsuranceEnabled,

        @Schema(description = "Зарплатный клиент (зарплата в этом банке)")
        Boolean isSalaryClient
) {}
