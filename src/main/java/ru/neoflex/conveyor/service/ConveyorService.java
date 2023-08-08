package ru.neoflex.conveyor.service;

import ru.neoflex.conveyor.dto.request.LoanApplicationRequestDTO;
import ru.neoflex.conveyor.dto.request.ScoringDataDTO;
import ru.neoflex.conveyor.dto.response.CreditDTO;
import ru.neoflex.conveyor.dto.response.LoanOfferDTO;

import java.util.List;

public interface ConveyorService {

    /**
     * Создание списка предложений по кредитам
     * <p>
     * @param loanApplicationRequestDTO основные данные для предоставления кредита
     * @return 4 варианта кредита от лучшего к худшему
     */
    List<LoanOfferDTO> calculationPossibleCreditConditions(LoanApplicationRequestDTO loanApplicationRequestDTO);


    /**
     * Скоринг данных клиента для рассчета окончательного кредитного предложения
     * <p>
     * @param scoringDataDTO полные данные для получения кредита
     * @return полная информация о кредите
     */
    CreditDTO calculationCreditParameters(ScoringDataDTO scoringDataDTO);
}
