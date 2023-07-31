package ru.Neoflex.conveyor.Service.Impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.Neoflex.conveyor.DTO.Responce.PaymentScheduleElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


//Данные брались с сайта: https://calculator-credit.ru/ !!!!


class ConveyorServiceImplTest {
    private static ConveyorServiceImpl conveyorService;
    private static final BigDecimal INSURANCE = BigDecimal.valueOf(1);

    @BeforeAll
    public static void setup() {
        conveyorService = new ConveyorServiceImpl();
        conveyorService.setCurrentAmount(BigDecimal.valueOf(100000)); // Выставление кредита для тестирования
        conveyorService.setCurrentTerm(12); // Выставление срока для тестирования
        conveyorService.setCurrentRate(BigDecimal.valueOf(8.5)); // Выставление ставки для тестирования
    }

    @Test
    public void calculateAnnuityPayment() {
        BigDecimal expectedPayment = BigDecimal.valueOf(8721.98).setScale(2, RoundingMode.HALF_UP); // Ожидаемый результат

        BigDecimal actualPayment = conveyorService.calculateAnnuityPayment(BigDecimal.valueOf(8.5));

        assertEquals(expectedPayment, actualPayment);
    }

    @Test
    public void calculateFullCreditInsurance() {
        BigDecimal expectedInsurance = BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP); // Ожидаемый результат

        BigDecimal actualInsurance = conveyorService.calculateFullCreditInsurance(INSURANCE);

        assertEquals(expectedInsurance, actualInsurance);
    }

    @Test
    public void calculatePSK() {
        BigDecimal expectedPSK = BigDecimal.valueOf(5.66).setScale(2, RoundingMode.HALF_UP); // Ожидаемый результат

        BigDecimal actualPSK = conveyorService.calculatePSK(INSURANCE);

        assertEquals(expectedPSK, actualPSK);
    }

    @Test
    public void calculateTotalLoanCost() {
        BigDecimal expectedTotalCost = BigDecimal.valueOf(106663.76).setScale(2, RoundingMode.HALF_UP); // Ожидаемый результат
        BigDecimal insuranceCost = BigDecimal.valueOf(2000); // Выставление полной стоимости страховки для теста

        BigDecimal actualTotalCost = conveyorService.calculateTotalLoanCost(insuranceCost);

        assertEquals(expectedTotalCost, actualTotalCost);
    }

    @Test
    public void calculatePaymentSchedule() {
        // Arrange
        BigDecimal monthlyPayment = BigDecimal.valueOf(25444.27).setScale(2, RoundingMode.HALF_UP); // Выставление месячного платежа для теста
        conveyorService.setCurrentTerm(4);
        List<PaymentScheduleElement> expectedSchedule = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement(
                 1,
                LocalDate.parse("2023-07-31", formatter),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(24735.94),
                BigDecimal.valueOf(708.33),
                BigDecimal.valueOf(75264.06));

        expectedSchedule.add(0,paymentScheduleElement);

        paymentScheduleElement = new PaymentScheduleElement(
                2,
                LocalDate.parse("2023-08-31", formatter),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(24911.15),
                BigDecimal.valueOf(533.12),
                BigDecimal.valueOf(50352.91));


        expectedSchedule.add(1,paymentScheduleElement);

        paymentScheduleElement = new PaymentScheduleElement(
                3,
                LocalDate.parse("2023-09-31", formatter),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(25087.60).setScale(2,RoundingMode.HALF_DOWN),
                BigDecimal.valueOf(356.67),
                BigDecimal.valueOf(25265.31));


        expectedSchedule.add(2,paymentScheduleElement);

        paymentScheduleElement = new PaymentScheduleElement(
                4,
                LocalDate.parse("2023-10-31", formatter),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(25265.31),
                BigDecimal.valueOf(178.96),
                BigDecimal.valueOf(0.00).setScale(2,RoundingMode.HALF_DOWN));
        {

        expectedSchedule.add(3,paymentScheduleElement);

        List<PaymentScheduleElement> actualSchedule = conveyorService.calculatePaymentSchedule(monthlyPayment);

        assertEquals(expectedSchedule, actualSchedule);
    }
    }
}