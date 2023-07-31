package ru.Neoflex.conveyor.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.Neoflex.conveyor.DTO.Request.EmploymentDTO;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.DTO.Responce.CreditDTO;
import ru.Neoflex.conveyor.DTO.Responce.LoanOfferDTO;
import ru.Neoflex.conveyor.DTO.Responce.PaymentScheduleElement;
import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.Gender;
import ru.Neoflex.conveyor.Enum.MaterialStatus;
import ru.Neoflex.conveyor.Enum.WorkPosition;
import ru.Neoflex.conveyor.Service.ConveyorService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ConveyorControllerTest {

    @MockBean
    private ConveyorService conveyorService;

    //Создание LoanApplicationRequestDTO
    private final static LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(
            BigDecimal.valueOf(150000),
            24,
            "Ivan",
            "Petrov",
            null, // Отчество пустое, так как это поле необязательное
            "ivan@example.com",
            LocalDate.of(1990, 8, 10),
            "5678",
            "123456"
    );

    // Создание ScoringDataDTO
    private final static ScoringDataDTO scoringDataDTO = new ScoringDataDTO(
            BigDecimal.valueOf(100000),
            4,
            "Jane",
            "Smith",
            null,
            Gender.NOT_BINARY, // +3
            LocalDate.parse("2000-08-10"),
            "5678",
            "123456",
            LocalDate.parse("2005-03-12"),
            "Local Passport Office",
            MaterialStatus.MARRIED, // -3
            2, //+1
            new EmploymentDTO(
                    EmploymentStatus.SELF_EMPLOYED, //+1
                    "123456789012",
                    BigDecimal.valueOf(30000),
                    WorkPosition.TOP_MANAGER, //-2
                    28,
                    12
            ),
            "9876543210",
            true,
            true
    );

    @Test
    public void testCalculationPossibleCreditConditions() throws Exception {
        // Создаем список с 4 объектами LoanOfferDTO, которые ожидаем получить от сервиса
        List<LoanOfferDTO> expectedLoanOffers = new ArrayList<>();

        LoanOfferDTO loanOffer1 = new LoanOfferDTO(0L, BigDecimal.valueOf(150000), BigDecimal.valueOf(151500.00),
                24, BigDecimal.valueOf(6547.17), BigDecimal.valueOf(4.5), true, true);

        LoanOfferDTO loanOffer2 = new LoanOfferDTO(1L, BigDecimal.valueOf(150000), BigDecimal.valueOf(151500.00),
                24, BigDecimal.valueOf(6614.35), BigDecimal.valueOf(5.5), true, false);

        LoanOfferDTO loanOffer3 = new LoanOfferDTO(2L, BigDecimal.valueOf(150000), BigDecimal.valueOf(150000),
                24, BigDecimal.valueOf(6749.94), BigDecimal.valueOf(7.5), false, true);

        LoanOfferDTO loanOffer4 = new LoanOfferDTO(3L, BigDecimal.valueOf(150000), BigDecimal.valueOf(150000),
                24, BigDecimal.valueOf(6818.35), BigDecimal.valueOf(8.5), false, false);

        // Заполняем список с нужными значениями
        expectedLoanOffers.add(0,loanOffer1);
        expectedLoanOffers.add(1,loanOffer2);
        expectedLoanOffers.add(2,loanOffer3);
        expectedLoanOffers.add(3,loanOffer4);

        // Мокируем поведение метода в сервисе, чтобы он возвращал ожидаемый список
        when(conveyorService.calculationPossibleCreditConditions(requestDTO)).thenReturn(expectedLoanOffers);
    }



    @Test
    public void testCalculationCreditParameters() throws Exception {
        List<PaymentScheduleElement> expectedSchedule = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //Заполнение ожидаемым результатом
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
                BigDecimal.valueOf(25087.60).setScale(2, RoundingMode.HALF_DOWN),
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
        expectedSchedule.add(3,paymentScheduleElement);

        CreditDTO creditDTO = new CreditDTO(
                BigDecimal.valueOf(100000),
                4,
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(8.5),
                BigDecimal.valueOf(8.42),
                 true,
                 true,
                expectedSchedule
        );

        // Мокируем поведение метода в сервисе, чтобы он возвращал ожидаемую DTO
        when(conveyorService.calculationCreditParameters(scoringDataDTO)).thenReturn(creditDTO);
    }
}

