package ru.Neoflex.conveyor.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.Neoflex.conveyor.DTO.ScoringDataDTO;
import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.WorkPosition;

import java.math.BigDecimal;

@Component
public class Scoring {
    @Value("${credit.rate}")
    private static int CREDIT_RATE;

    public static int scoring(ScoringDataDTO scoringDataDTO){
        return CREDIT_RATE;
    }

    /**
     * Рабочий статус:
     * <p>
     * Безработный → отказ;
     * <p>
     * Самозанятый → ставка увеличивается на 1;
     * <p>
     * Владелец бизнеса → ставка увеличивается на 3
     * <p>
     * @param status статус рабочего
     * @return 0 - отказ, 1 и 3 - увелечение ставки
     */
    int checkEmploymentStatus(EmploymentStatus status){
        int rate = 0;
        switch (status){
            case SELF_EMPLOYED -> rate = 1;
            case BUSINESS_OWNER -> rate = 3;
        }
        return rate;
    }

    /**
     * Позиция на работе:
     * <p>
     * Менеджер среднего звена → ставка уменьшается на 2
     * <p>
     * Топ-менеджер → ставка уменьшается на 4
     * <p>
     * @param position позиция на работе
     * @return -2 и -4 уменьшение ставки
     */
    int checkWorkPosition(WorkPosition position){
        int rate = 0;
        switch (position){
            case MIDDLE_MANAGER -> rate = -2;
            case TOP_MANAGER -> rate = -4;
        }
        return rate;
    }

    boolean checkAmount(BigDecimal amount, BigDecimal salary){
        return amount.compareTo(salary.multiply(new BigDecimal(20))) <= 0;
    }
}
