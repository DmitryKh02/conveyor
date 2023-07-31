package ru.Neoflex.conveyor.Utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.Exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDate;


class PreScoringTest {

    private static LoanApplicationRequestDTO validRequest;
    private static LoanApplicationRequestDTO invalidRequest;

    @BeforeAll
    public static void setup() {
        // Создание валидного dto
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

        // Создание не валидного dto
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
    public void testValidInformation() {
        // Не должно бросаться исключение
        Assertions.assertDoesNotThrow(() -> PreScoring.isInformationCorrect(validRequest));
    }

    @Test
    public void testInvalidInformation() {
        // Должно бросаться исключение
        Assertions.assertThrows(InvalidDataException.class, () -> PreScoring.isInformationCorrect(invalidRequest));
    }
}