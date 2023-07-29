package ru.Neoflex.conveyor.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.Service.ConveyorService;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
public class ConveyorController {
    private final ConveyorService conveyorService;

    @PostMapping(value="/offers")
    public ResponseEntity<?> calculationPossibleCreditConditions(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return new ResponseEntity<>(conveyorService.calculationPossibleCreditConditions(loanApplicationRequestDTO) , HttpStatus.OK);
    }

    @PostMapping(value="/calculation")
    public ResponseEntity<?> calculationCreditParameters(@RequestBody ScoringDataDTO scoringDataDTO){
        return new ResponseEntity<>(conveyorService.calculationCreditParameters(scoringDataDTO) , HttpStatus.OK);
    }
}
