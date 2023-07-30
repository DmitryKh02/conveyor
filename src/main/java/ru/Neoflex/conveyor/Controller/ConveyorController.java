package ru.Neoflex.conveyor.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.Neoflex.conveyor.DTO.Request.LoanApplicationRequestDTO;
import ru.Neoflex.conveyor.DTO.Request.ScoringDataDTO;
import ru.Neoflex.conveyor.DTO.Responce.CreditDTO;
import ru.Neoflex.conveyor.DTO.Responce.LoanOfferDTO;
import ru.Neoflex.conveyor.Service.ConveyorService;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
@Tag(name="Кредитные предложения ")
public class ConveyorController {
    private final ConveyorService conveyorService;

    @PostMapping(value="/offers")
    @Operation(summary="Работа с оферами", description="Прескоринг данных и создание 4 кредитных предложений")
    public ResponseEntity<List<LoanOfferDTO>> calculationPossibleCreditConditions(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return new ResponseEntity<>(conveyorService.calculationPossibleCreditConditions(loanApplicationRequestDTO) , HttpStatus.OK);
    }

    @PostMapping(value="/calculation")
    @Operation(summary="Калькулятор кредита", description="Скоринг данных и создание полностью готового кредитного предложения со всеми расчетами")
    public ResponseEntity<CreditDTO> calculationCreditParameters(@RequestBody ScoringDataDTO scoringDataDTO){
        return new ResponseEntity<>(conveyorService.calculationCreditParameters(scoringDataDTO) , HttpStatus.OK);
    }
}
