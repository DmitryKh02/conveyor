package ru.Neoflex.conveyor.Exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.Utils.PreScoring;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvalidDataExceptionTest {

    private static LoanApplicationRequestDTO validRequest;
    private static LoanApplicationRequestDTO invalidRequest;

    @BeforeAll
    public static void setup() {
        // Создание валидной DTO
        validRequest = new LoanApplicationRequestDTO(
                BigDecimal.valueOf(20000),
                12,
                "John",
                "Doe",
                null,
                "john.doe@example.com",
                LocalDate.of(1990, 1, 1),
                "1234",
                "567890"
        );

        // Создание не валидной DTO
        invalidRequest = new LoanApplicationRequestDTO(
                BigDecimal.valueOf(2000),
                2,
                "J",
                "Doefsdfsdfafsdsfdasffdfadfdfasffdfasdfdfsafdsdsfdf",
                "",
                "@example.com",
                LocalDate.of(2024, 1, 1),
                "123466",
                "567"
        );

    }

    @Test
    public void testInvalidDataException() {
        try {
            PreScoring.isInformationCorrect(validRequest);
            // Не должно бросаться исключение
        } catch (InvalidDataException e) {
            // Если исключение брошено - тест провален
            Assertions.fail("Valid request should not throw InvalidDataException");
        }

        try {
            PreScoring.isInformationCorrect(invalidRequest);
            //Если исключение не брошено - тест провален
            Assertions.fail("Invalid request should throw InvalidDataException");
        } catch (InvalidDataException e) {
            // Исключение должно быть брошено и тест пройден
        }
    }
}
