package ru.Neoflex.conveyor.Service;

import ru.Neoflex.conveyor.DTO.Responce.CreditDTO;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.Responce.LoanOfferDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;

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
