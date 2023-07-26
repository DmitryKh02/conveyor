package ru.Neoflex.conveyor.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.Gender;
import ru.Neoflex.conveyor.Enum.WorkPosition;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Component
public class Scoring {
    @Value("${credit.rate}")
    private static BigDecimal CREDIT_RATE;
    private static final int TOTAL_WORK_EXPERIENCE = 12;
    private static final int CURRENT_WORK_EXPERIENCE = 3;
    private static boolean isCreditDenied = false;

    public static BigDecimal getScoring(ScoringDataDTO scoringDataDTO){
        return CREDIT_RATE;
    }

    public static boolean getCreditStatus() {
        return isCreditDenied;
    }

    private static void calculateScoring(){

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
     * @return 1 и 3 - увелечение ставки
     */
    private static int checkEmploymentStatus(EmploymentStatus status){
        int rate = 0;
        switch (status){
            case UNEMPLOYED -> isCreditDenied = true;
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
    private static int checkWorkPosition(WorkPosition position){
        int rate = 0;
        switch (position){
            case MIDDLE_MANAGER -> rate = -2;
            case TOP_MANAGER -> rate = -4;
        }
        return rate;
    }

    /**
     * Сумма займа больше, чем 20 зарплат → отказ
     * <p>
     * @param amount сумма займа
     * @param salary зарплата
     */
    private static void checkAmount(BigDecimal amount, BigDecimal salary){
        isCreditDenied = amount.compareTo(salary.multiply(new BigDecimal(20))) <= 0;
    }

    /**
     * Возраст менее 20 или более 60 лет → отказ
     * <p>
     * @param birthday дата рождения
     */
    private static void checkAge(LocalDate birthday){
        int age = calculateAge(birthday);
        if(age < 20 || age > 60)
            isCreditDenied = true;
    }

    /**
     * Пол: <p>
     * Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3; <p>
     * Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3; <p>
     * Не бинарный → ставка увеличивается на 3 <p>
     * @param gender пол человека
     * @param birthday дата рождения
     * @return -3 уменьшение ставки при соблюдении условий мужчины и женщины <p>
     * +3 - не бинарная личность
     */
    private static int checkGenderAndAge(Gender gender, LocalDate birthday){
        int rate = 0;
        int age = calculateAge(birthday);

        switch (gender){
            case MALE -> {
                if(age >= 35 && age <= 55)
                    rate = -3;
            }
            case FEMALE -> {
                if(age >= 35 && age <= 60)
                    rate = -3;
            }
            case NOT_BINARY -> rate = 3;
        }
        return rate;
    }

    /**
     * Подсчет возраста из даты
     * <p>
     * @param birthdate день рождения
     * @return возраст
     */
    public static int calculateAge(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthdate, currentDate).getYears();
    }

    /**
     * Стаж работы:<p>
     * Общий стаж менее 12 месяцев → отказ
     * <p>
     * @param totalWorkExperience общий опыт работы
     */
    private static void checkTotalWorkExperience(int totalWorkExperience){
        if(totalWorkExperience < TOTAL_WORK_EXPERIENCE)
            isCreditDenied = true;
    }

    /**
     * Стаж работы: <p>
     * Текущий стаж менее 3 месяцев → отказ
     * <p>
     * @param currentWorkExperience текущий опыт работы
     */
    private static void checkCurrentWorkExperience(int currentWorkExperience){
        if(currentWorkExperience < CURRENT_WORK_EXPERIENCE)
            isCreditDenied = true;
    }




}
