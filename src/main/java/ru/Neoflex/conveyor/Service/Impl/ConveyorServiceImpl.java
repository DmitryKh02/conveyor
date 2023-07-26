package ru.Neoflex.conveyor.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.Neoflex.conveyor.DTO.Responce.CreditDTO;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.Responce.LoanOfferDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.Service.ConveyorService;
import ru.Neoflex.conveyor.Utils.PreScoring;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConveyorServiceImpl implements ConveyorService {
    @Value("${credit.insurance}")
    private BigDecimal INSURANCE;

    @Value("${credit.rate}")
    private BigDecimal CREDIT_RATE;

    @Override
    public List<LoanOfferDTO> calculationPossibleCreditConditions(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        boolean isValidBirthday = PreScoring.isValidBirthday(loanApplicationRequestDTO.birthdate());

        BigDecimal fullCreditInsurance = calculateFullCreditInsurance(loanApplicationRequestDTO.amount());
        BigDecimal amount = loanApplicationRequestDTO.amount();
        Integer term = loanApplicationRequestDTO.term();

        List<LoanOfferDTO> responceList = new ArrayList<>();

        boolean[] insuranceOptions = {true, true, false, false};
        boolean[] salaryOptions = {true, false, true, false};

        for (int i = 0; i < insuranceOptions.length; i++) {
            responceList.add(getLoanOfferDTO(
                    (long) i,
                    amount,
                    term,
                    fullCreditInsurance,
                    insuranceOptions[i],
                    salaryOptions[i]
            ));
        }

        return responceList;
    }

    /**
     * Создание записи предложения по кредиту на основании включения страхования и зарплатного клиента
     * <p>
     * @param id номер предложения
     * @param amount сумма запрашиваемого кредита
     * @param term количество месяцев для погашения кредита
     * @param fullCreditInsurance полная стоимость страховки кредита
     * @param isInsuranceEnabled включена ли страховка
     * @param isSalaryClient зарплатный ли клиент
     * @return запись предложения по кредиту
     */
    private LoanOfferDTO getLoanOfferDTO(Long id,
                                                BigDecimal amount,
                                                Integer term,
                                                BigDecimal fullCreditInsurance,
                                                boolean isInsuranceEnabled,
                                                boolean isSalaryClient){
        BigDecimal totalAmount = amount;
        BigDecimal monthlyPayment;
        BigDecimal finalRate = CREDIT_RATE;

        if (isInsuranceEnabled) {
            totalAmount = amount.add(fullCreditInsurance);
            finalRate = finalRate.subtract(BigDecimal.valueOf(3));
        }

        if (isSalaryClient) {
            finalRate = finalRate.subtract(BigDecimal.valueOf(1));
        }

        monthlyPayment = calculateAnnuityPayment(totalAmount, term, finalRate);

        return new LoanOfferDTO(id,amount,totalAmount,term,monthlyPayment,finalRate,isInsuranceEnabled,isSalaryClient);
    }

    /**
     *
     Для расчета аннуитетного платежа можно использовать следующую формулу: <p>

     r * (1+r)^n <p>
     -------------* P = A<p>
     (1 + r)^n - 1<p>

     где:<p>
     A - аннуитетный платеж, <p>
     P - сумма кредита, <p>
     r - месячная процентная ставка (должна быть в долях, не в процентах), <p>
     n - количество месяцев.<p>
     * @param loanAmount сумма кредита
     * @param term количество месяцев для погашения кредита
     * @param creditRate ставка по кредиту
     * @return ежемесячный аннуитетный платеж
     */
    private BigDecimal calculateAnnuityPayment(BigDecimal loanAmount, int term, BigDecimal creditRate) {
        // Преобразуем процентную ставку в долю (например, 5% -> 0.05)
        BigDecimal monthlyInterestRate = creditRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Расчет аннуитетного коэффициента
        BigDecimal annuityCoefficient = monthlyInterestRate.multiply(
                        (BigDecimal.ONE.add(monthlyInterestRate)).pow(term))
                .divide((BigDecimal.ONE.add(monthlyInterestRate)).pow(term).subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        // Расчет аннуитетного платежа
        return loanAmount.multiply(annuityCoefficient).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Рассчет стоимости для страховки <p>
     * P * r = A
     * где: <p>
     * A - конечная сумма страховки <p>
     * P - сумма кредита, <p>
     * r - ставка страхования  (должна быть в долях, не в процентах), <p>
     * @param amount сумма кредита
     * @return итоговая стоимость страховки
     */
    private BigDecimal calculateFullCreditInsurance(BigDecimal amount){
        return INSURANCE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(amount);
    }


    @Override
    public CreditDTO calculationCreditParameters(ScoringDataDTO scoringDataDTO) {
        return null;
    }
}
