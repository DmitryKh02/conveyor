package ru.Neoflex.conveyor.Utils;

import org.springframework.stereotype.Component;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class PreScoring {
    private static final String PATTERN_NAME = "^[A-Za-z]{2,30}$";
    private static final String PATTERN_EMAIL = "[\\w\\.]{2,50}@[\\w\\.]{2,20}";
    private static final String PATTERN_SERIES = "\\d{4}";
    private static final String PATTERN_NUMBER = "\\d{6}";
    private static final BigDecimal MINIMAL_AMOUNT = BigDecimal.valueOf(10000);
    private static final int MINIMAL_TERM = 6;
    public static boolean isInformationCorrect(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        //TODO написать логгер и обработчик ошибок
        boolean isFirstNameValid = isValidName(loanApplicationRequestDTO.firstName());
        boolean isLastNameValid = isValidName(loanApplicationRequestDTO.lastName());
        boolean isMiddleNameValid = isValidName(loanApplicationRequestDTO.middleName());
        boolean isSumValid = isValidSum(loanApplicationRequestDTO.amount());
        boolean isTermValid = isValidTerm(loanApplicationRequestDTO.term());
        boolean isBirthdayValid = isValidBirthday(loanApplicationRequestDTO.birthdate());
        boolean isEmailValid = isValidEmail(loanApplicationRequestDTO.email());
        boolean isPassportSeriesValid = isValidPassportSeries(loanApplicationRequestDTO.passportSeries());
        boolean isPassportNumberValid = isValidPassportNumber(loanApplicationRequestDTO.passportNumber());

        return isFirstNameValid && isLastNameValid && isMiddleNameValid
                && isSumValid && isTermValid && isBirthdayValid
                && isEmailValid && isPassportSeriesValid && isPassportNumberValid;
    }

    /**
     * Имя, Фамилия - от 2 до 30 латинских букв.<p>
     * Отчество, при наличии - от 2 до 30 латинских букв.
     * <p>
     * @param name любое имя
     * @return true - подходит под паттерн, false - нет
     */
    private static boolean isValidName(String name) {
        return name.matches(PATTERN_NAME);
    }

    /**
     * Сумма кредита - действительно число, большее или равное 10000.
     * <p>
     * @param sum сумма кредита
     * @return  true - подходит под условие, false - нет
     */
    private static boolean isValidSum(BigDecimal sum){
        return sum.compareTo(MINIMAL_AMOUNT) >= 0;
    }

    /**
     * Срок кредита - целое число, большее или равное 6.
     * <p>
     * @param term количество месяцев платежа
     * @return true - подходит под условие, false - нет
     */
    private static boolean isValidTerm(int term){
        return term >= MINIMAL_TERM;
    }

    /**
     * Дата рождения - число в формате гггг-мм-дд, не позднее 18 лет с текущего дня.
     * <p>
     * @param birthday дата рождения
     * @return true - подходит под условие, false - нет
     */
    public static boolean isValidBirthday(LocalDate birthday){
        return birthday.isBefore(LocalDate.now().minusYears(18));
    }

    /**
     * Email адрес - строка, подходящая под паттерн [\w\.]{2,50}@[\w\.]{2,20}
     * <p>
     * @param email эллектронная почта пользователя
     * @return true - подходит под условие, false - нет
     */
    private static boolean isValidEmail(String email) {
        return email.matches(PATTERN_EMAIL);
    }

    /**
     * Серия паспорта - 4 цифры
     * <p>
     * @param series серия паспорта
     * @return true - подходит под условие, false - нет
     */
    private static boolean isValidPassportSeries(String series) {
       return series.matches(PATTERN_SERIES);
    }

    /**
     * Номер паспорта - 6 цифр
     * <p>
     * @param number номер паспорта
     * @return true - подходит под условие, false - нет
     */
    private static boolean isValidPassportNumber(String number) {
        return number.matches(PATTERN_NUMBER);
    }

}
