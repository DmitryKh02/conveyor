package ru.Neoflex.conveyor.Service.Impl;

import org.springframework.stereotype.Service;
import ru.Neoflex.conveyor.DTO.CreditDTO;
import ru.Neoflex.conveyor.DTO.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.LoanOfferDTO;
import ru.Neoflex.conveyor.DTO.ScoringDataDTO;
import ru.Neoflex.conveyor.Service.ConveyorService;

import java.util.List;

@Service
public class ConveyorServiceImpl implements ConveyorService {


    @Override
    public List<LoanOfferDTO> calculationPossibleCreditConditions(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return null;
    }

    @Override
    public CreditDTO calculationCreditParameters(ScoringDataDTO scoringDataDTO) {
        return null;
    }
}
