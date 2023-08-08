package ru.neoflex.conveyor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.neoflex.conveyor.dto.request.EmploymentDTO;
import ru.neoflex.conveyor.dto.request.LoanApplicationRequestDTO;
import ru.neoflex.conveyor.dto.request.ScoringDataDTO;
import ru.neoflex.conveyor.dto.response.CreditDTO;
import ru.neoflex.conveyor.dto.response.LoanOfferDTO;
import ru.neoflex.conveyor.dto.response.PaymentScheduleElement;
import ru.neoflex.conveyor.enums.EmploymentStatus;
import ru.neoflex.conveyor.enums.Gender;
import ru.neoflex.conveyor.enums.MaterialStatus;
import ru.neoflex.conveyor.enums.WorkPosition;
import ru.neoflex.conveyor.service.ConveyorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ConveyorController.class)
@ExtendWith(MockitoExtension.class)
class ConveyorControllerTest {
    @MockBean
    private ConveyorService conveyorService;
    @InjectMocks
    private ConveyorController conveyorController;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ConveyorController_CalculationCreditParameters_ReturnLoanOfferDTO() throws Exception {
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                BigDecimal.valueOf(150000),
                24,
                "Ivan",
                "Petrov",
                null,
                "ivan@example.com",
                LocalDate.of(1990, 8, 10),
                "5678",
                "123456"
        );

        List<LoanOfferDTO> expectedLoanOffers = new ArrayList<>();

        LoanOfferDTO loanOffer1 = new LoanOfferDTO(0L, BigDecimal.valueOf(150000), BigDecimal.valueOf(151500.00),
                24, BigDecimal.valueOf(6547.17), BigDecimal.valueOf(4.5), true, true);

        LoanOfferDTO loanOffer2 = new LoanOfferDTO(1L, BigDecimal.valueOf(150000), BigDecimal.valueOf(151500.00),
                24, BigDecimal.valueOf(6614.35), BigDecimal.valueOf(5.5), true, false);

        LoanOfferDTO loanOffer3 = new LoanOfferDTO(2L, BigDecimal.valueOf(150000), BigDecimal.valueOf(150000),
                24, BigDecimal.valueOf(6749.94), BigDecimal.valueOf(7.5), false, true);

        LoanOfferDTO loanOffer4 = new LoanOfferDTO(3L, BigDecimal.valueOf(150000), BigDecimal.valueOf(150000),
                24, BigDecimal.valueOf(6818.35), BigDecimal.valueOf(8.5), false, false);

        expectedLoanOffers.add(0,loanOffer1);
        expectedLoanOffers.add(1,loanOffer2);
        expectedLoanOffers.add(2,loanOffer3);
        expectedLoanOffers.add(3,loanOffer4);

        when(conveyorService.calculationPossibleCreditConditions(loanApplicationRequestDTO)).thenReturn(expectedLoanOffers);

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].applicationId").value(0L))
                .andExpect(jsonPath("$[0].requestedAmount").value(150000))
                .andExpect(jsonPath("$[0].totalAmount").value(151500.00))
                .andExpect(jsonPath("$[0].term").value(24))
                .andExpect(jsonPath("$[0].monthlyPayment").value(6547.17))
                .andExpect(jsonPath("$[0].rate").value(4.5))
                .andExpect(jsonPath("$[0].isInsuranceEnabled").value(true))
                .andExpect(jsonPath("$[0].isSalaryClient").value(true));
    }

    @Test
    void ConveyorController_CalculationCreditParameters_ReturnCreditDTO() throws Exception {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(
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

        PaymentScheduleElement scheduleItem1 = new PaymentScheduleElement(
                1,
                LocalDate.of(2023, 8, 6),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(24735.94),
                BigDecimal.valueOf(708.33),
                BigDecimal.valueOf(75264.06)
        );

        PaymentScheduleElement scheduleItem2 = new PaymentScheduleElement(
                2,
                LocalDate.of(2023, 9, 6),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(24911.15),
                BigDecimal.valueOf(533.12),
                BigDecimal.valueOf(50352.91)
        );

        PaymentScheduleElement scheduleItem3 = new PaymentScheduleElement(
                3,
                LocalDate.of(2023, 10, 6),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(25087.60),
                BigDecimal.valueOf(356.67),
                BigDecimal.valueOf(25265.31)
        );

        PaymentScheduleElement scheduleItem4 = new PaymentScheduleElement(
                4,
                LocalDate.of(2023, 11, 6),
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(25265.31),
                BigDecimal.valueOf(178.96),
                BigDecimal.valueOf(0.00)
        );

        List<PaymentScheduleElement> paymentSchedule = List.of(scheduleItem1, scheduleItem2, scheduleItem3, scheduleItem4);

        CreditDTO creditDTO = new CreditDTO(
                BigDecimal.valueOf(100000),
                4,
                BigDecimal.valueOf(25444.27),
                BigDecimal.valueOf(8.5),
                BigDecimal.valueOf(8.42),
                true,
                true,
                paymentSchedule
        );

        when(conveyorService.calculationCreditParameters(scoringDataDTO)).thenReturn(creditDTO);

        mockMvc.perform(post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringDataDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(100000))
                .andExpect(jsonPath("$.term").value(4))
                .andExpect(jsonPath("$.monthlyPayment").value(25444.27))
                .andExpect(jsonPath("$.rate").value(8.5))
                .andExpect(jsonPath("$.psk").value(8.42))
                .andExpect(jsonPath("$.isInsuranceEnabled").value(true))
                .andExpect(jsonPath("$.isSalaryClient").value(true))
                .andExpect(jsonPath("$.paymentSchedule", hasSize(4)))
                .andExpect(jsonPath("$.paymentSchedule[0].number").value(1))
                .andExpect(jsonPath("$.paymentSchedule[0].date").value("2023-08-06"))
                .andExpect(jsonPath("$.paymentSchedule[0].totalPayment").value(25444.27))
                .andExpect(jsonPath("$.paymentSchedule[0].interestPayment").value(24735.94))
                .andExpect(jsonPath("$.paymentSchedule[0].debtPayment").value(708.33))
                .andExpect(jsonPath("$.paymentSchedule[0].remainingDebt").value(75264.06));
    }
}

