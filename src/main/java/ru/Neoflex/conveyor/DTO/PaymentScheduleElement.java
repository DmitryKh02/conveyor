package ru.Neoflex.conveyor.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentScheduleElement(
        Integer number,
        LocalDate date,
        BigDecimal totalPayment,
        BigDecimal interestPayment,
        BigDecimal debtPayment,
        BigDecimal remainingDebt
) {}