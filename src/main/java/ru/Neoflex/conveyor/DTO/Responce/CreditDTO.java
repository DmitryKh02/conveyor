package ru.Neoflex.conveyor.DTO.Responce;

import java.math.BigDecimal;
import java.util.List;

public record CreditDTO(
        //Сумма кредита
        BigDecimal amount,
        //Срок выдачи кредита в месяцах
        Integer term,
        //Ежемесячный платеж
        BigDecimal monthlyPayment,
        //Ставка по кредиту
        BigDecimal rate,
        //Полная стоимость кредита
        BigDecimal psk,
        //Включена ли страховка
        Boolean isInsuranceEnabled,
        //Зарплатный клиент (зарплата в этом банке)
        Boolean isSalaryClient,
        //График ежемесячных платежей
        List<PaymentScheduleElement> paymentSchedule
) {}