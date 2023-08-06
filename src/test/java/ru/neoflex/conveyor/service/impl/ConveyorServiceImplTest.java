package ru.neoflex.conveyor.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import ru.neoflex.conveyor.dto.response.PaymentScheduleElement;
import ru.neoflex.conveyor.utils.PreScoring;
import ru.neoflex.conveyor.utils.Scoring;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


//Данные брались с сайта: https://calculator-credit.ru/ !!!!

@RequiredArgsConstructor
@TestPropertySource(properties = {
        "credit.rate = 8.5",
        "credit.insurance=1"
})
@ExtendWith(MockitoExtension.class)
class ConveyorServiceImplTest {
    @MockBean
    private PreScoring preScoring;
    @MockBean
    private Scoring scoring;

    @InjectMocks
    private ConveyorServiceImpl conveyorService;

    private static final BigDecimal INSURANCE = BigDecimal.valueOf(1);

    @BeforeEach
    public void setup() {
        conveyorService.setCurrentAmount(BigDecimal.valueOf(100000)); // Выставление кредита для тестирования
        conveyorService.setCurrentTerm(12); // Выставление срока для тестирования
        conveyorService.setCurrentRate(BigDecimal.valueOf(8.5)); // Выставление ставки для тестирования
    }

    @Test
    void ConveyorService_CalculateAnnuityPayment_ReturnAnnuityPayment() {
        BigDecimal expectedPayment = BigDecimal.valueOf(8721.98).setScale(2, RoundingMode.HALF_UP); // Ожидаемый результат

        BigDecimal actualPayment = conveyorService.calculateAnnuityPayment(BigDecimal.valueOf(8.5));

        assertEquals(expectedPayment, actualPayment);
    }

    @Test
    void ConveyorService_CalculateFullCreditInsurance_ReturnInsurance() {
        BigDecimal expectedInsurance = BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP); // Ожидаемый результат

        BigDecimal actualInsurance = conveyorService.calculateFullCreditInsurance(INSURANCE);

        assertEquals(expectedInsurance, actualInsurance);
    }

    @Test
    void ConveyorService_CalculatePSK_ReturnPSK() {
        BigDecimal expectedPSK = BigDecimal.valueOf(5.66).setScale(2, RoundingMode.HALF_UP); // Ожидаемый результат

        BigDecimal actualPSK = conveyorService.calculatePSK(INSURANCE);

        assertEquals(expectedPSK, actualPSK);
    }

    @Test
    void ConveyorServiceImpl_CalculateTotalCreditCost_ReturnCost() {
        BigDecimal expectedTotalCost = BigDecimal.valueOf(106663.76).setScale(2, RoundingMode.HALF_UP); // Ожидаемый результат
        BigDecimal insuranceCost = BigDecimal.valueOf(2000); // Выставление полной стоимости страховки для теста

        BigDecimal actualTotalCost = conveyorService.calculateTotalCreditCost(insuranceCost);

        assertEquals(expectedTotalCost, actualTotalCost);
    }

    @Test
    void ConveyorService_CreatePaymentSchedule_ReturnPaymentScheduleDTO() {
        // Arrange
        BigDecimal monthlyPayment = BigDecimal.valueOf(25444.27).setScale(2, RoundingMode.HALF_UP); // Выставление месячного платежа для теста
        conveyorService.setCurrentTerm(4);
        List<PaymentScheduleElement> expectedSchedule = new ArrayList<>();

        PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement(
                1,
                LocalDate.now(),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(24735.94),
                BigDecimal.valueOf(708.33),
                BigDecimal.valueOf(75264.06));

        expectedSchedule.add(0, paymentScheduleElement);

        paymentScheduleElement = new PaymentScheduleElement(
                2,
                LocalDate.now().plusMonths(1),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(24911.15),
                BigDecimal.valueOf(533.12),
                BigDecimal.valueOf(50352.91));


        expectedSchedule.add(1, paymentScheduleElement);

        paymentScheduleElement = new PaymentScheduleElement(
                3,
                LocalDate.now().plusMonths(2),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(25087.60).setScale(2, RoundingMode.HALF_DOWN),
                BigDecimal.valueOf(356.67),
                BigDecimal.valueOf(25265.31));


        expectedSchedule.add(2, paymentScheduleElement);

        paymentScheduleElement = new PaymentScheduleElement(
                4,
                LocalDate.now().plusMonths(3),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(25265.31),
                BigDecimal.valueOf(178.96),
                BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_DOWN));


        expectedSchedule.add(3, paymentScheduleElement);

        List<PaymentScheduleElement> actualSchedule = conveyorService.calculatePaymentSchedule(monthlyPayment);

        assertEquals(expectedSchedule, actualSchedule);

    }
}