package ru.Neoflex.conveyor.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.Neoflex.conveyor.DTO.Responce.CreditDTO;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.Responce.LoanOfferDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.DTO.Responce.PaymentScheduleElement;
import ru.Neoflex.conveyor.Service.ConveyorService;
import ru.Neoflex.conveyor.Utils.PreScoring;
import ru.Neoflex.conveyor.Utils.Scoring;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ConveyorServiceImpl implements ConveyorService {
    @Value("${credit.insurance}")
    private BigDecimal INSURANCE;

    @Value("${credit.rate}")
    private BigDecimal CREDIT_RATE;

    private BigDecimal currentAmount;
    private Integer currentTerm;
    private BigDecimal currentRate;

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setCurrentTerm(Integer currentTerm) {
        this.currentTerm = currentTerm;
    }

    public void setCurrentRate(BigDecimal currentRate) {
        this.currentRate = currentRate;
    }

    @Override
    public List<LoanOfferDTO> calculationPossibleCreditConditions(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        PreScoring.isInformationCorrect(loanApplicationRequestDTO);

        setCurrentAmount(loanApplicationRequestDTO.amount());
        setCurrentTerm(loanApplicationRequestDTO.term());

        BigDecimal fullCreditInsurance = calculateFullCreditInsurance();

        List<LoanOfferDTO> responceList = new ArrayList<>();

        boolean[] insuranceOptions = {true, true, false, false};
        boolean[] salaryOptions = {true, false, true, false};

        for (int i = 0; i < insuranceOptions.length; i++) {
            responceList.add(getLoanOfferDTO(
                    (long) i,
                    fullCreditInsurance,
                    insuranceOptions[i],
                    salaryOptions[i]
            ));
        }

        return responceList;
    }

    @Override
    public CreditDTO calculationCreditParameters(ScoringDataDTO scoringDataDTO) {

        setCurrentAmount(scoringDataDTO.amount());
        setCurrentTerm(scoringDataDTO.term());
        setCurrentRate(Scoring.calculateScoring(scoringDataDTO, CREDIT_RATE));

        BigDecimal monthlyPayment = calculateAnnuityPayment(currentRate);
        BigDecimal psk = calculatePSK();


        return new CreditDTO(
                currentAmount,
                currentTerm,
                monthlyPayment,
                currentRate,
                psk,
                scoringDataDTO.isInsuranceEnabled(),
                scoringDataDTO.isSalaryClient(),
                calculatePaymentSchedule(monthlyPayment));
    }

    /**
     * Создание записи предложения по кредиту на основании включения страхования и зарплатного клиента
     * <p>
     * @param id номер предложения
     * @param fullCreditInsurance полная стоимость страховки кредита
     * @param isInsuranceEnabled включена ли страховка
     * @param isSalaryClient зарплатный ли клиент
     * @return запись предложения по кредиту
     */
    private LoanOfferDTO getLoanOfferDTO(Long id,
                                         BigDecimal fullCreditInsurance,
                                         boolean isInsuranceEnabled,
                                         boolean isSalaryClient){
        BigDecimal totalAmount = currentAmount;
        BigDecimal monthlyPayment;
        BigDecimal finalRate = CREDIT_RATE;

        if (isInsuranceEnabled) {
            totalAmount = currentAmount.add(fullCreditInsurance);
            finalRate = finalRate.subtract(BigDecimal.valueOf(3));
        }

        if (isSalaryClient) {
            finalRate = finalRate.subtract(BigDecimal.valueOf(1));
        }

        monthlyPayment = calculateAnnuityPayment(finalRate);

        return new LoanOfferDTO(
                id,
                currentAmount,
                totalAmount,
                currentTerm,
                monthlyPayment,
                finalRate,
                isInsuranceEnabled,
                isSalaryClient);
    }

    /**
     Для расчета аннуитетного платежа можно использовать следующую формулу: <p>

     r * (1+r)^n <p>
     -------------* P = A<p>
     (1 + r)^n - 1<p>

     где:<p>
     A - аннуитетный платеж, <p>
     P - сумма кредита, <p>
     r - месячная процентная ставка (должна быть в долях, не в процентах), <p>
     n - количество месяцев.
     <p>
     * @param creditRate ставка по кредиту
     * @return ежемесячный аннуитетный платеж
     */
    private BigDecimal calculateAnnuityPayment(BigDecimal creditRate) {
        // Преобразуем процентную ставку в долю
        double r = creditRate.doubleValue()/100/12;
        // Расчет аннуитетного коэффициента
        double annuityCoefficient = r * Math.pow(1 + r, currentTerm) / (Math.pow(1 + r, currentTerm) - 1);

        // Расчет аннуитетного платежа
        return currentAmount.multiply(BigDecimal.valueOf(annuityCoefficient)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Расчет стоимости для страховки <p>
     * P * r = A
     * где: <p>
     * A - конечная сумма страховки <p>
     * P - сумма кредита, <p>
     * r - ставка страхования (должна быть в долях, не в процентах)
     * <p>
     * @return итоговая стоимость страховки
     */
    private BigDecimal calculateFullCreditInsurance(){
        return INSURANCE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(currentAmount);
    }

    /**
     * Рассчет полной стоимости кредита по упрощенной формуле:
     * <p>
     * F/A - 1 <p>
     * -------- = PSK <p>
     * N <p>
     * где: <p>
     * PSK - полная стоимость кредита в процентах годовых <p>
     * F - сумма всех выплат по кредиту (включая страховку) <p>
     * A - сумма выданного кредита <p>
     * N - количество лет выплаты кредита
     * <p>
     * @return полная стоимость кредита
     */
    private BigDecimal calculatePSK(){
        BigDecimal fullCreditInsurance = calculateFullCreditInsurance();
        BigDecimal totalLoanCost = calculateTotalLoanCost(fullCreditInsurance);
        BigDecimal creditYears = BigDecimal.valueOf(currentTerm).divide(BigDecimal.valueOf(12), RoundingMode.HALF_DOWN);

        return totalLoanCost
                .divide(currentAmount, RoundingMode.HALF_DOWN)
                .subtract(BigDecimal.ONE)
                .divide(creditYears, RoundingMode.HALF_DOWN)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Рассчет всех выплат (с учетом страховки)
     * <p>
     * @param insuranceCost стоимость страховки для кредита
     * @return сумма всех выплат
     */
    public BigDecimal calculateTotalLoanCost(BigDecimal insuranceCost) {
        BigDecimal monthlyPayment = calculateAnnuityPayment(currentRate);
        return monthlyPayment.multiply(BigDecimal.valueOf(currentTerm)).add(insuranceCost);
    }

    /**
     * Рассчет графика ежемесячных платежей <p>
     * A - месячный платеж monthlyPayment - дано <p>
     * r - месячная процентная ставка - P/100/12 (1) <p>
     * B - погашение долга interestPayment - A*r (2) <p>
     * C - погашение процентов - A-B (3) <p>
     * D - остаток долга D-C (4)
     * <p>
     * @param monthlyPayment месячный платеж по кредиту
     * @return список платежей
     */
    public List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal monthlyPayment) {
        //Месячная процентная ставка (должна быть в долях, не в процентах)
        BigDecimal r;

        //погашение долга
        BigDecimal interestPayment;

        // погашение процентов
        BigDecimal debtPayment;

        // остаток долга
        BigDecimal remainingDebt = currentAmount;

        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        for (int i = 0; i < currentTerm; i++) {
            //Считаем месячную процентную ставку в долях (1)
            r = currentRate.divide(BigDecimal.valueOf(1200),8, RoundingMode.HALF_DOWN);

            //Считаем погашение процентов (2)
            debtPayment = remainingDebt.multiply(r).setScale(2, RoundingMode.HALF_DOWN);

            //Считаем погашение долга (3)
            interestPayment = monthlyPayment.subtract(debtPayment);

            //Считаем остаток долга
            remainingDebt = remainingDebt.subtract(interestPayment);

            // TODO какую дату платежа выставлять???
            LocalDate paymentDate = LocalDate.now().plusMonths(i);

            PaymentScheduleElement payment = new PaymentScheduleElement(
                    i + 1, // Номер платежа
                    paymentDate, // Дата платежа
                    monthlyPayment, // Общая сумма платежа
                    interestPayment, // Погашение долга
                    debtPayment, // Погашение процентов
                    remainingDebt // Оставшийся долг
            );
            paymentSchedule.add(payment);
        }

        return paymentSchedule;
    }



}
