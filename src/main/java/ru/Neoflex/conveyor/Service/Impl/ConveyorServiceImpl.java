package ru.Neoflex.conveyor.Service.Impl;

import org.springframework.stereotype.Service;
import ru.Neoflex.conveyor.DTO.Responce.CreditDTO;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.Responce.LoanOfferDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.Service.ConveyorService;
import ru.Neoflex.conveyor.Utils.PreScoring;

import java.util.List;

@Service
public class ConveyorServiceImpl implements ConveyorService {
    @Override
    public List<LoanOfferDTO> calculationPossibleCreditConditions(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        boolean isValidBirthday = PreScoring.isValidBirthday(loanApplicationRequestDTO.birthdate());

        return null;
    }

    @Override
    public CreditDTO calculationCreditParameters(ScoringDataDTO scoringDataDTO) {
        return null;
    }
}
