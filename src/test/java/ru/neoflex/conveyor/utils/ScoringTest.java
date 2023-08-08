package ru.neoflex.conveyor.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.neoflex.conveyor.dto.request.EmploymentDTO;
import ru.neoflex.conveyor.dto.request.ScoringDataDTO;
import ru.neoflex.conveyor.enums.EmploymentStatus;
import ru.neoflex.conveyor.enums.Gender;
import ru.neoflex.conveyor.enums.MaterialStatus;
import ru.neoflex.conveyor.enums.WorkPosition;
import ru.neoflex.conveyor.exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDate;


class ScoringTest {
    private static Scoring scoring;

    @BeforeAll
    static void setup(){
        scoring = new Scoring();
    }

    @Test
    void Scoring_CalculateScoring_ReturnExceptedRate() {
        // Установка необходимого для тестирования
        BigDecimal fixedCreditRate = BigDecimal.valueOf(8.5);
        BigDecimal excepted = BigDecimal.valueOf(6.5);

        // Создание валидного DTO
        ScoringDataDTO validDataDTO = new ScoringDataDTO(
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

        try {
            BigDecimal result = scoring.calculateScoring(validDataDTO, fixedCreditRate);

            Assertions.assertEquals(excepted, result);
        }
        catch (InvalidDataException e){
            Assertions.fail("Valid request should not throw InvalidDataException");
        }
    }

    @Test
    void Scoring_CalculateScoring_ReturnException() {
        // Установка необходимого для тестирования
        BigDecimal fixedCreditRate = BigDecimal.valueOf(8.5);

        // Создание не валидного DTO
        ScoringDataDTO invalidDataDTO = new ScoringDataDTO(
                BigDecimal.valueOf(100000),
                4,
                "Jane",
                "Smith",
                null,
                Gender.NOT_BINARY,
                LocalDate.parse("2000-08-10"),
                "5678",
                "123456",
                LocalDate.parse("1940-03-12"), // More than 60 years
                "Local Passport Office",
                MaterialStatus.MARRIED,
                2,
                new EmploymentDTO(
                        EmploymentStatus.SELF_EMPLOYED,
                        "123456789012",
                        BigDecimal.valueOf(3000), //20 salaries isn't bigger than amount
                        WorkPosition.MIDDLE_MANAGER,
                        2, // <12
                        0 // <3
                ),
                "9876543210",
                true,
                true
        );

        Assertions.assertThrows(InvalidDataException.class, ()-> scoring.calculateScoring(invalidDataDTO,fixedCreditRate));
    }

    @Test
    void Scoring_calculateAge_ReturnRealAge() {
        int age = 24;
        int result = scoring.calculateAge(LocalDate.now().minusYears(24));

        Assertions.assertEquals(age, result);
    }
}