package ru.neoflex.conveyor.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.neoflex.conveyor.dto.request.ScoringDataDTO;
import ru.neoflex.conveyor.enums.EmploymentStatus;
import ru.neoflex.conveyor.enums.Gender;
import ru.neoflex.conveyor.enums.MaterialStatus;
import ru.neoflex.conveyor.enums.WorkPosition;
import ru.neoflex.conveyor.exception.InvalidDataException;
import ru.neoflex.conveyor.exception.InvalidField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Scoring {
    private static final int TOTAL_WORK_EXPERIENCE = 12;
    private static final int CURRENT_WORK_EXPERIENCE = 3;

    private BigDecimal currentCreditRate;
    private boolean isCreditDenied = false;
    private final List<InvalidField> invalidInformation = new ArrayList<>();

    /**
     * Скоринг данных и подсчет финальной ставки (если не отказано в кредите)
     *
     * @param scoringDataDTO информация о клиенте
     * @param creditRate     зафиксированная ставка
     * @return итоговая ставка
     */
    public BigDecimal calculateScoring(ScoringDataDTO scoringDataDTO, BigDecimal creditRate) {
        isCreditDenied = false;
        invalidInformation.clear();

        log.debug("Scoring Internal data: scoringDataDTO {}, creditRate{} ", scoringDataDTO, creditRate);

        currentCreditRate = creditRate;

        checkCreditAbility(scoringDataDTO);

        log.debug("Scoring all data is valid");

        return calculateCurrentRate(scoringDataDTO);
    }

    /**
     * Проверка возможности фактической выдачи кредита
     *
     * @param scoringDataDTO информация о клиенте
     * @throws InvalidDataException список по которому клиент не может получить кредит
     */
    private void checkCreditAbility(ScoringDataDTO scoringDataDTO) throws InvalidDataException {
        //Безработный → отказ
        checkEmploymentStatus(scoringDataDTO.employment().employmentStatus());

        //Сумма займа больше, чем 20 зарплат → отказ
        checkAmount(scoringDataDTO.amount(), scoringDataDTO.employment().salary());

        //Возраст менее 20 или более 60 лет → отказ
        checkAge(scoringDataDTO.birthdate());

        //Общий стаж менее 12 месяцев → отказ
        checkTotalWorkExperience(scoringDataDTO.employment().workExperienceTotal());

        //Текущий стаж менее 3 месяцев → отказ
        checkCurrentWorkExperience(scoringDataDTO.employment().workExperienceCurrent());

        if (isCreditDenied) {
            log.warn("Scorning credit denied due to this data: {}", invalidInformation);
            throw new InvalidDataException(invalidInformation);
        }

        log.debug("Scoring credit is not denied");
    }

    /**
     * Подсчет итоговой процентной ставки по кредиту
     *
     * @param scoringDataDTO информация о клиенте
     * @return итоговая ставка
     */
    private BigDecimal calculateCurrentRate(ScoringDataDTO scoringDataDTO) {
        currentCreditRate = currentCreditRate.add(checkDependentAmount(scoringDataDTO.dependentAmount()));
        currentCreditRate = currentCreditRate.add(checkWorkPosition(scoringDataDTO.employment().position()));
        currentCreditRate = currentCreditRate.add(checkEmploymentStatus(scoringDataDTO.employment().employmentStatus()));
        currentCreditRate = currentCreditRate.add(checkMaterialStatus(scoringDataDTO.maritalStatus()));
        currentCreditRate = currentCreditRate.add(checkGenderAndAge(scoringDataDTO.gender(), scoringDataDTO.birthdate()));

        return currentCreditRate;
    }

    /**
     * Добавление неверных полей в ошибку
     * <p>
     *
     * @param name    имя поля
     * @param message сообщение для клиента
     */
    private void addInvalidField(String name, String message) {
        InvalidField ex = new InvalidField(name, message);
        int endOfList = invalidInformation.size();
        invalidInformation.add(endOfList, ex);
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
     *
     * @param status статус рабочего
     * @return 1 и 3 - увеличение ставки
     */
    private BigDecimal checkEmploymentStatus(EmploymentStatus status) {
        BigDecimal rate = BigDecimal.valueOf(0);

        switch (status) {
            case UNEMPLOYED -> {
                addInvalidField("Employment", "Status cannot be unemployed for a credit!");
                isCreditDenied = true;
            }
            case SELF_EMPLOYED -> rate = BigDecimal.valueOf(1);
            case BUSINESS_OWNER -> rate = BigDecimal.valueOf(3);
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
     *
     * @param position позиция на работе
     * @return -2 и -4 уменьшение ставки
     */
    private BigDecimal checkWorkPosition(WorkPosition position) {
        BigDecimal rate = BigDecimal.valueOf(0);

        switch (position) {
            case MIDDLE_MANAGER -> rate = BigDecimal.valueOf(-2);
            case TOP_MANAGER -> rate = BigDecimal.valueOf(-4);
        }
        return rate;
    }

    /**
     * Сумма займа больше, чем 20 зарплат → отказ
     * <p>
     *
     * @param amount сумма займа
     * @param salary зарплата
     */
    private void checkAmount(BigDecimal amount, BigDecimal salary) {
        if (amount.compareTo(salary.multiply(new BigDecimal(20))) > 0) {
            addInvalidField("Amount and salary", "Amount cannot be more than 20 salaries");
            isCreditDenied = true;
        }
    }

    /**
     * Семейное положение:
     * <p>
     * Замужем/женат → ставка уменьшается на 3;
     * <p>
     * Разведен → ставка увеличивается на 1
     * <p>
     *
     * @param status семейное положение
     * @return -3 уменьшение ставки, +1 увеличение ставки
     */
    private BigDecimal checkMaterialStatus(MaterialStatus status) {
        BigDecimal rate = BigDecimal.valueOf(0);

        switch (status) {
            case MARRIED -> rate = BigDecimal.valueOf(-3);
            case DIVORCED -> rate = BigDecimal.valueOf(+1);
            case SINGLE -> rate = BigDecimal.valueOf(0);
        }

        return rate;
    }

    /**
     * Количество иждивенцев:
     * <p>
     * Больше 1 → ставка увеличивается на 1
     * <p>
     *
     * @param dependentAmount количество иждивенцев
     * @return 1 если больше одного, 0 если меньше или равно
     */
    private BigDecimal checkDependentAmount(Integer dependentAmount) {
        return dependentAmount > 1 ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0);
    }

    /**
     * Возраст менее 20 или более 60 лет → отказ
     * <p>
     *
     * @param birthday дата рождения
     */
    private void checkAge(LocalDate birthday) {
        int age = calculateAge(birthday);

        if (age < 20 || age > 60) {
            addInvalidField("Age", "Age cannot be less than 20 and more than 60");
            isCreditDenied = true;
        }
    }

    /**
     * Пол: <p>
     * Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3; <p>
     * Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3; <p>
     * Не бинарный → ставка увеличивается на 3 <p>
     *
     * @param gender   пол человека
     * @param birthday дата рождения
     * @return -3 уменьшение ставки при соблюдении условий мужчины и женщины <p>
     * +3 - не бинарная личность
     */
    private BigDecimal checkGenderAndAge(Gender gender, LocalDate birthday) {
        BigDecimal rate = BigDecimal.valueOf(0);
        int age = calculateAge(birthday);

        switch (gender) {
            case MALE -> {
                if (age >= 35 && age <= 55)
                    rate = BigDecimal.valueOf(-3);
            }
            case FEMALE -> {
                if (age >= 35 && age <= 60)
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
     *
     * @param totalWorkExperience общий опыт работы
     */
    private void checkTotalWorkExperience(int totalWorkExperience) {
        if (totalWorkExperience < TOTAL_WORK_EXPERIENCE) {
            addInvalidField("Total work experience", "Total work experience cannot be less than 12 months");
            isCreditDenied = true;
        }
    }

    /**
     * Стаж работы: <p>
     * Текущий стаж менее 3 месяцев → отказ
     * <p>
     *
     * @param currentWorkExperience текущий опыт работы
     */
    private void checkCurrentWorkExperience(int currentWorkExperience) {
        if (currentWorkExperience < CURRENT_WORK_EXPERIENCE) {
            addInvalidField("Current work experience", "Current work experience cannot be less than 3 months");
            isCreditDenied = true;
        }

    }

    /**
     * Подсчет возраста из даты
     * <p>
     *
     * @param birthdate день рождения
     * @return возраст
     */
    public int calculateAge(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthdate, currentDate).getYears();
    }

}
