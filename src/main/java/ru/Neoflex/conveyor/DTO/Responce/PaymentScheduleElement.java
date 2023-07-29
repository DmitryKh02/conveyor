package ru.Neoflex.conveyor.DTO.Responce;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentScheduleElement(
        //Номер платежа
        Integer number,
        //Дата платежа
        LocalDate date,
        // Общая сумма платежа
        BigDecimal totalPayment,
        // Погашение долга
        BigDecimal interestPayment,
        // Погашение процентов
        BigDecimal debtPayment,
        // Оставшийся долг
        BigDecimal remainingDebt
) {}