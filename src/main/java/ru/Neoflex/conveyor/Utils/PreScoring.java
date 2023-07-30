package ru.Neoflex.conveyor.Utils;

import org.springframework.stereotype.Component;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.Exception.InvalidDataException;
import ru.Neoflex.conveyor.Exception.InvalidField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Component
public class PreScoring {
    private static final List<InvalidField> INVALID_INFORMATION = new LinkedList<>();
    private static final String PATTERN_NAME = "^[A-Za-z]{2,30}$";
    private static final String PATTERN_EMAIL = "[\\w\\.]{2,50}@[\\w\\.]{2,20}";
    private static final String PATTERN_SERIES = "\\d{4}";
    private static final String PATTERN_NUMBER = "\\d{6}";
    private static final BigDecimal MINIMAL_AMOUNT = BigDecimal.valueOf(10000);
    private static final int MINIMAL_TERM = 6;

    /**
     * Прескоринг данных для дальнейшей выдачи кредита
     * <p>
     * @param loanApplicationRequestDTO основная информация о клиенте
     * @throws InvalidDataException ошибка полученных данных
     */
    public static void isInformationCorrect(LoanApplicationRequestDTO loanApplicationRequestDTO) throws InvalidDataException {
        INVALID_INFORMATION.clear();

        checkInvalidInformation(isValidSum(loanApplicationRequestDTO.amount()), "Amount", "Amount must be greater than or equal to 10000");
        checkInvalidInformation(isValidTerm(loanApplicationRequestDTO.term()), "Term", "Term must be greater than or equal to 6");

        checkInvalidInformation(isValidName(loanApplicationRequestDTO.firstName()), "First Name", "Invalid first name format");
        checkInvalidInformation(isValidName(loanApplicationRequestDTO.lastName()), "Last Name", "Invalid last name format");
        checkInvalidInformation(isValidName(loanApplicationRequestDTO.middleName()), "Middle Name", "Invalid middle name format");

        checkInvalidInformation(isValidEmail(loanApplicationRequestDTO.email()), "Email", "Invalid email format");
        checkInvalidInformation(isValidBirthday(loanApplicationRequestDTO.birthdate()), "Birthdate", "Birthdate must be a past date and more then 18 years old");

        checkInvalidInformation(isValidPassportSeries(loanApplicationRequestDTO.passportSeries()), "Passport Series", "Invalid passport series format");
        checkInvalidInformation(isValidPassportNumber(loanApplicationRequestDTO.passportNumber()), "Passport Number", "Invalid passport number format");

        if(!INVALID_INFORMATION.isEmpty()) throw new InvalidDataException(INVALID_INFORMATION);
    }

    /**
     * Добавление неверных полей в ошибку
     * <p>
     * @param isValid прошло ли проверку поле
     * @param fieldName имя поля
     * @param message сообщение для клиента
     */
    private static void checkInvalidInformation(boolean isValid, String fieldName, String message){
        if(!isValid){
            InvalidField ex = new InvalidField(fieldName, message);
            int endOfList = INVALID_INFORMATION.size();
            INVALID_INFORMATION.add(endOfList, ex);
        }
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
        return Scoring.calculateAge(birthday) > 18;
    }

    /**
     * Email адрес - строка, подходящая под паттерн [\w\.]{2,50}@[\w\.]{2,20}
     * <p>
     * @param email электронная почта пользователя
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
