package ru.neoflex.conveyor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.conveyor.dto.request.LoanApplicationRequestDTO;
import ru.neoflex.conveyor.dto.request.ScoringDataDTO;
import ru.neoflex.conveyor.dto.response.CreditDTO;
import ru.neoflex.conveyor.dto.response.LoanOfferDTO;
import ru.neoflex.conveyor.service.ConveyorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
@Tag(name = "Кредитные предложения ")
public class ConveyorController {

    private final ConveyorService conveyorService;

    @PostMapping(value = "/offers")
    @Operation(summary = "Работа с оферами", description = "Прескоринг данных и создание 4 кредитных предложений")
    public ResponseEntity<List<LoanOfferDTO>> calculationPossibleCreditConditions(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.trace("/conveyor/offers Request: " + loanApplicationRequestDTO.toString());
        List<LoanOfferDTO> loanOfferDTOS = conveyorService.calculationPossibleCreditConditions(loanApplicationRequestDTO);
        log.trace("/conveyor/offers Response: " + loanOfferDTOS.toString());
        return ResponseEntity.status(HttpStatus.OK).body(loanOfferDTOS);
    }

    @PostMapping(value = "/calculation")
    @Operation(summary = "Калькулятор кредита", description = "Скоринг данных и создание полностью готового кредитного предложения со всеми расчетами")
    public ResponseEntity<CreditDTO> calculationCreditParameters(@RequestBody ScoringDataDTO scoringDataDTO) {
        log.trace("/conveyor/calculation Request: " + scoringDataDTO);
        CreditDTO creditDTO = conveyorService.calculationCreditParameters(scoringDataDTO);
        log.trace("/conveyor/calculation Response: " + creditDTO);
        return ResponseEntity.status(HttpStatus.OK).body(creditDTO);
    }
}
