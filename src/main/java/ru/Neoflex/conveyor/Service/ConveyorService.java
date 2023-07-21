package ru.Neoflex.conveyor.Service;

import org.springframework.stereotype.Service;
import ru.Neoflex.conveyor.DTO.CreditDTO;
import ru.Neoflex.conveyor.DTO.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.LoanOfferDTO;
import ru.Neoflex.conveyor.DTO.ScoringDataDTO;

import java.util.List;

@Service
public interface ConveyorService {

    List<LoanOfferDTO> calculationPossibleCreditConditions(LoanApplicationRequestDTO loanApplicationRequestDTO);

    CreditDTO calculationCreditParameters(ScoringDataDTO scoringDataDTO);
}
