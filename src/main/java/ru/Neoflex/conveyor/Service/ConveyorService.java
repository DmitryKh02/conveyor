package ru.Neoflex.conveyor.Service;

import org.springframework.stereotype.Service;
import ru.Neoflex.conveyor.DTO.Responce.CreditDTO;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.Responce.LoanOfferDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;

import java.util.List;

@Service
public interface ConveyorService {

    List<LoanOfferDTO> calculationPossibleCreditConditions(LoanApplicationRequestDTO loanApplicationRequestDTO);

    CreditDTO calculationCreditParameters(ScoringDataDTO scoringDataDTO);
}
