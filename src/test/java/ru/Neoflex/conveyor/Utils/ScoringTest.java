package ru.Neoflex.conveyor.Utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.Neoflex.conveyor.DTO.Request.EmploymentDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.Gender;
import ru.Neoflex.conveyor.Enum.MaterialStatus;
import ru.Neoflex.conveyor.Enum.WorkPosition;
import ru.Neoflex.conveyor.Exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDate;

class ScoringTest {

    @Test
    public void testCalculateScoring() {
        // Установка необходимого для тестирования
        BigDecimal fixedCreditRate = BigDecimal.valueOf(8.5);
        BigDecimal excepted = BigDecimal.valueOf(6.5);

        // Создание валидного DTO
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

        try {
            BigDecimal result = Scoring.calculateScoring(scoringDataDTO, fixedCreditRate);

            Assertions.assertEquals(excepted, result);
        }
        catch (InvalidDataException e){
            Assertions.fail("Valid request should not throw InvalidDataException");
        }


        // Создание не валидного DTO
        scoringDataDTO = new ScoringDataDTO(
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

        try {
            BigDecimal result = Scoring.calculateScoring(scoringDataDTO, fixedCreditRate);
            Assertions.assertEquals(excepted, result);
            Assertions.fail("Invalid request should throw InvalidDataException");
        }
        catch (InvalidDataException e){
            // тест пройден, когда выброшено исключение
        }


    }

    @Test
    void calculateAge() {
        int age = 18;
        int result = Scoring.calculateAge(LocalDate.parse("2004-09-17"));

        Assertions.assertEquals(age, result);
    }
}