package ru.Neoflex.conveyor.DTO.Responce;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentScheduleElement(
        //Номер платежа
        Integer number,
        //Дата платежа
        LocalDate date,
        //TODO???
        BigDecimal totalPayment,
        BigDecimal interestPayment,
        BigDecimal debtPayment,
        BigDecimal remainingDebt
) {}