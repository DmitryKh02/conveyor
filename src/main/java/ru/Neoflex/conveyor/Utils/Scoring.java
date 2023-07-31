package ru.Neoflex.conveyor.Utils;

import org.springframework.stereotype.Component;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.Enum.EmploymentStatus;
import ru.Neoflex.conveyor.Enum.Gender;
import ru.Neoflex.conveyor.Enum.MaterialStatus;
import ru.Neoflex.conveyor.Enum.WorkPosition;
import ru.Neoflex.conveyor.Exception.InvalidDataException;
import ru.Neoflex.conveyor.Exception.InvalidField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.List;

@Component
public class Scoring {
    private static final int TOTAL_WORK_EXPERIENCE = 12;
    private static final int CURRENT_WORK_EXPERIENCE = 3;
    private static final List<InvalidField> INVALID_INFORMATION = new LinkedList<>();
    private static BigDecimal currentCreditRate;
    private static boolean IS_CREDIT_DENIED = false;

    /**
     * Скоринг данных и подсчет финальной ставки (если не отказано в кредите)
     * @param scoringDataDTO информация о клиенте
     * @param creditRate зафиксированная ставка
     * @return итоговая ставка
     */
    public static BigDecimal calculateScoring(ScoringDataDTO scoringDataDTO, BigDecimal creditRate){
        IS_CREDIT_DENIED = false;
        INVALID_INFORMATION.clear();

        currentCreditRate = creditRate;

        checkCreditAbility(scoringDataDTO);

        return calculateCurrentRate(scoringDataDTO);
    }

    /**
     * Проверка возможности фактической выдачи кредита
     * @param scoringDataDTO информация о клиенте
     * @throws InvalidDataException список по которому клиент не может получить кредит
     */
    private static void checkCreditAbility(ScoringDataDTO scoringDataDTO) throws InvalidDataException {
        //Безработный → отказ
        checkEmploymentStatus(scoringDataDTO.employment().employmentStatus());

        //Сумма займа больше, чем 20 зарплат → отказ
        checkAmount(scoringDataDTO.amount(),scoringDataDTO.employment().salary());

        //Возраст менее 20 или более 60 лет → отказ
        checkAge(scoringDataDTO.birthdate());

        //Общий стаж менее 12 месяцев → отказ
        checkTotalWorkExperience(scoringDataDTO.employment().workExperienceTotal());

        //Текущий стаж менее 3 месяцев → отказ
        checkCurrentWorkExperience(scoringDataDTO.employment().workExperienceCurrent());

        if(IS_CREDIT_DENIED) throw new InvalidDataException(INVALID_INFORMATION);
    }

    /**
     * Подсчет итоговой процентной ставки по кредиту
     * @param scoringDataDTO информация о клиенте
     * @return итоговая ставка
     */
    private static BigDecimal calculateCurrentRate(ScoringDataDTO scoringDataDTO){
        currentCreditRate = currentCreditRate.add(checkDependentAmount(scoringDataDTO.dependentAmount()));
        currentCreditRate = currentCreditRate.add(checkWorkPosition(scoringDataDTO.employment().position()));
        currentCreditRate = currentCreditRate.add(checkEmploymentStatus(scoringDataDTO.employment().employmentStatus()));
        currentCreditRate = currentCreditRate.add(checkMaterialStatus(scoringDataDTO.maritalStatus()));
        currentCreditRate = currentCreditRate.add(checkGenderAndAge(scoringDataDTO.gender(),scoringDataDTO.birthdate()));

        return currentCreditRate;
    }

