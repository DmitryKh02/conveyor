package ru.Neoflex.conveyor.DTO.Responce;

import java.math.BigDecimal;

public record LoanOfferDTO(
        //ID оффера
        Long applicationId,
        //Запрошенная сумма кредита
        BigDecimal requestedAmount,
        //Максимальная сумма выдаваемого кредита
        BigDecimal totalAmount,
        //Срок выдачи кредита в месяцах
        Integer term,
        //Месячный платеж
        BigDecimal monthlyPayment,
        //Ставка по кредиту
        BigDecimal rate,
        //Включена ли страховка
        Boolean isInsuranceEnabled,
        //Зарплатный клиент (зарплата в этом банке)
        Boolean isSalaryClient
) {}
