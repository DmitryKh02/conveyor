package ru.Neoflex.conveyor.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.Neoflex.conveyor.DTO.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.ScoringDataDTO;
import ru.Neoflex.conveyor.Service.ConveyorService;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
public class ConveyorController {
    private final ConveyorService conveyorService;

    @PostMapping(name="/offers",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> calculationPossibleCreditConditions(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO){
        return new ResponseEntity<>(conveyorService.calculationPossibleCreditConditions(loanApplicationRequestDTO) , HttpStatus.OK);
    }

    @PostMapping(name="/calculation",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> calculationCreditParameters(@RequestBody ScoringDataDTO scoringDataDTO){
        return new ResponseEntity<>(conveyorService.calculationCreditParameters(scoringDataDTO) , HttpStatus.OK);
    }
}
