package ru.neoflex.conveyor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.conveyor.dto.response.CreditDTO;
import ru.neoflex.conveyor.dto.request.LoanApplicationRequestDTO;
import ru.neoflex.conveyor.dto.response.LoanOfferDTO;
import ru.neoflex.conveyor.dto.request.ScoringDataDTO;
import ru.neoflex.conveyor.dto.response.PaymentScheduleElement;
import ru.neoflex.conveyor.service.ConveyorService;
import ru.neoflex.conveyor.utils.PreScoring;
import ru.neoflex.conveyor.utils.Scoring;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConveyorServiceImpl implements ConveyorService {
    @Value("${credit.insurance}")
    private BigDecimal insurance;
    @Value("${credit.rate}")
    private BigDecimal globalCreditRate;

    private final PreScoring preScoring;
    private final Scoring scoring;

    @Override
    public List<LoanOfferDTO> calculationPossibleCreditConditions(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.trace("ConveyorServiceImpl.calculationPossibleCreditConditions - internal data: loanApplicationRequestDTO {}", loanApplicationRequestDTO);

        preScoring.isInformationCorrect(loanApplicationRequestDTO);

        BigDecimal fullCreditInsurance = calculateFullCreditInsurance(insurance, loanApplicationRequestDTO.amount());

        log.debug("ConveyorServiceImpl.calculationPossibleCreditConditions - full credit insurance: {} ", fullCreditInsurance);

        List<LoanOfferDTO> responseList = new ArrayList<>(4);

        boolean[] insuranceOptions = {true, true, false, false};
        boolean[] salaryOptions = {true, false, true, false};

        for (int i = 0; i < insuranceOptions.length; i++) {
            responseList.add(getLoanOfferDTO(
                    (long) i,
                    loanApplicationRequestDTO.amount(),
                    loanApplicationRequestDTO.term(),
                    fullCreditInsurance,
                    insuranceOptions[i],
                    salaryOptions[i]
            ));
        }

        log.trace("ConveyorServiceImpl.calculationPossibleCreditConditions - list of loanOfferDTO: {}", responseList);

        return responseList;
    }

    @Override
    public CreditDTO calculationCreditParameters(ScoringDataDTO scoringDataDTO) {
        log.debug("ConveyorServiceImpl.calculationCreditParameters - internal data: scoringDataDTO {}", scoringDataDTO);

        BigDecimal currentAmount = scoringDataDTO.amount();
        Integer currentTerm = scoringDataDTO.term();
        BigDecimal currentRate = scoring.calculateScoring(scoringDataDTO, globalCreditRate);

        BigDecimal monthlyPayment = calculateAnnuityPayment(currentRate, currentAmount, currentTerm);
        BigDecimal psk = calculatePSK(insurance, currentAmount, currentRate, currentTerm);

        CreditDTO creditDTO = new CreditDTO(
                currentAmount,
                currentTerm,
                monthlyPayment,
                currentRate,
                psk,
                scoringDataDTO.isInsuranceEnabled(),
                scoringDataDTO.isSalaryClient(),
                calculatePaymentSchedule(monthlyPayment, currentAmount, currentRate, currentTerm));

        log.debug("ConveyorServiceImpl.calculationCreditParameters - creditDTO: {}", creditDTO);

        return creditDTO;
    }


    /**
     * Создание записи предложения по кредиту на основании включения страхования и зарплатного клиента
     * <p>
     *
     * @param id                  номер предложения
     * @param currentAmount       стоимость кредита
     * @param currentTerm         длительность кредита
     * @param fullCreditInsurance полная стоимость страховки кредита
     * @param isInsuranceEnabled  включена ли страховка
     * @param isSalaryClient      зарплатный ли клиент
     * @return запись предложения по кредиту
     */
    protected LoanOfferDTO getLoanOfferDTO(Long id,
                                           BigDecimal currentAmount,
                                           Integer currentTerm,
                                           BigDecimal fullCreditInsurance,
                                           boolean isInsuranceEnabled,
                                           boolean isSalaryClient) {

        log.debug("ConveyorServiceImpl.getLoanOfferDTO - internal data: id {}, currentAmount {}, currentTerm {}, fullCreditInsurance {}, isInsuranceEnabled {}, isSalaryClient{}",
                id,
                currentAmount,
                currentTerm,
                fullCreditInsurance,
                isInsuranceEnabled,
                isSalaryClient);

        BigDecimal totalAmount = currentAmount;
        BigDecimal monthlyPayment;
        BigDecimal finalRate = globalCreditRate;

        if (isInsuranceEnabled) {
            totalAmount = currentAmount.add(fullCreditInsurance);
            finalRate = finalRate.subtract(BigDecimal.valueOf(3));
        }

        if (isSalaryClient) {
            finalRate = finalRate.subtract(BigDecimal.valueOf(1));
        }

        monthlyPayment = calculateAnnuityPayment(finalRate, currentAmount, currentTerm);

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO(
                id,
                currentAmount,
                totalAmount,
                currentTerm,
                monthlyPayment,
                finalRate,
                isInsuranceEnabled,
                isSalaryClient);

        log.debug("ConveyorServiceImpl.getLoanOfferDTO - loanOfferDTO: {}", loanOfferDTO);

        return loanOfferDTO;
    }

    /**
     * Для расчета аннуитетного платежа можно использовать следующую формулу: <p>
     * <p>
     * r * (1+r)^n <p>
     * -------------* P = A<p>
     * (1 + r)^n - 1<p>
     * <p>
     * где:<p>
     * A - аннуитетный платеж, <p>
     * P - сумма кредита, <p>
     * r - месячная процентная ставка (должна быть в долях, не в процентах), <p>
     * n - количество месяцев.
     * <p>
     *
     * @param creditRate    ставка по кредиту
     * @param currentAmount стоимость кредита
     * @param currentTerm   длительность кредита
     * @return ежемесячный аннуитетный платеж
     */
    protected BigDecimal calculateAnnuityPayment(BigDecimal creditRate, BigDecimal currentAmount, Integer currentTerm) {
        log.debug("ConveyorServiceImpl.calculateAnnuityPayment - internal data: creditRate {}, currentAmount {}, currentTerm {}, ", creditRate, currentAmount, currentTerm);

        // Преобразуем процентную ставку в долю
        double rate = creditRate.doubleValue() / 100 / 12;
        // Расчет аннуитетного коэффициента
        double annuityCoefficient = rate * Math.pow(1 + rate, currentTerm) / (Math.pow(1 + rate, currentTerm) - 1);

        // Расчет аннуитетного платежа
        BigDecimal annuityPayment = currentAmount.multiply(BigDecimal.valueOf(annuityCoefficient)).setScale(2, RoundingMode.HALF_UP);

        log.debug("ConveyorServiceImpl.calculateAnnuityPayment - annuity payment: {}", annuityPayment);

        return annuityPayment;
    }

    /**
     * Расчет стоимости для страховки <p>
     * P * r = A
     * где: <p>
     * A - конечная сумма страховки <p>
     * P - сумма кредита, <p>
     * r - ставка страхования (должна быть в долях, не в процентах)
     * <p>
     *
     * @return итоговая стоимость страховки
     */
    protected BigDecimal calculateFullCreditInsurance(BigDecimal insurance, BigDecimal currentAmount) {
        log.debug("ConveyorServiceImpl.calculateFullCreditInsurance - internal data: insurance {}, currentAmount {}", insurance, currentAmount);

        BigDecimal fullCreditInsurance = insurance.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(currentAmount);

        log.debug("ConveyorServiceImpl.calculateFullCreditInsurance - full credit insurance: {}", fullCreditInsurance);

        return fullCreditInsurance;
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
     *
     * @return полная стоимость кредита
     */
    protected BigDecimal calculatePSK(BigDecimal insurance, BigDecimal currentAmount, BigDecimal currentRate, Integer currentTerm) {
        log.debug("ConveyorServiceImpl.calculatePSK - internal data: insurance {}, currentAmount {}, currentRate {}, currentTerm {}", insurance, currentAmount, currentRate, currentTerm);

        BigDecimal fullCreditInsurance = calculateFullCreditInsurance(insurance, currentAmount);
        BigDecimal totalLoanCost = calculateTotalCreditCost(fullCreditInsurance, currentAmount, currentRate, currentTerm);

        BigDecimal creditYears = BigDecimal.valueOf(currentTerm).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_DOWN);

        BigDecimal psk = totalLoanCost
                .divide(currentAmount, 4, RoundingMode.HALF_DOWN)
                .subtract(BigDecimal.ONE)
                .divide(creditYears, 4, RoundingMode.HALF_DOWN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_DOWN);

        log.debug("ConveyorServiceImpl.calculatePSK - psk: {}", psk);

        return psk;
    }

    /**
     * Рассчет всех выплат (с учетом страховки)
     * <p>
     *
     * @param insuranceCost стоимость страховки для кредита
     * @param currentAmount стоимость кредита
     * @param currentRate   ставка по кредиту
     * @param currentTerm   длительность кредита
     * @return сумма всех выплат
     */
    protected BigDecimal calculateTotalCreditCost(BigDecimal insuranceCost, BigDecimal currentAmount, BigDecimal currentRate, Integer currentTerm) {
        log.debug("ConveyorServiceImpl.calculateTotalCreditCost - internal data: insuranceCost {} currentAmount {}, currentRate {}, currentTerm {}", insuranceCost, currentAmount, currentRate, currentTerm);

        BigDecimal monthlyPayment = calculateAnnuityPayment(currentRate, currentAmount, currentTerm);
        BigDecimal totalCreditCost = monthlyPayment.multiply(BigDecimal.valueOf(currentTerm)).add(insuranceCost);

        log.debug("ConveyorServiceImpl.calculateTotalCreditCost - total credit cost: {}", totalCreditCost);

        return totalCreditCost;
    }

    /**
     * Рассчет графика ежемесячных платежей <p>
     * A - месячный платеж monthlyPayment - дано <p>
     * r - месячная процентная ставка - P/100/12 (1) <p>
     * B - погашение долга interestPayment - A*r (2) <p>
     * C - погашение процентов - A-B (3) <p>
     * D - остаток долга D-C (4)
     * <p>
     *
     * @param monthlyPayment месячный платеж по кредиту
     * @param currentAmount  стоимость кредита
     * @param currentRate    ставка по кредиту
     * @param currentTerm    длительность кредита
     * @return список платежей
     */
    protected List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal monthlyPayment, BigDecimal currentAmount, BigDecimal currentRate, Integer currentTerm) {
        log.debug("ConveyorServiceImpl.calculatePaymentSchedule - internal data: monthlyPayment {} , currentAmount {}, currentRate {}, currentTerm {}", monthlyPayment, currentAmount, currentRate, currentTerm);

        //Месячная процентная ставка (должна быть в долях, не в процентах)
        BigDecimal rate;

        //погашение долга
        BigDecimal interestPayment;

        // погашение процентов
        BigDecimal debtPayment;

        // остаток долга
        BigDecimal remainingDebt = currentAmount;

        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        for (int i = 0; i < currentTerm; i++) {

            //Считаем месячную процентную ставку в долях (1)
            rate = currentRate.divide(BigDecimal.valueOf(1200), 8, RoundingMode.HALF_DOWN);
            log.debug("ConveyorServiceImpl.calculatePaymentSchedule - rate: {}", rate);

            //Считаем погашение процентов (2)
            debtPayment = remainingDebt.multiply(rate).setScale(2, RoundingMode.HALF_DOWN);
            log.debug("ConveyorServiceImpl.calculatePaymentSchedule - debt payment: {}", debtPayment);

            //Считаем погашение долга (3)
            interestPayment = monthlyPayment.subtract(debtPayment);
            log.debug("ConveyorServiceImpl.calculatePaymentSchedule - interest payment: {}", interestPayment);

            //Считаем остаток долга
            remainingDebt = remainingDebt.subtract(interestPayment);
            log.debug("ConveyorServiceImpl.calculatePaymentSchedule - remaining debt : {}", remainingDebt);

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

        log.debug("ConveyorServiceImpl.calculatePaymentSchedule - payment schedule: {}", paymentSchedule);

        return paymentSchedule;
    }


}
