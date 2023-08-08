package ru.neoflex.conveyor.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.neoflex.conveyor.dto.request.LoanApplicationRequestDTO;
import ru.neoflex.conveyor.exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDate;


class PreScoringTest {
    private  static PreScoring preScoring;

    @BeforeAll
    public static void setup() {
        preScoring = new PreScoring();
    }

    @Test
    void PreScoring_IsInformationCorrect_ReturnPass() {
        //Создание валидных данных
        LoanApplicationRequestDTO validRequest = new LoanApplicationRequestDTO(
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

        try {
            preScoring.isInformationCorrect(validRequest);
            // Не должно бросаться исключение
        } catch (InvalidDataException e) {
            // Если исключение брошено - тест провален
            Assertions.fail("Valid request should not throw InvalidDataException");
        }
    }

    @Test
    void PreScoring_IsInformationCorrect_ReturnException() {
        //Создание не валидных данных
        LoanApplicationRequestDTO invalidRequest = new LoanApplicationRequestDTO(
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

        Assertions.assertThrows(InvalidDataException.class, ()->preScoring.isInformationCorrect(invalidRequest));
    }
}