    /**
     * Добавление неверных полей в ошибку
     * <p>
     * @param name имя поля
     * @param message сообщение для клиента
     */
    private static void addInvalidField(String name, String message){
        InvalidField ex = new InvalidField(name, message);
        int endOfList = INVALID_INFORMATION.size();
        INVALID_INFORMATION.add(endOfList, ex);
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
     * @return 1 и 3 - увеличение ставки
     */
    private static BigDecimal checkEmploymentStatus(EmploymentStatus status){
        BigDecimal rate = BigDecimal.valueOf(0);

        switch (status){
            case UNEMPLOYED -> {
                addInvalidField("Employment", "Status cannot be unemployed for a credit!");
                IS_CREDIT_DENIED = true;
            }
            case SELF_EMPLOYED -> rate = BigDecimal.valueOf(1);
            case BUSINESS_OWNER -> rate = BigDecimal.valueOf(3);
            default -> addInvalidField("Employment status", "Please, set one of this variant: UNEMPLOYED, SELF_EMPLOYED, BUSINESS_OWNER");
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
    private static BigDecimal checkWorkPosition(WorkPosition position){
        BigDecimal rate = BigDecimal.valueOf(0);

        switch (position){
            case MIDDLE_MANAGER -> rate = BigDecimal.valueOf(-2);
            case TOP_MANAGER -> rate = BigDecimal.valueOf(-4);
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
       if(amount.compareTo(salary.multiply(new BigDecimal(20))) > 0) {
           addInvalidField("Amount and salary", "Amount cannot be more than 20 salaries");
           IS_CREDIT_DENIED = true;
       }
    }

    /**
     * Семейное положение:
     * <p>
     * Замужем/женат → ставка уменьшается на 3;
     * <p>
     * Разведен → ставка увеличивается на 1
     * <p>
     * @param status семейное положение
     * @return -3 уменьшение ставки, +1 увеличение ставки
     */
    private static BigDecimal checkMaterialStatus(MaterialStatus status){
        BigDecimal rate = BigDecimal.valueOf(0);

        switch (status){
            case MARRIED -> rate = BigDecimal.valueOf(-3);
            case DIVORCED -> rate = BigDecimal.valueOf(+1);
        }

        return rate;
    }

    /**
     * Количество иждивенцев:
     * <p>
     * Больше 1 → ставка увеличивается на 1
     * <p>
     * @param dependentAmount количество иждивенцев
     * @return 1 если больше одного, 0 если меньше или равно
     */
    private static BigDecimal checkDependentAmount(Integer dependentAmount){
        return dependentAmount > 1 ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0);
    }

    /**
     * Возраст менее 20 или более 60 лет → отказ
     * <p>
     * @param birthday дата рождения
     */
    private static void checkAge(LocalDate birthday){
        int age = calculateAge(birthday);
        if(age < 20 || age > 60) {
            addInvalidField("Age", "Age cannot be less than 20 and more than 60");
            IS_CREDIT_DENIED = true;
        }
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
    private static BigDecimal checkGenderAndAge(Gender gender, LocalDate birthday){
        BigDecimal rate = BigDecimal.valueOf(0);
        int age = calculateAge(birthday);

        switch (gender){
            case MALE -> {
                if(age >= 35 && age <= 55)
                    rate = BigDecimal.valueOf(-3);
            }
            case FEMALE -> {
                if(age >= 35 && age <= 60)
                    rate = BigDecimal.valueOf(-3);
            }
            case NOT_BINARY -> rate = BigDecimal.valueOf(3);
        }
        return rate;
    }

    /**
     * Стаж работы:<p>
     * Общий стаж менее 12 месяцев → отказ
     * <p>
     * @param totalWorkExperience общий опыт работы
     */
    private static void checkTotalWorkExperience(int totalWorkExperience){
        if(totalWorkExperience < TOTAL_WORK_EXPERIENCE) {
            addInvalidField("Total work experience", "Total work experience cannot be less than 12 months");
            IS_CREDIT_DENIED = true;
        }
    }

    /**
     * Стаж работы: <p>
     * Текущий стаж менее 3 месяцев → отказ
     * <p>
     * @param currentWorkExperience текущий опыт работы
     */
    private static void checkCurrentWorkExperience(int currentWorkExperience){
        if(currentWorkExperience < CURRENT_WORK_EXPERIENCE){
            addInvalidField("Current work experience", "Current work experience cannot be less than 3 months");
            IS_CREDIT_DENIED = true;
        }

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

}
