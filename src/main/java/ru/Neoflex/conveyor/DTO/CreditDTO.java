package ru.Neoflex.conveyor.DTO;

import java.math.BigDecimal;
import java.util.List;

public record CreditDTO(
        BigDecimal amount,
        Integer term,
        BigDecimal monthlyPayment,
        BigDecimal rate,
        BigDecimal psk,
        Boolean isInsuranceEnabled,
        Boolean isSalaryClient,
        List<PaymentScheduleElement> paymentSchedule
) {}