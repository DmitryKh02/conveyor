package ru.Neoflex.conveyor.DTO.Responce;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Итоговое кредитное предложение")
public record CreditDTO(
        @Schema(description = "Сумма кредита")
        BigDecimal amount,

        @Schema(description = "Срок выдачи кредита в месяцах")
        Integer term,

        @Schema(description = "Ежемесячный платеж")
        BigDecimal monthlyPayment,

        @Schema(description = "Ставка по кредиту")
        BigDecimal rate,

        @Schema(description = "Полная стоимость кредита")
        BigDecimal psk,

        @Schema(description = "Включена ли страховка")
        Boolean isInsuranceEnabled,

        @Schema(description = "Зарплатный клиент (зарплата в этом банке)")
        Boolean isSalaryClient,

        @Schema(description = "График ежемесячных платежей")
        List<PaymentScheduleElement> paymentSchedule
) {}