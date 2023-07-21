package ru.Neoflex.conveyor.DTO;

import java.math.BigDecimal;

record LoanOfferDTO(
        Long applicationId,
        BigDecimal requestedAmount,
        BigDecimal totalAmount,
        Integer term,
        BigDecimal monthlyPayment,
        BigDecimal rate,
        Boolean isInsuranceEnabled,
        Boolean isSalaryClient
) {}
